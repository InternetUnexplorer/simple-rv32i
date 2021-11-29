import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import java.nio.{ByteBuffer, ByteOrder}

class CpuTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Cpu"

  def loadProgram(resourcePath: String): Seq[BigInt] = {
    val buffer =
      ByteBuffer.wrap(getClass.getResourceAsStream(resourcePath).readAllBytes())
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val words = new Array[Int](buffer.remaining() / 4)
    buffer.asIntBuffer().get(words)
    words.map(x => BigInt(Integer.toUnsignedLong(x)))
  }

  it should "run a simple program" in {
    val program = "/home/alex/Code/simple-rv32i/src/test/resources/test.hex"
    test(new Cpu(1024, program)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      c.clock.step(128)
    }
  }
}
