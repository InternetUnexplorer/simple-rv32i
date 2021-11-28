package types

import chisel3._
import chisel3.experimental.ChiselEnum
import components.{AluOp, JumpType, MemoryOp}

class ControlSignal extends Bundle {
  val aluOp     = AluOp()
  val aluASrc   = AluASrc()
  val aluBSrc   = AluBSrc()
  val memoryOp  = MemoryOp()
  val writeback = Writeback()
  val jumpType  = JumpType()
  val immFormat = ImmFormat()
}

object AluASrc extends ChiselEnum {
  val Rs1 = Value
  val Pc  = Value
  val Any = Rs1
}

object AluBSrc extends ChiselEnum {
  val Rs2 = Value
  val Imm = Value
  val Any = Rs2
}

object Writeback extends ChiselEnum {
  val None = Value
  val Alu  = Value
  val Mem  = Value
  val Ctrl = Value
}

object ImmFormat extends ChiselEnum {
  val IType = Value
  val SType = Value
  val BType = Value
  val UType = Value
  val JType = Value
  val Any   = IType
}
