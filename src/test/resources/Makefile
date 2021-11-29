SRC_FILES = $(wildcard *.c)

CC_FLAGS = -march=rv32i -mabi=ilp32 -nostartfiles -nostdlib crt0.s
OBJCOPY_FLAGS = -O binary

O_FILES = $(SRC_FILES:.c=.o)
BIN_FILES = $(SRC_FILES:.c=.bin)

.PHONY: default clean

default: $(BIN_FILES)

%.o: %.c
	$(strip riscv32-none-elf-gcc $(CC_FLAGS) $< -o $@)

%.bin: %.o
	@riscv32-none-elf-objdump -d $<
	$(strip riscv32-none-elf-objcopy $(OBJCOPY_FLAGS) $< $@)

clean:
	rm -rf *.elf *.bin
