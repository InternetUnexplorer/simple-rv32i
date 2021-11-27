package components

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MemoryTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Memory"

  val size = 1024

  def memInit(memory: Memory, values: Seq[UInt] = Seq(0.U)): Unit = {
    for ((value, addr) <- values.zipWithIndex) {
      memWrite(memory, MemoryOp.SW, addr.U, value)
    }
  }

  def memWrite(
      memory: Memory,
      op: MemoryOp.Type,
      addr: UInt,
      in: UInt
  ): Unit = {
    memory.io.op.poke(op)
    memory.io.addr.poke(addr)
    memory.io.in.poke(in)
    memory.clock.step()
  }

  def memRead(
      memory: Memory,
      op: MemoryOp.Type,
      addr: UInt,
      expected: UInt
  ): Unit = {
    memory.io.op.poke(op)
    memory.io.addr.poke(addr)
    memory.clock.step()
    memory.io.out.expect(expected)
  }

  it should "handle masked loads correctly" in {
    test(new Memory(size)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      memInit(c, Seq("b00000000000000001000000010000000".U))

      c.clock.step()
      memRead(c, MemoryOp.LB, 0.U, "b11111111111111111111111110000000".U)
      c.clock.step()
      memRead(c, MemoryOp.LH, 0.U, "b11111111111111111000000010000000".U)
      c.clock.step()
      memRead(c, MemoryOp.LBU, 0.U, "b00000000000000000000000010000000".U)
      c.clock.step()
      memRead(c, MemoryOp.LHU, 0.U, "b00000000000000001000000010000000".U)
    }
  }

  it should "handle reads followed by writes correctly" in {
    test(new Memory(size)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      memInit(c, (0 until size).map(x => x.U))

      for (addr <- 0 until size) {
        memRead(c, MemoryOp.LW, addr.U, addr.U)
      }

      for (addr <- (0 until size).reverse) {
        memRead(c, MemoryOp.LW, addr.U, addr.U)
      }
    }
  }
}
