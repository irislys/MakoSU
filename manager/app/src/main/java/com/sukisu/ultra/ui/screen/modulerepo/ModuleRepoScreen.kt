package com.sukisu.ultra.ui.screen.modulerepo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sukisu.ultra.ui.navigation3.LocalNavigator
import com.sukisu.ultra.ui.navigation3.Route
import com.sukisu.ultra.ui.screen.flash.FlashIt
import com.sukisu.ultra.ui.viewmodel.ModuleRepoViewModel
import com.sukisu.ultra.ui.viewmodel.ModuleViewModel
import com.sukisu.ultra.ui.util.module.fetchModuleDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ModuleRepoScreen() {
    val navigator = LocalNavigator.current
    val viewModel = viewModel<ModuleRepoViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val installedVm = viewModel<ModuleViewModel>()
    val installedUiState by installedVm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.modules.isEmpty()) {
            viewModel.refresh()
        }
        if (installedUiState.moduleList.isEmpty()) {
            installedVm.fetchModuleList()
        }
    }

    val actions = ModuleRepoActions(
        onBack = { navigator.pop() },
        onRefresh = viewModel::refresh,
        onSearchTextChange = viewModel::updateSearchText,
        onClearSearch = { viewModel.updateSearchText("") },
        onSearchStatusChange = viewModel::updateSearchStatus,
        onSetSortOrder = viewModel::setSortOrder,
        onOpenRepoDetail = { module ->
            val downloadUrl = module.latestAsset?.downloadUrl.orEmpty()
            val assetName = module.latestAsset?.name
                ?: downloadUrl.substringAfterLast('/').ifBlank { "${module.moduleId}.zip" }
            val args = RepoModuleArg(
                moduleId = module.moduleId,
                moduleName = module.moduleName,
                authors = module.authors,
                authorsList = module.authorList.map { AuthorArg(it.name, it.link) },
                summary = module.summary,
                latestRelease = module.latestRelease,
                latestReleaseTime = module.latestReleaseTime,
                downloadUrl = downloadUrl,
                url = module.url,
                homepageUrl = module.homepageUrl,
                sourceUrl = module.sourceUrl,
            )
            navigator.push(Route.ModuleRepoDetail(args))
        },
    )

    ModuleRepoScreenMiuix(uiState, actions)
}

@Composable
fun ModuleRepoDetailScreen(module: RepoModuleArg) {
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current
    var readmeHtml by remember(module.moduleId) { mutableStateOf<String?>(null) }
    var readmeLoaded by remember(module.moduleId) { mutableStateOf(false) }
    var detailLoaded by remember(module.moduleId) { mutableStateOf(false) }
    var detailReleases by remember(module.moduleId) { mutableStateOf<List<ReleaseArg>>(emptyList()) }
    var webUrl by remember(module.moduleId) {
        mutableStateOf(module.url.ifBlank { "https://irislys.github.io/MakoSU_ModuleDownload/module/${module.moduleId}/" })
    }
    var sourceUrl by remember(module.moduleId) { mutableStateOf(module.sourceUrl) }
    LaunchedEffect(module.moduleId) {
        if (module.moduleId.isBlank()) {
            readmeLoaded = true
            detailLoaded = true
            return@LaunchedEffect
        }

        val result = withContext(Dispatchers.IO) {
            runCatching { fetchModuleDetail(module.moduleId) }
        }
        result.onSuccess { detail ->
            if (detail != null) {
                readmeHtml = detail.readmeHtml.takeIf { it.isNotBlank() }
                webUrl = detail.url.ifBlank { webUrl }
                sourceUrl = detail.sourceUrl.ifBlank { sourceUrl }
                detailReleases = detail.releases.map { release ->
                    ReleaseArg(
                        tagName = release.tagName,
                        name = release.name,
                        publishedAt = release.publishedAt,
                        assets = release.assets.map { asset ->
                            ReleaseAssetArg(asset.name, asset.downloadUrl, asset.size, asset.downloadCount)
                        },
                        descriptionHTML = release.descriptionHTML,
                    )
                }
            } else {
                readmeHtml = null
                detailReleases = emptyList()
            }
        }.onFailure {
            readmeHtml = null
            detailReleases = emptyList()
        }
        readmeLoaded = true
        detailLoaded = true
    }

    val state = ModuleRepoDetailUiState(
        module = module,
        readmeHtml = readmeHtml,
        readmeLoaded = readmeLoaded,
        detailLoaded = detailLoaded,
        detailReleases = detailReleases,
        webUrl = webUrl,
        sourceUrl = sourceUrl,
    )
    val actions = ModuleRepoDetailActions(
        onBack = { navigator.pop() },
        onOpenWebUrl = { if (webUrl.isNotEmpty()) uriHandler.openUri(webUrl) },
        onOpenUrl = uriHandler::openUri,
        onInstallModule = { uri -> navigator.push(Route.Flash(FlashIt.FlashModules(listOf(uri)))) },
    )

    ModuleRepoDetailScreenMiuix(state, actions)
}
