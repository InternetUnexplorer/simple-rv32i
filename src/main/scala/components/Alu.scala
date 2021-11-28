package components

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object AluOp extends ChiselEnum {
  val ADD   = Value
  val SUB   = Value
  val AND   = Value
  val OR    = Value
  val XOR   = Value
  val SLL   = Value
  val SRL   = Value
  val SLT   = Value
  val SLTU  = Value
  val SRA   = Value
  val COPYB = Value
  val Any   = ADD
}

class AluIO extends Bundle {
  val a   = Input(UInt(32.W))
  val b   = Input(UInt(32.W))
  val op  = Input(AluOp())
  val out = Output(UInt(32.W))
}

class Alu extends Module {
  val io = IO(new AluIO)

  val shamt = io.b(4, 0).asUInt

  val mapping = Seq(
    AluOp.ADD.asUInt  -> (io.a + io.b),
    AluOp.SUB.asUInt  -> (io.a - io.b),
    AluOp.AND.asUInt  -> (io.a & io.b),
    AluOp.OR.asUInt   -> (io.a | io.b),
    AluOp.XOR.asUInt  -> (io.a ^ io.b),
    AluOp.SLL.asUInt  -> (io.a << shamt),
    AluOp.SRL.asUInt  -> (io.a >> shamt),
    AluOp.SRA.asUInt  -> (io.a.asSInt >> shamt).asUInt,
    AluOp.SLT.asUInt  -> (io.a.asSInt < io.b.asSInt),
    AluOp.SLTU.asUInt -> (io.a < io.b)
  )

  io.out := MuxLookup(io.op.asUInt, io.b, mapping)
}
