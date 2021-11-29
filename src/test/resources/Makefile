SRC_FILES = $(wildcard *.c)

LD_SCRIPT = riscv32-simple.ld
RUNTIME = crt0.s

CC_FLAGS = -march=rv32i -mabi=ilp32 -nostartfiles -nostdlib -nodefaultlibs \
	   -ffreestanding -Wl,-T,$(LD_SCRIPT)
OBJCOPY_FLAGS = -O binary

BIN_FILES = $(SRC_FILES:.c=.bin)

.PHONY: default clean

default: $(BIN_FILES)

%.o: %.c
	$(strip riscv32-none-elf-gcc $(CC_FLAGS) $(RUNTIME) $< -o $@)

%.bin: %.o
	@riscv32-none-elf-objdump -D $<
	$(strip riscv32-none-elf-objcopy $(OBJCOPY_FLAGS) $< $@)

clean:
	rm -rf *.o *.elf *.bin
