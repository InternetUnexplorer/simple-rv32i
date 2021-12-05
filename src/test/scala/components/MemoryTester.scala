package components

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MemoryTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Memory"

  val ramSize  = 4096L
  val ramStart = 0x80000000L

  def params(romData: Seq[UInt] = Seq(0.U)): MemoryParams =
    MemoryParams(romData, ramSize, ramStart)

  def ramInit(memory: Memory, values: Seq[UInt] = Seq(0.U)): Unit =
    for ((value, index) <- values.zipWithIndex)
      memWrite(memory, MemoryOp.SW, (index * 4 + ramStart).U, value)

  def memWrite(
      memory: Memory,
      op: MemoryOp.Type,
      addr: UInt,
      in: UInt
  ): Unit = {
    memory.io.memOp.poke(op)
    memory.io.memAddr.poke(addr)
    memory.io.memIn.poke(in)
    memory.clock.step()
  }

  def instrRead(memory: Memory, addr: UInt, expected: UInt): Unit = {
    memory.io.instrAddr.poke(addr)
    memory.io.instrData.asUInt.expect(expected)
  }

  def memRead(
      memory: Memory,
      op: MemoryOp.Type,
      addr: UInt,
      expected: UInt
  ): Unit = {
    memory.io.memOp.poke(op)
    memory.io.memAddr.poke(addr)
    memory.io.memOut.expect(expected)
    memory.clock.step()
  }

  it should "handle reads from ROM correctly" in {
    val romData = (0 until 128).map(x => x.U)
    test(new Memory(params(romData))) { c =>
      for ((value, index) <- romData.zipWithIndex) {
        instrRead(c, (index * 4).U, value)
        memRead(c, MemoryOp.LW, (index * 4).U, value)
      }
    }
  }

  it should "ignore stores to ROM" in {
    test(new Memory(params(Seq(0.U)))) { c =>
      memWrite(c, MemoryOp.SW, 0.U, 1.U)
      memRead(c, MemoryOp.LW, 0.U, 0.U)
    }
  }

  it should "handle masked loads correctly" in {
    val romData = Seq("h80808080".U)
    val ramData = Seq("h80808080".U)
    test(new Memory(params(romData))) { c =>
      ramInit(c, ramData)
      for (addr <- Seq(0.U, ramStart.U)) {
        memRead(c, MemoryOp.LB, addr, "hffffff80".U)
        memRead(c, MemoryOp.LH, addr, "hffff8080".U)
        memRead(c, MemoryOp.LBU, addr, "h00000080".U)
        memRead(c, MemoryOp.LHU, addr, "h00008080".U)
      }
    }
  }

  it should "handle masked stores correctly" in {
    test(new Memory(params())) { c =>
      ramInit(c, Seq("hffffffff".U))
      memWrite(c, MemoryOp.SB, ramStart.U, 0.U)
      memRead(c, MemoryOp.LW, ramStart.U, "hffffff00".U)
      memWrite(c, MemoryOp.SH, ramStart.U, 0.U)
      memRead(c, MemoryOp.LW, ramStart.U, "hffff0000".U)
    }
  }
}
