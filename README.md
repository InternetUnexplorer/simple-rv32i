# simple-rv32i

This is an (extremely WIP) RV32I CPU written in Chisel, my first real hardware design project.
The code here is a bit messy and not really documented, since I'm still in the prototyping phase. 

Since I was completely new to RISC-V and processor design in general when I started this project, many of the design decisions here don't make much sense.
I'm currently working on a new revision that (among other things) implements traps correctly, moves the memory and other peripherals out of the CPU design, and supports the [RVFI](https://github.com/SymbioticEDA/riscv-formal).
It probably won't be ready for a bit, as I'm quite busy at the moment.
