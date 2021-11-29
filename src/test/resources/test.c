// $ riscv32-none-elf-as test.s -o test.elf
// $ riscv32-none-elf-objcopy -O binary test.elf test.bin

static inline int __syscall(int fun, int arg) {
  register unsigned a0 asm("a0") = fun;
  register unsigned a1 asm("a1") = arg;
  asm volatile("ecall" : "+r"(a0) : "r"(a0), "r"(a1));
  return a0;
};

void set_debug_value(int value) { __syscall(0, value); }

void main() {
  set_debug_value(64);

  while (1)
    ;
}
