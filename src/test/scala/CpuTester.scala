import chisel3._
import chiseltest._
import components.MemoryParams
import org.scalatest.flatspec.AnyFlatSpec

import java.nio.{ByteBuffer, ByteOrder}

class CpuTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Cpu"

  def loadProgram(resourcePath: String): Seq[UInt] = {
    val buffer =
      ByteBuffer.wrap(getClass.getResourceAsStream(resourcePath).readAllBytes())
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val words = new Array[Int](buffer.remaining() / 4)
    buffer.asIntBuffer().get(words)
    words.map(x => Integer.toUnsignedLong(x).asUInt(32.W))
  }

  def testCpu(program: Seq[UInt], expectedDebugVal: UInt): TestResult = {
    test(new Cpu(MemoryParams(program, 1024))) { c =>
      while (c.debugIO.debugVal.peek().litValue == 0)
        c.clock.step()
      c.debugIO.debugVal.expect(expectedDebugVal)
    }
  }

  it should "handle LUI correctly" in {
    testCpu(
      Seq(
        "h00000513".U, // ADDI a0, zero, 0x0
        "h00100593".U, // ADDI a1, zero, 0x1
        "habcde5b7".U, // LUI a1, 0xabcde
        "h00000073".U  // ECALL
      ),
      "habcde000".U
    )
  }

  it should "handle AUIPC correctly" in {
    testCpu(
      Seq(
        "h00000513".U, // ADDI a0, zero, 0x0
        "h12345597".U, // AUIPC a1, 0x12345
        "h00000073".U  // ECALL
      ),
      "h12345004".U
    )
  }

  it should "handle JAL correctly" in {
    testCpu(
      Seq(
        "h00000513".U, // ADDI a0, zero, 0x0
        "h008005ef".U, // JAL a1, 0xc
        "h00000073".U, // ECALL
        "hffdff5ef".U  // JAL a1, 0x8
      ),
      "h00000010".U
    )
  }

  it should "run a simple program" in {
    val memoryParams = MemoryParams(loadProgram("/test.bin"), 1024)

    test(new Cpu(memoryParams)) { c =>
      c.clock.setTimeout(1001)
      c.clock.step(1000)
    }
  }
}
