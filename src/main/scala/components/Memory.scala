package components

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import types.Instruction

object MemoryOp extends ChiselEnum {
  val NOP = Value
  val LB  = Value
  val LH  = Value
  val LW  = Value
  val LBU = Value
  val LHU = Value
  val SB  = Value
  val SH  = Value
  val SW  = Value
}

class MemoryIO extends Bundle {
  // Instruction fetch, can only read from ROM
  val instrAddr = Input(UInt(32.W))
  val instrData = Output(new Instruction)

  // Program memory access, can read/write RAM and read ROM
  val memOp   = Input(MemoryOp())
  val memAddr = Input(UInt(32.W))
  val memIn   = Input(UInt(32.W))
  val memOut  = Output(UInt(32.W))
}

case class MemoryParams(
    romData: Seq[UInt],
    ramSize: Long = 4096L,
    ramStart: Long = 0x80000000L
)

class Memory(params: MemoryParams) extends Module {
  import components.MemoryOp._

  require(params.ramSize  % 4 == 0, "RAM size must be a multiple of 4")
  require(params.ramStart % 4 == 0, "RAM start address must be a multiple of 4")
  require(
    params.romData.length <= params.ramStart,
    "RAM start address overlaps with ROM"
  )

  // //////////////////////////////////////////////

  val io  = IO(new MemoryIO)
  val rom = VecInit(params.romData)
  val ram = Mem(params.ramSize / 4, Vec(4, UInt(8.W)))

  // //////////////////////////////////////////////

  io.instrData := rom(io.instrAddr / 4.U).asTypeOf(new Instruction)

  // //////////////////////////////////////////////

  val romIndex  = io.memAddr / 4.U
  val ramIndex  = (io.memAddr - params.ramStart.U) / 4.U
  val isRamAddr = io.memAddr >= params.ramStart.U

  val isStore = io.memOp === SB | io.memOp === SH | io.memOp === SW
  val isLoad  = !isStore & io.memOp =/= NOP

  io.memOut := 0.U
  when(isLoad) {
    val data = Mux(isRamAddr, ram.read(ramIndex).asUInt, rom(romIndex))
    val mapping = Seq(
      LB.asUInt  -> data(7, 0).asTypeOf(SInt(32.W)).asUInt,
      LH.asUInt  -> data(15, 0).asTypeOf(SInt(32.W)).asUInt,
      LBU.asUInt -> data(7, 0),
      LHU.asUInt -> data(15, 0)
    )
    io.memOut := MuxLookup(io.memOp.asUInt, data, mapping)
  }.elsewhen(isStore & isRamAddr) {
    val writeMaskMapping = Seq(
      SB.asUInt -> "b0001".U,
      SH.asUInt -> "b0011".U
    )
    val mask = MuxLookup(io.memOp.asUInt, "b1111".U, writeMaskMapping).asBools
    ram.write(ramIndex, io.memIn.asTypeOf(Vec(4, UInt(8.W))), mask)
  }
}
