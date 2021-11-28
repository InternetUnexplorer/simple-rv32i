package components

import chisel3._
import types.Instruction

class IMemIO extends Bundle {
  val address     = Input(UInt(32.W))
  val instruction = Output(new Instruction)
}

class IMem(program: Seq[UInt]) extends Module {
  val io     = IO(new IMemIO)
  val memory = VecInit(program)

  io.instruction := memory((io.address >> 2.U).asUInt).asTypeOf(new Instruction)
}
