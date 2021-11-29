# $ riscv32-none-elf-as test.s -o test.elf
# $ riscv32-none-elf-objcopy -O binary test.elf test.bin

    lw s0, 0(zero)
loop:
    j loop
