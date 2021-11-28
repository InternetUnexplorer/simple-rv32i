package components

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object JumpType extends ChiselEnum {
  val None = Value
  val JAL  = Value
  val BEQ  = Value
  val BNE  = Value
  val BLT  = Value
  val BGE  = Value
  val BLTU = Value
  val BGEU = Value
}

class ControlUnitIO extends Bundle {
  val pc  = Input(UInt(32.W))
  val rs1 = Input(UInt(32.W))
  val rs2 = Input(UInt(32.W))

  val jumpAddr = Input(UInt(32.W))
  val jumpType = Input(JumpType())

  val nextPc  = Output(UInt(32.W))
  val pcPlus4 = Output(UInt(32.W))
}

class ControlUnit extends Module {
  val io = IO(new ControlUnitIO)

  val pcPlus4 = io.pc + 4.U

  val rsEQ  = io.rs1 === io.rs2
  val rsLT  = io.rs1.asSInt < io.rs2.asSInt
  val rsLTU = io.rs1 < io.rs2

  import components.JumpType._

  val branchTakenMapping = Seq(
    JAL.asUInt  -> true.B,
    BEQ.asUInt  -> rsEQ,
    BNE.asUInt  -> !rsEQ,
    BLT.asUInt  -> rsLT,
    BGE.asUInt  -> !rsLT,
    BLTU.asUInt -> rsLTU,
    BGEU.asUInt -> !rsLTU
  )
  val branchTaken = MuxLookup(io.jumpType.asUInt, false.B, branchTakenMapping)

  io.nextPc  := Mux(branchTaken, io.jumpAddr, pcPlus4)
  io.pcPlus4 := pcPlus4
}
