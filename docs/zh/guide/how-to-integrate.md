# MakoSU 集成指导

MakoSU 可以集成到 GKI 和 non-GKI 内核中，但 non-GKI 必须针对设备源码单独适配，项目不会提供可通吃所有厂商内核的启动镜像。

<!-- 应该是 3.4 版本，但 backslashxx 的 syscall manual hook 无法在 SukiSU 中使用-->

有些 OEM 定制可能导致多达 50% 的内核代码超出内核树代码，而非来自上游 Linux 内核或 ACK。因此，non-GKI 内核的定制特性导致了严重的内核碎片化，而且我们缺乏构建它们的通用方法。因此，我们无法提供 non-GKI 内核的启动映像。

前提条件：开源的、可启动的内核。

## Hook 方法

1. **KPROBES Hook：**

   - 当前 MakoSU 的默认 Hook 路径。
   - 需要 `CONFIG_KPROBES=y` 和 `CONFIG_EXT4_FS=y`。
   - 可编译为 LKM（`CONFIG_KSU=m`）或内置到内核（`CONFIG_KSU=y`）。

2. **源码内置/手动适配：**

   <!-- - backslashxx's syscall manual hook: https://github.com/backslashxx/KernelSU/issues/5 (v1.5 version is not available at the moment, if you want to use it, please use v1.4 version, or standard KernelSU hooks)-->

   - 当前 MakoSU 不定义 `CONFIG_KSU_MANUAL_HOOK` 或 `CONFIG_KSU_TRACEPOINT_HOOK`，不要在配置中添加这两个旧选项。
   - non-GKI 需要把 `kernel/` 集成到设备内核源码，并根据厂商 API、符号和 Hook 能力适配。

3. **Tracepoint 能力：**

   - 是否可用由内核的 `CONFIG_TRACEPOINTS`、`CONFIG_HAVE_SYSCALL_TRACEPOINTS` 和架构决定。
   - 具体符号和厂商实现不一致时，必须以目标内核的构建结果为准。
   
<!-- This part refer to [rsuntk/KernelSU](https://github.com/rsuntk/KernelSU). -->

如果您能够构建可启动内核，有两种方法可以将 KernelSU 集成到内核源代码中：

1. 使用 `kprobe` 自动集成
2. 手动集成

## 与 kprobe 集成

适用：具备 `CONFIG_KPROBES=y` 且相关符号可用的 GKI 或 non-GKI 内核。

KernelSU 使用 kprobe 机制来做内核的相关 hook，如果 _kprobe_ 可以在你编译的内核中正常运行，那么推荐用这个方法来集成。

如果目标内核不提供所需 Kprobes/符号，请改用源码内置方式并逐项适配；不要仅凭主版本号强行加载 LKM。

从内核源码树执行以下命令，将当前 MakoSU 集成脚本下载并运行：

```sh
curl -LSs "https://raw.githubusercontent.com/Spring-bulid/MakoSU/main/kernel/setup.sh" | bash
```

## 手动修改内核源代码

适用：

- GKI 内核
- non-GKI 内核

请先准备可启动的设备内核源码，再运行上面的脚本。脚本默认克隆 MakoSU；可通过 `KSU_REPO` 和 `KSU_SOURCE_DIR` 覆盖仓库与本地路径。

### GKI 内核

```sh [bash]
curl -LSs "https://raw.githubusercontent.com/Spring-bulid/MakoSU/main/kernel/setup.sh" | bash
```

### Built-in 内核

```sh [bash]
curl -LSs "https://raw.githubusercontent.com/Spring-bulid/MakoSU/main/kernel/setup.sh" | KSU_SOURCE_DIR=/path/to/MakoSU bash
```

`CONFIG_KSU=m` 适合生成与目标内核完全匹配的 LKM；`CONFIG_KSU=y` 适合 non-GKI 的源码内置场景。两者都必须使用目标设备自己的 `.config`、符号和编译器验证。
