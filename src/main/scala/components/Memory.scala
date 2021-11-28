package components

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

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
  val op   = Input(MemoryOp())
  val addr = Input(UInt(32.W))
  val in   = Input(UInt(32.W))
  val out  = Output(UInt(32.W))
}

class Memory(size: Int) extends Module {
  require(size % 4 == 0, "memory size must be a multiple of 4")

  val io     = IO(new MemoryIO)
  val memory = SyncReadMem(size >> 2, Vec(4, UInt(8.W)))

  import components.MemoryOp._

  val index = (io.addr >> 2.U).asUInt;

  io.out := DontCare
  when(io.op === SB | io.op === SH | io.op === SW) {
    val writeMaskMapping = Seq(
      SB.asUInt -> "b0001".U,
      SH.asUInt -> "b0011".U
    )
    val mask = MuxLookup(io.op.asUInt, "b1111".U, writeMaskMapping).asBools
    memory.write(index, io.in.asTypeOf(Vec(4, UInt(8.W))), mask)
  }.elsewhen(io.op =/= MemoryOp.NOP) {
    val data = memory.read(index).asUInt
    val mapping = Seq(
      LB.asUInt  -> data(7, 0).asTypeOf(SInt(32.W)).asUInt,
      LH.asUInt  -> data(15, 0).asTypeOf(SInt(32.W)).asUInt,
      LBU.asUInt -> data(7, 0),
      LHU.asUInt -> data(15, 0)
    )
    io.out := MuxLookup(io.op.asUInt, data, mapping)
  }
}
