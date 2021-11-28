package components

import org.scalatest.flatspec.AnyFlatSpec
import chisel3._
import chiseltest._

import scala.util.Random

class AluTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Alu"

  it should "add correctly" in {
    test(new Alu()).withAnnotations(
      Seq(WriteVcdAnnotation)
    ) { c =>
      val aValues = Seq.fill(10)(Random.nextInt(Integer.MAX_VALUE))
      val bValues = Seq.fill(10)(Random.nextInt(Integer.MAX_VALUE))

      for (a <- aValues)
        for (b <- bValues) {
          c.io.a.poke(a.U(32.W))
          c.io.b.poke(b.U(32.W))
          c.clock.step()
          c.io.out.expect((a.toLong + b.toLong).U)
        }
    }
  }

  it should "handle overflows correctly when adding" in {
    test(new Alu()).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      c.io.a.poke(4294967295L.U)
      c.io.b.poke(1.U)
      c.clock.step()
      c.io.out.expect(0.U)
    }
  }
}
