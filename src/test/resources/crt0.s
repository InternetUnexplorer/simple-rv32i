.section .init, "ax"
.global _start
_start:
    .cfi_startproc
    .cfi_undefined ra
    .option push
    .option norelax
    .option pop
    la sp, __stack_top
    jal zero, main
    .cfi_endproc
    .end
