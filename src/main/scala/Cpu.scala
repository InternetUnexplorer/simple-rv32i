import Chisel.switch
import chisel3._
import chisel3.util.is
import components._
import types._

class CpuDebugIO extends Bundle {
  val debugVal = Output(UInt(32.W))
}

class Cpu(memoryParams: MemoryParams) extends Module {
  val debugIO = IO(new CpuDebugIO)

  val decoder     = Module(new Decoder)
  val alu         = Module(new Alu)
  val registers   = Module(new RegisterFile)
  val memory      = Module(new Memory(memoryParams))
  val controlUnit = Module(new ControlUnit)

  val pc = RegInit(0.U(32.W))

  val debugVal = RegInit(0.U(32.W))
  debugIO.debugVal := debugVal

  // //////////////////////////////////////////////

  memory.io.instrAddr := pc
  val instruction = memory.io.instrData

  // //////////////////////////////////////////////

  decoder.io.instruction := instruction
  val control = decoder.io.control
  val isECall = control.writeback === Writeback.Env

  // //////////////////////////////////////////////

  // Load the registers specified the instruction.
  // If the instruction is ECALL, load a0 (x10) and a1 (x11) instead.
  registers.io.read1Addr := Mux(isECall, 10.U, instruction.rs1)
  registers.io.read2Addr := Mux(isECall, 11.U, instruction.rs2)

  val rd  = instruction.rd
  val rs1 = registers.io.read1Data
  val rs2 = registers.io.read2Data
  val imm = instruction.imm(control.immFormat).asUInt

  // //////////////////////////////////////////////

  alu.io.op := control.aluOp
  alu.io.a  := Mux(control.aluASrc === AluASrc.Rs1, rs1, pc)
  alu.io.b  := Mux(control.aluBSrc === AluBSrc.Rs2, rs2, imm)

  // //////////////////////////////////////////////

  controlUnit.io.pc  := pc
  controlUnit.io.rs1 := rs1
  controlUnit.io.rs2 := rs2

  controlUnit.io.jumpAddr := alu.io.out
  controlUnit.io.jumpType := control.jumpType

  // //////////////////////////////////////////////

  memory.io.memOp   := control.memoryOp
  memory.io.memAddr := alu.io.out
  memory.io.memIn   := rs2

  // //////////////////////////////////////////////

  registers.io.writeAddr := 0.U
  registers.io.writeData := 0.U

  when(control.writeback === Writeback.Alu) {
    registers.io.writeAddr := rd
    registers.io.writeData := alu.io.out
  }.elsewhen(control.writeback === Writeback.Ctrl) {
    registers.io.writeAddr := rd
    registers.io.writeData := controlUnit.io.pcPlus4
  }.elsewhen(control.writeback === Writeback.Mem) {
    registers.io.writeAddr := rd
    registers.io.writeData := memory.io.memOut
  }.elsewhen(control.writeback === Writeback.Env) {
    switch(rs1) {
      is(0.U) { debugVal := rs2 }
    }
  }

  pc := controlUnit.io.nextPc
}
