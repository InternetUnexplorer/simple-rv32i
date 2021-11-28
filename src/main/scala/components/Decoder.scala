package components

import chisel3._
import chisel3.util._
import types._

class DecoderIO extends Bundle {
  val instruction = Input(new Instruction())
  val control     = Output(new ControlSignal())
}

class Decoder extends Module {
  val io = IO(new DecoderIO)

  val O = AluOp
  val A = AluASrc
  val B = AluBSrc
  val M = MemoryOp
  val W = Writeback
  val J = JumpType
  val I = ImmFormat

  import types.InstructionPat._

  /* format: off */
  val mapping = Array(
    LUI   -> List(O.COPYB, A.Any, B.Imm, M.NOP, W.Alu,  J.None, I.UType),
    AUIPC -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.Alu,  J.None, I.UType),
    
    JAL   -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.Ctrl, J.JAL,  I.JType),
    JALR  -> List(O.ADD,   A.Rs1, B.Imm, M.NOP, W.Ctrl, J.JAL,  I.IType),
    
    BEQ   -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.None, J.BEQ,  I.BType),
    BNE   -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.None, J.BNE,  I.BType),
    BLT   -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.None, J.BLT,  I.BType),
    BGE   -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.None, J.BGE,  I.BType),
    BLTU  -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.None, J.BLTU, I.BType),
    BGEU  -> List(O.ADD,   A.Pc,  B.Imm, M.NOP, W.None, J.BGEU, I.BType),
    
    LB    -> List(O.ADD,   A.Rs1, B.Imm, M.LB,  W.Mem,  J.None, I.IType),
    LH    -> List(O.ADD,   A.Rs1, B.Imm, M.LH,  W.Mem,  J.None, I.IType),
    LW    -> List(O.ADD,   A.Rs1, B.Imm, M.LW,  W.Mem,  J.None, I.IType),
    LBU   -> List(O.ADD,   A.Rs1, B.Imm, M.LBU, W.Mem,  J.None, I.IType),
    LHU   -> List(O.ADD,   A.Rs1, B.Imm, M.LHU, W.Mem,  J.None, I.IType),
    
    SB    -> List(O.ADD,   A.Rs1, B.Imm, M.SB,  W.None, J.None, I.SType),
    SH    -> List(O.ADD,   A.Rs1, B.Imm, M.SH,  W.None, J.None, I.SType),
    SW    -> List(O.ADD,   A.Rs1, B.Imm, M.SW,  W.None, J.None, I.SType),
    
    ADDI  -> List(O.ADD,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    SLTI  -> List(O.SLT,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    SLTIU -> List(O.SLTU,  A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    XORI  -> List(O.XOR,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    ORI   -> List(O.OR,    A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    ANDI  -> List(O.AND,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    SLLI  -> List(O.SLL,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    SRLI  -> List(O.SRL,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    SRAI  -> List(O.SRA,   A.Rs1, B.Imm, M.NOP, W.Alu,  J.None, I.IType),
    
    ADD   -> List(O.ADD,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    SUB   -> List(O.SUB,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    SLL   -> List(O.SLL,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    SLT   -> List(O.SLT,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    SLTU  -> List(O.SLTU,  A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    XOR   -> List(O.XOR,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    SRL   -> List(O.SRL,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    SRA   -> List(O.SRA,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    OR    -> List(O.OR,    A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType),
    AND   -> List(O.AND,   A.Rs1, B.Rs2, M.NOP, W.Alu,  J.None, I.SType)
  )
  /* format: on */

  val default = List(O.Any, A.Any, B.Any, M.NOP, W.None, J.None, I.Any)
  val control = ListLookup(io.instruction.instruction, default, mapping)

  io.control.aluOp     := control(0)
  io.control.aluASrc   := control(1)
  io.control.aluBSrc   := control(2)
  io.control.memoryOp  := control(3)
  io.control.writeback := control(4)
  io.control.jumpType  := control(5)
  io.control.immFormat := control(6)
}
