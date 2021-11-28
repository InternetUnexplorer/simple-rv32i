package types

import chisel3._
import chisel3.util._

class Instruction extends Bundle {
  val instruction = UInt(32.W)

  def rd: UInt  = instruction(11, 7)
  def rs1: UInt = instruction(19, 15)
  def rs2: UInt = instruction(24, 20)

  def imm(format: ImmFormat.Type): SInt = {
    val immIType = instruction(31, 20).asSInt
    val immSType = Cat(instruction(31, 25), instruction(11, 7)).asSInt
    val immBType = Cat(
      instruction(31),
      instruction(7),
      instruction(30, 25),
      instruction(11, 8),
      0.U(1.W)
    ).asSInt
    val immUType = Cat(instruction(31, 12), 0.U(12.W)).asSInt
    val immJType = Cat(
      instruction(31),
      instruction(19, 12),
      instruction(20),
      instruction(30, 25),
      instruction(24, 21),
      0.U(1.W)
    ).asSInt

    val mapping = Seq(
      ImmFormat.SType.asUInt -> immSType,
      ImmFormat.BType.asUInt -> immBType,
      ImmFormat.UType.asUInt -> immUType,
      ImmFormat.JType.asUInt -> immJType
    )

    MuxLookup(format.asUInt, immIType, mapping)
  }
}
