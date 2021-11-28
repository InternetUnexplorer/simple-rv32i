# $ riscv32-none-elf-as test.s -o test.elf
# $ riscv32-none-elf-objcopy -O binary test.elf test.bin

start:
    ori s0,zero,1
    ori s1,zero,2
    sw s0, 0(zero)
    sw s1, 4(zero)
    lw s2, 0(zero)
    lw s2, 4(zero)
loop:
    jal loop
