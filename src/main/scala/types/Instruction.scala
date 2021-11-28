package types

import chisel3._
import chisel3.util._

class Instruction extends Bundle {
  val bits = UInt(32.W)

  def rd  = bits(11, 7)
  def rs1 = bits(19, 15)
  def rs2 = bits(24, 40)

  def imm(format: ImmFormat.Type) = {
    val immIType = bits(31, 20).asSInt
    val immSType = Cat(bits(31, 25), bits(11, 7)).asSInt
    val immBType = Cat(
      bits(31),
      bits(7),
      bits(30, 25),
      bits(11, 8),
      0.U(1.W)
    ).asSInt
    val immUType = Cat(bits(31, 12), 0.U(12.W)).asSInt
    val immJType = Cat(
      bits(31),
      bits(19, 12),
      bits(20),
      bits(30, 25),
      bits(24, 21),
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
