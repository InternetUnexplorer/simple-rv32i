package components

import chisel3.util.MuxLookup
import chisel3._
import chisel3.experimental.ChiselEnum

object AluOp extends ChiselEnum {
  // Addition and subtraction
  val ADD = Value
  val SUB = Value

  // Logic
  val AND = Value
  val OR  = Value
  val XOR = Value

  // Shifts
  val SLL  = Value
  val SRL  = Value
  val SLT  = Value
  val SLTU = Value
  val SRA  = Value
}

class AluIO extends Bundle {
  val a   = Input(UInt(32.W))
  val b   = Input(UInt(32.W))
  val op  = Input(AluOp())
  val out = Output(UInt(32.W))
}

class Alu extends Module {
  val io = IO(new AluIO)

  val shiftAmount = io.b(4, 0).asUInt()

  val lookup =
    Seq(
      AluOp.ADD.asUInt()  -> (io.a + io.b),
      AluOp.SUB.asUInt()  -> (io.a - io.b),
      AluOp.AND.asUInt()  -> (io.a & io.b),
      AluOp.OR.asUInt()   -> (io.a | io.b),
      AluOp.XOR.asUInt()  -> (io.a ^ io.b),
      AluOp.SLL.asUInt()  -> (io.a << shiftAmount),
      AluOp.SRL.asUInt()  -> (io.a >> shiftAmount),
      AluOp.SRA.asUInt()  -> (io.a.asSInt() >> shiftAmount).asUInt(),
      AluOp.SLT.asUInt()  -> (io.a.asSInt() < io.b.asSInt()),
      AluOp.SLTU.asUInt() -> (io.a < io.b)
    )

  io.out := MuxLookup(io.op.asUInt(), 0.U, lookup)
}
