package components

import chisel3._

class RegisterFileIO extends Bundle {
  val read1Addr = Input(UInt(5.W))
  val read1Data = Output(UInt(32.W))
  val read2Addr = Input(UInt(5.W))
  val read2Data = Output(UInt(32.W))

  val writeAddr = Input(UInt(5.W))
  val writeData = Input(UInt(32.W))
}

class RegisterFile extends Module {
  val io        = IO(new RegisterFileIO)
  val registers = Mem(32, UInt(32.W))

  io.read1Data := Mux(io.read1Addr.orR, registers.read(io.read1Addr), 0.U)
  io.read2Data := Mux(io.read2Addr.orR, registers.read(io.read2Addr), 0.U)

  when(io.writeAddr.orR) {
    registers.write(io.writeAddr, io.writeData)
  }
}
