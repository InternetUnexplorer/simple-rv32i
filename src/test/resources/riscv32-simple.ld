OUTPUT_ARCH(riscv)

MEMORY
{
    rom : ORIGIN = 0x00000000, LENGTH = 0x00010000
    ram : ORIGIN = 0x00010000, LENGTH = 1024
}

start = 0x0;

PROVIDE(__stack_top = ORIGIN(ram) + LENGTH(ram));
ENTRY(_start)

SECTIONS
{
    .init : { *(.init*) } > rom
    .text : { *(.text*) } > rom
    .rodata : { *(.rodata*) } > rom
    .bss : { *(.bss*) } > ram
    .data : { *(.data*) } > ram
}
