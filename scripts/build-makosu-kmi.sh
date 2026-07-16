#!/usr/bin/env bash
set -euo pipefail

SOURCE_DIR="$(git rev-parse --show-toplevel)"
OUTPUT_DIR="$(realpath -m "${1:-$SOURCE_DIR/out/lkm}")"
DDK_RELEASE="${DDK_RELEASE:-20260313}"
EXPECTED_SIZE="${KSU_EXPECTED_SIZE:-0x0585}"
EXPECTED_HASH="${KSU_EXPECTED_HASH:-19faea4e5ef5db4e8293183b7c98da92e902e13240cb65f829f65aea761619a2}"
MANAGER_PACKAGE="${KSU_MANAGER_PACKAGE:-com.makosu.manager}"
MODULE_SIGN_KEY="${KSU_MODULE_SIGN_KEY:-}"
MODULE_SIGN_CERT="${KSU_MODULE_SIGN_CERT:-}"

if [[ -n "$MODULE_SIGN_KEY" || -n "$MODULE_SIGN_CERT" ]]; then
    if [[ -z "$MODULE_SIGN_KEY" || -z "$MODULE_SIGN_CERT" ]]; then
        echo "KSU_MODULE_SIGN_KEY and KSU_MODULE_SIGN_CERT must be set together" >&2
        exit 1
    fi
    [[ -f "$MODULE_SIGN_KEY" && -f "$MODULE_SIGN_CERT" ]] || {
        echo "LKM signing key and certificate must point to existing files" >&2
        exit 1
    }
    MODULE_SIGN_KEY_DIR="$(cd "$(dirname "$MODULE_SIGN_KEY")" && pwd)"
    MODULE_SIGN_CERT_DIR="$(cd "$(dirname "$MODULE_SIGN_CERT")" && pwd)"
    MODULE_SIGN_KEY_NAME="$(basename "$MODULE_SIGN_KEY")"
    MODULE_SIGN_CERT_NAME="$(basename "$MODULE_SIGN_CERT")"
else
    MODULE_SIGN_KEY_DIR=""
    MODULE_SIGN_CERT_DIR=""
    MODULE_SIGN_KEY_NAME=""
    MODULE_SIGN_CERT_NAME=""
fi

DEFAULT_KMIS=(
    android12-5.10
    android13-5.10
    android13-5.15
    android14-5.15
    android14-6.1
    android15-6.6
    android16-6.12
)

if [[ -n "${KSU_KMIS:-}" ]]; then
    read -r -a KMIS <<< "$KSU_KMIS"
else
    KMIS=("${DEFAULT_KMIS[@]}")
fi

mkdir -p "$OUTPUT_DIR"

for kmi in "${KMIS[@]}"; do
    image="ghcr.io/ylarod/ddk-min:${kmi}-${DDK_RELEASE}"
    echo "==> Building ${kmi} with ${image}"
    docker pull "$image"
    module_sign_key_container=""
    module_sign_cert_container=""
    if [[ -n "$MODULE_SIGN_KEY" ]]; then
        module_sign_key_container="/makosu-signing/key/$MODULE_SIGN_KEY_NAME"
        module_sign_cert_container="/makosu-signing/cert/$MODULE_SIGN_CERT_NAME"
    fi
    docker_args=(
        run --rm --privileged
        -e "KMI=$kmi"
        -e "KSU_EXPECTED_SIZE=$EXPECTED_SIZE"
        -e "KSU_EXPECTED_HASH=$EXPECTED_HASH"
        -e "KSU_MANAGER_PACKAGE=$MANAGER_PACKAGE"
        -e "KSU_MODULE_SIGN_KEY=$module_sign_key_container"
        -e "KSU_MODULE_SIGN_CERT=$module_sign_cert_container"
        -v "$SOURCE_DIR:/workspace"
        -v "$OUTPUT_DIR:/out"
    )
    if [[ -n "$MODULE_SIGN_KEY" ]]; then
        docker_args+=(
            -v "$MODULE_SIGN_KEY_DIR:/makosu-signing/key:ro"
            -v "$MODULE_SIGN_CERT_DIR:/makosu-signing/cert:ro"
        )
    fi
    docker_args+=(
        -w /workspace/kernel
        "$image"
        bash -lc '
            set -euo pipefail
            make clean >/dev/null 2>&1 || true
            CONFIG_KSU=m CC=clang make \
                KSU_EXPECTED_SIZE="$KSU_EXPECTED_SIZE" \
                KSU_EXPECTED_HASH="$KSU_EXPECTED_HASH" \
                KSU_MANAGER_PACKAGE="$KSU_MANAGER_PACKAGE"
            llvm-strip -d kernelsu.ko
            if [[ -n "$KSU_MODULE_SIGN_KEY" || -n "$KSU_MODULE_SIGN_CERT" ]]; then
                if [[ -z "$KSU_MODULE_SIGN_KEY" || -z "$KSU_MODULE_SIGN_CERT" ]]; then
                    echo "KSU_MODULE_SIGN_KEY and KSU_MODULE_SIGN_CERT must be set together" >&2
                    exit 1
                fi
                scripts/sign-file sha256 "$KSU_MODULE_SIGN_KEY" "$KSU_MODULE_SIGN_CERT" kernelsu.ko
            fi
            cp kernelsu.ko "/out/${KMI}_kernelsu.ko"
        '
    )
    docker "${docker_args[@]}"
done

sha256sum "$OUTPUT_DIR"/*_kernelsu.ko
