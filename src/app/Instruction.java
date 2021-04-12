package app;

public class Instruction {

	public int source; //Instruction source in numeric format
	public int opcode; //Instruction opcode in numeric format
	public int funct3;
	public int funct7;
	public int rd;
	public int rs1;
	public int rs2;
	public int imm;
	public RiscvOp op; //Instruction opcode in Enum format

	//Shorthands for accessing each type of opcode defined in RV32I
	public static final int Jtype = 	0x6f;
	public static final int Utype1 = 0x37;
	public static final int Utype2 = 0x17;
	public static final int Itype1 = 0x03;
	public static final int Itype2 = 0x67;
	public static final int ItypeRtype = 0x13; //This one is used for both Itype and Rtype instructions ...
	public static final int Stype =	0x23;
	public static final int Btype =	0x63;
	public static final int Rtype =	0x33;

	public Instruction() {
		updateFields(0);
	}

	public Instruction(int source) {
		updateFields(source);
	}

	public void updateFields(int source) {
		this.source = source;
		opcode = extractBitField(source, 6, 0); //opcode = instruction[6:0]
		rd = extractBitField(source, 11, 7); //rd = instruction[11:7]
		rs1 = extractBitField(source, 19, 15); //rs1 = instruction[19:15]
		rs2 = extractBitField(source, 24, 20); //rs2 = instruction[24:20]
		funct3 = extractBitField(source, 14, 12); //funct3 = instruction[14:12]
		funct7 = extractBitField(source, 31, 25); //funct7 = instruction[31:25], no need to mask
		calculateOp();
		calculateImmediate(source, opcode); //Immediate generation takes more logic
	}

	/**
	 * Calculates the immediate for the current instruction, as defined by the opcode
	 * @param inst The 32-bit instruction that the machine is about to execute
	 * @param opcode The opcode of the current instruction
	 * @return The immediate field for the current instruction, returns 0 for R-type instructions
	 */
	private void calculateImmediate(int inst, int opcode) {
		this.imm = 0;
		switch (opcode) {
			case 0x6f: //J-type instruction
				// System.out.printf("J-type");
				if(extractBitField(inst, 31, 31) != 0) { //Set imm[31:20] by sign-extending inst[31]
					imm = setBitfield(imm, 0xfff, 20);
				} //otherwise, keep imm[31:20] equal to 0x000
				imm = setBitfield(imm, extractBitField(inst, 19, 12), 12); 	//imm[19:12] = inst[19:12]
				imm = setBitfield(imm, extractBitField(inst, 20, 20), 11); 	//imm[11] = inst[20]
				imm = setBitfield(imm, extractBitField(inst, 30, 21), 1); 	//imm[10:1] = inst[30:21]
				//Keeps imm[0] at 0 to allow half-word jumps
				break;
			case 0x37:
			case 0x17: //U-type
				// System.out.printf("U-type");
				imm = setBitfield(imm, extractBitField(inst, 31, 12), 12); 	//imm[31:12] = inst[31:12]
				break;
			case 0x03:
			case 0x67:
			case 0x13: //I-type
				// System.out.printf("I-type");
				if(extractBitField(inst, 31, 31) != 0) { //Set imm[31:12] by sign-extending inst[31]
					imm = setBitfield(imm, 0xfffff, 12);
				} //otherwise, keep imm[31:12] equal to 0x00000
				imm = setBitfield(imm, extractBitField(inst, 31, 20), 0); //imm[11:0] = inst[31:20]
				break;
			case 0x23: //S-type
				// System.out.printf("S-type");
				if(extractBitField(inst, 31, 31) != 0) { //Set imm[31:12] by sign-extending inst[31]
					imm = setBitfield(imm, 0xfffff, 12);
				}
				imm = setBitfield(imm, extractBitField(inst, 31, 25), 5); 	//imm[11:5] = inst[31:25]
				imm = setBitfield(imm, extractBitField(inst, 11, 7), 0); 	//imm[4:0] = inst[11:7]
				break;
			case 0x63: //B-type
				// System.out.printf("B-type");
				if(extractBitField(inst, 31, 31) != 0) { //Set imm[31:12] by sign-extending inst[31]
					imm = setBitfield(imm, 0xfffff, 12);
				}
				imm = setBitfield(imm, extractBitField(inst, 7, 7), 11); 	//imm[11] = inst[7];
				imm = setBitfield(imm, extractBitField(inst, 30, 25), 5);//imm[10:5] = inst[30:25]
				imm = setBitfield(imm, extractBitField(inst, 11, 8), 1);//imm[4:1] = inst[11:8]
				break;
			default: //R-type
				// System.out.printf("R-type");
				break;
		}
	}

