package components

import Chisel.{MuxLookup, switch}
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util.is

object MemoryOp extends ChiselEnum {
  val LB  = Value
  val LH  = Value
  val LW  = Value
  val LBU = Value
  val LHU = Value
  val SB  = Value
  val SH  = Value
  val SW  = Value
  val NOP = Value
}

class MemoryIO extends Bundle {
  val op   = Input(MemoryOp())
  val addr = Input(UInt(32.W))
  val in   = Input(UInt(32.W))
  val out  = Output(UInt(32.W))
}

class Memory(size: Int) extends Module {
  val io     = IO(new MemoryIO)
  val memory = SyncReadMem(size, Vec(4, UInt(8.W)))

  val writeMaskMapping = Seq(
    MemoryOp.SB.asUInt -> "b0001".U,
    MemoryOp.SW.asUInt -> "b0011".U
  )
  val writeMask = MuxLookup(io.op.asUInt, "b1111".U, writeMaskMapping).asBools

  io.out := 0.U

  switch(io.op) {
    is(MemoryOp.SB, MemoryOp.SH, MemoryOp.SW) {
      memory.write(io.addr, io.in.asTypeOf(Vec(4, UInt(8.W))))
    }
    is(MemoryOp.LB) {
      io.out := memory.read(io.addr).asUInt()(7, 0).asTypeOf(SInt(32.W)).asUInt
    }
    is(MemoryOp.LH) {
      io.out := memory.read(io.addr).asUInt()(15, 0).asTypeOf(SInt(32.W)).asUInt
    }
    is(MemoryOp.LW) {
      io.out := memory.read(io.addr).asUInt
    }
    is(MemoryOp.LBU) {
      io.out := memory.read(io.addr).asTypeOf(UInt(8.W))
    }
    is(MemoryOp.LHU) {
      io.out := memory.read(io.addr).asTypeOf(UInt(16.W))
    }
  }
}
