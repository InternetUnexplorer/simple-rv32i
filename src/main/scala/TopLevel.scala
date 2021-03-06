import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util.Counter

// We don't need a reset, so we make `TopLevel` a `RawModule` and define the
// clock explicitly to avoid an implicit reset.

class TopLevel extends RawModule {
  val io = IO(new Bundle {
    val clock = Input(Clock())

    val led1 = Output(Bool())
    val led2 = Output(Bool())
    val led3 = Output(Bool())
    val led4 = Output(Bool())
    val led5 = Output(Bool())
  })

  withClockAndReset(io.clock, false.B) {
    val CLOCK_HZ     = 12_000_000 // 12 MHz
    val (counter, _) = Counter(0 until CLOCK_HZ)

    io.led1 := false.B
    io.led2 := false.B
    io.led3 := false.B
    io.led4 := false.B
    io.led5 := (counter >= (CLOCK_HZ / 2).U)
  }
}

// This part generates the Verilog.

object TopLevel extends App {
  (new ChiselStage).emitVerilog(new TopLevel, args)
}
