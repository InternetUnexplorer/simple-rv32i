let
  pkgs = import <nixpkgs> {
    crossSystem = {
      config = "riscv32-none-elf";
      libc = "newlib";
      gcc.abi = "ilp32";
      gcc.arch = "rv32i";
    };
  };
in pkgs.symlinkJoin {
  name = "riscv32-embedded-ilp32";
  paths = [ pkgs.stdenv.cc pkgs.stdenv.cc.bintools ];
}
