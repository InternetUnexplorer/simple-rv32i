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

  it should "run a simple program" in {
    val memoryParams = MemoryParams(loadProgram("/test.bin"), 1024)

    test(new Cpu(memoryParams)) { c =>
      c.clock.setTimeout(1001)
      c.clock.step(1000)
    }
  }
}
