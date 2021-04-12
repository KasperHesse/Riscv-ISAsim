package app;

public enum RiscvOp {
	LUI, AUIPC, //U-types
	JAL, JALR, //Jumps (Jal=J, Jalr=I)
	BEQ, BNE, BLT, BGE, BLTU, BGEU, //Branches, B-type
	LB, LH, LW, LBU, LHU, //Loads, I-type
	SB, SH, SW, //Stores, S-type
	ADDI, SLTI, SLTIU, XORI, ORI, ANDI, //Immediate arithmetic, I-type
	SLLI, SRLI, SRAI, //Immediate arithmetic, R-type
	ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND, //Arithmetic, R-type
	ECALL,
	ERROR //Used for error handling
}
