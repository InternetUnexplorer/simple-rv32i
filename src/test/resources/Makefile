SRC_FILES = $(wildcard *.c)

LD_SCRIPT = riscv32-simple.ld
RUNTIME = crt0.s

CC_FLAGS = -march=rv32i -mabi=ilp32 -nostartfiles \
	   -ffreestanding -Wl,-T,$(LD_SCRIPT) -static-libgcc -lgcc -Os
OBJCOPY_FLAGS = -O binary

BIN_FILES = $(SRC_FILES:.c=.bin)

.PHONY: default clean

default: $(BIN_FILES)

%.o: %.c $(LD_SCRIPT)
	$(strip riscv32-none-elf-gcc $(CC_FLAGS) $(RUNTIME) $< -o $@)

%.bin: %.o
	$(strip riscv32-none-elf-objcopy $(OBJCOPY_FLAGS) $< $@)
	@riscv32-none-elf-objdump -D $<
	@hexyl $@

clean:
	rm -rf *.o *.elf *.bin
