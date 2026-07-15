# Integrate MakoSU

MakoSU can be integrated into GKI and non-GKI kernels, but non-GKI support must be adapted per device source tree. There is no universal boot image for vendor kernels.

<!-- It should be 3.4, but backslashxx's syscall manual hook cannot use in SukiSU-->

Some OEMs' customization could result in as much as 50% of kernel code being out-of-tree code and not from upstream Linux kernels or ACKs. Due to this, the custom nature of _non-GKI_ kernels resulted in significant kernel fragmentation, and we lacked a universal method for building them. Therefore, we cannot provide boot images of _non-GKI_ kernels.

Prerequisites: open source bootable kernel.

### Hook method

1. **KPROBES hook:**

   - Default hook path in the current MakoSU kernel tree.
   - Requires `CONFIG_KPROBES=y` and `CONFIG_EXT4_FS=y`.
   - Use `CONFIG_KSU=m` for an LKM or `CONFIG_KSU=y` for a built-in integration.

2. **Built-in/source adaptation:**

   <!-- - backslashxx's syscall manual hook: https://github.com/backslashxx/KernelSU/issues/5 (v1.5 version is not available at the moment, if you want to use it, please use v1.4 version, or standard KernelSU hooks)-->

   - The current MakoSU tree does not define `CONFIG_KSU_MANUAL_HOOK` or `CONFIG_KSU_TRACEPOINT_HOOK`; do not add these obsolete options.
   - Non-GKI kernels require source integration and vendor-specific adaptation of APIs, symbols, and hook availability.

3. **Tracepoint capability:**

   - Availability depends on `CONFIG_TRACEPOINTS`, `CONFIG_HAVE_SYSCALL_TRACEPOINTS`, and the target architecture.
   - Verify symbols and vendor implementation against the target kernel build.

<!-- This part refer to [rsuntk/KernelSU](https://github.com/rsuntk/KernelSU). -->

If you're able to build a bootable kernel, there are two ways to integrate KernelSU into the kernel source code:

1. Automatically with `kprobe`
2. Manually

## Integrate with kprobe

Applicable to GKI or non-GKI kernels that provide `CONFIG_KPROBES=y` and the required symbols.

KernelSU uses kprobe to do kernel hooks. If kprobe runs well in your kernel, it's recommended to use it this way.

If the target kernel lacks the required Kprobes or symbols, use source integration and adapt each hook; do not force-load an LKM based only on the kernel major version.

From the kernel source tree, run the current MakoSU integration script:

```sh
curl -LSs "https://raw.githubusercontent.com/Spring-bulid/MakoSU/main/kernel/setup.sh" | bash
```

## Manually modify the kernel source

Applicable:

- GKI kernel
- non-GKI kernel

Prepare a bootable device kernel source tree before running the script. It clones MakoSU by default; override `KSU_REPO` and `KSU_SOURCE_DIR` when needed.

### GKI kernel

```sh
curl -LSs "https://raw.githubusercontent.com/Spring-bulid/MakoSU/main/kernel/setup.sh" | bash
```

### Built-in kernel

```sh
curl -LSs "https://raw.githubusercontent.com/Spring-bulid/MakoSU/main/kernel/setup.sh" | KSU_SOURCE_DIR=/path/to/MakoSU bash
```

`CONFIG_KSU=m` is for an LKM that exactly matches the target kernel. `CONFIG_KSU=y` is for built-in non-GKI integration. Both modes require the device's own `.config`, symbols, and toolchain.