	/**
	 * Determines the operation to perform based on the opcode, funct3 and funct7 fields
	 * @return
	 */
	private void calculateOp() {
		switch(this.opcode) {
			
		case Jtype: 
			this.op = RiscvOp.JAL;
			break;
		
			case Utype1:
			this.op = RiscvOp.LUI;
			break;
		
			case Utype2:
			this.op = RiscvOp.AUIPC;
			break;
		
		case Btype:
		switch(this.funct3) {
			case 0b000:
				this.op = RiscvOp.BEQ;
				break;
			case 0b001:
				this.op = RiscvOp.BNE;
				break;
			case 0b100:
				this.op = RiscvOp.BLT;
				break;
			case 0b101:
				this.op = RiscvOp.BGE;
				break;
			case 0b110:
				op = RiscvOp.BLTU;
				break;
			case 0b111:
				op = RiscvOp.BGEU;
				break;
		}
		break; //Finish B-type nested switch
		
		case Stype:
		switch(this.funct3) {
			case 0b000:
				op = RiscvOp.SB;
				break;
			case 0b001:
				op = RiscvOp.SH;
				break;
			case 0b010:
				op = RiscvOp.SW;
				break;
		}
		break; //End S-type nested switch
		
		case Itype1: //Loads
		switch(this.funct3) {
			case 0b000:
				op = RiscvOp.LB;
				break;
			case 0b001:
				op = RiscvOp.LH;
				break;
			case 0b010:
				op = RiscvOp.LW;
				break;
			case 0b100:
				op = RiscvOp.LBU;
				break;
			case 0b101:
				op = RiscvOp.LHU;
				break;
		}
		break; //End Itype1 nested switch
	
		case Itype2: //Jalr
			op = RiscvOp.JALR;
			break;
		
		case ItypeRtype: //Mixed I-type and R-type, looks mostly like an I-type 
			switch(this.funct3) {
				case 0b000:
					op = RiscvOp.ADDI;
					break;
				case 0b001:
					op = RiscvOp.SLLI;
					break;
				case 0b010:
					op = RiscvOp.SLTI;
					break;
				case 0b011:
					op = RiscvOp.SLTIU;
					break;
				case 0b100:
					op = RiscvOp.XORI;
					break;
				case 0b101:
					if (funct7 > 0) {
						op = RiscvOp.SRAI;
					} else {
						op = RiscvOp.SRLI;
					}
					break;
				case 0b110:
					op = RiscvOp.ORI;
					break;
				case 0b111:
					op = RiscvOp.ANDI;
					break;
			}
			break; //end ItypeRtype nested switch

		case Rtype:
			switch(this.funct3) {
				case 0b000:
					if (this.funct7 > 0) {
						op = RiscvOp.SUB;
					} else {
						op = RiscvOp.ADD;
					}
					break;
				case 0b001:
					op = RiscvOp.SLL;
					break;
				case 0b010:
					op = RiscvOp.SLT;
					break;
				case 0b011:
					op = RiscvOp.SLTU;
					break;
				case 0b100:
					op = RiscvOp.XOR;
					break;
				case 0b101:
					if (this.funct7 > 0) {
						op = RiscvOp.SRA;
					} else {
						op = RiscvOp.SRL;
					}
					break;
				case 0b110:
					op = RiscvOp.OR;
					break;
				case 0b111:
					op = RiscvOp.AND;
					break;
			}
			break; //end Rtype nested switch
		
		case 0x73:
			op = RiscvOp.ECALL;
			break;
		
		default:
			op = RiscvOp.ERROR;
		}
	}
	
	/**
	 * Extracts the bitfield {@code source[high:low]} from an int
	 * @param source The source value to extract a bitfield from
	 * @param high The index of the highest bit to extract, in range [0:31]
	 * @param low The index of the lowest bit to extract, in range [0:31] where low<=high
	 * @return The extracted bitfield, right-shifted such that source[low] is now at position 0
	 */
	private static int extractBitField(int source, int high, int low) {
		if(high < low) {
			return -1;
		}
		int mask = (1 << (high-low+1)) - 1;
		return (source >> low) & mask;
	}

	/**
	 * Sets a bitfield in the source by shifting the {@code bitfield} operand to the left {@code low} times,
	 * using an OR mask to set the values
	 * 
	 * @param source The original value in which to set a bitfield
	 * @param low The index of the lowest bit of the field in the new value
	 * @return The new value
	 */
	public static int setBitfield(int source, int bitfield, int low) {
		return (source | (bitfield << low));
	}

	
	/**
	 * @return The string representation of the instruction
	 */
	@Override
	public String toString() {
		String s;
		s = String.format("" + 
		"opcode: [%d/0x%x]\n" +
		"op: %s\n" +
		"rd: [%d/0x%x]\n" +
		"rs1: [%d/0x%x]\n" +
		"rs2: [%d/0x%x]\n" + 
		"funct3: [%d/0x%x]\n" + 
		"funct7: [%d/0x%x]\n" +
		"imm: [%d/0x%x]\n", opcode, opcode, op.name(),
		 rd, rd, rs1, rs1, rs2, rs2, funct3, funct3, funct7, funct7, imm, imm);
		return s;
	}
}