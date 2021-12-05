OUTPUT_ARCH(riscv)

MEMORY
{
    rom (rx)  : ORIGIN = 0x00000000, LENGTH = 2K
    ram (rwx) : ORIGIN = 0x80000000, LENGTH = 4K
}

ENTRY(_start)

SECTIONS
{
    PROVIDE(__stack_top = ORIGIN(ram) + LENGTH(ram));

    .init : { *(SORT_NONE(.init)) } > rom
    .text : { *(.text*) } > rom
    .rodata : { *(.rodata*) } > rom
    .bss : { *(.bss*) } > ram
    .data : { *(.data*) } > ram
    /DISCARD/ : { *(.eh_frame* .debug* .comment .riscv.attributes) }
}
