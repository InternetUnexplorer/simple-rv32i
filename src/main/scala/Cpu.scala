import chisel3._
import chisel3.util._
import components._
import types._

class CpuDebugIO extends Bundle {
  val pc      = Output(UInt(32.W))
  val control = Output(new ControlSignal)
}

class Cpu(program: Seq[UInt], memorySize: Int) extends Module {
  val debugIO = IO(new CpuDebugIO)

  val imem        = Module(new IMem(program))
  val decoder     = Module(new Decoder)
  val alu         = Module(new Alu)
  val registers   = Module(new RegisterFile)
  val memory      = Module(new Memory(memorySize))
  val controlUnit = Module(new ControlUnit)
  val pc          = RegInit(0.U(32.W))
  val stall       = RegInit(false.B)

  // //////////////////////////////////////////////

  imem.io.address := pc
  val instruction = imem.io.instruction

  // //////////////////////////////////////////////

  val control = decoder.io.control

  debugIO.pc      := pc
  debugIO.control := control

  decoder.io.instruction := instruction

  registers.io.read1Addr := instruction.rs1
  registers.io.read2Addr := instruction.rs2

  val rd  = instruction.rd
  val rs1 = registers.io.read1Data
  val rs2 = registers.io.read2Data
  val imm = instruction.imm(control.immFormat).asUInt

  // //////////////////////////////////////////////

  alu.io.op := control.aluOp
  alu.io.a  := Mux(control.aluASrc === AluASrc.Rs1, rs1, pc)
  alu.io.b  := Mux(control.aluBSrc === AluBSrc.Rs2, rs2, imm)

  // //////////////////////////////////////////////

  memory.io.op   := control.memoryOp
  memory.io.addr := Mux(control.memoryOp =/= MemoryOp.NOP, alu.io.out, 0.U)
  memory.io.in   := rs2

  // //////////////////////////////////////////////

  controlUnit.io.pc  := pc
  controlUnit.io.rs1 := rs1
  controlUnit.io.rs2 := rs2

  controlUnit.io.jumpAddr := alu.io.out
  controlUnit.io.jumpType := control.jumpType

  // //////////////////////////////////////////////

  registers.io.writeData := MuxLookup(
    control.writeback.asUInt,
    alu.io.out,
    Seq(
      Writeback.Mem.asUInt  -> memory.io.out,
      Writeback.Ctrl.asUInt -> controlUnit.io.pcPlus4
    )
  )

  // //////////////////////////////////////////////

  when(control.writeback === Writeback.Mem & !stall) {
    registers.io.writeAddr := 0.U

    pc    := pc
    stall := true.B
  }.otherwise {
    registers.io.writeAddr := Mux(control.writeback =/= Writeback.None, rd, 0.U)

    pc    := controlUnit.io.nextPc
    stall := false.B
  }
}
