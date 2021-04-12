package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import app.RiscvOp;
import app.Instruction;

/**
 * Tests whether Opcode generation works as expected
 */
@RunWith(Parameterized.class)
public class OpcodeTest {

	@Parameterized.Parameter(0)
	public int source;
	@Parameterized.Parameter(1)
	public RiscvOp op;

	/**
	 * Tests every single opcode format vs the generated opcode to make sure that part works correctly
	 * @return The data used by JUnit to perform the test
	 */
	@Parameterized.Parameters(name = "{index}: source={0} op={1} ")
	public static Collection<Object[]> testData() {
		Object[][] data = new Object[][] {
			{0x6f, RiscvOp.JAL},
			{0x37, RiscvOp.LUI},
			{0x17, RiscvOp.AUIPC},
			{0x67, RiscvOp.JALR},
			{0x63, RiscvOp.BEQ},
			{Instruction.setBitfield(0x63, 0b001, 12), RiscvOp.BNE},
			{Instruction.setBitfield(0x63, 0b100, 12), RiscvOp.BLT },
			{Instruction.setBitfield(0x63, 0b101, 12), RiscvOp.BGE},
			{Instruction.setBitfield(0x63, 0b110, 12), RiscvOp.BLTU},
			{Instruction.setBitfield(0x63, 0b111, 12), RiscvOp.BGEU},
			{0x03, RiscvOp.LB},
			{Instruction.setBitfield(0x03, 0b001, 12), RiscvOp.LH},
			{Instruction.setBitfield(0x03, 0b010, 12), RiscvOp.LW},
			{Instruction.setBitfield(0x03, 0b100, 12), RiscvOp.LBU},
			{Instruction.setBitfield(0x03, 0b101, 12), RiscvOp.LHU},
			{0x23, RiscvOp.SB},
			{Instruction.setBitfield(0x23, 0b001, 12), RiscvOp.SH},
			{Instruction.setBitfield(0x23, 0b010, 12), RiscvOp.SW},
			{0x13, RiscvOp.ADDI},
			{Instruction.setBitfield(0x13, 0b010, 12), RiscvOp.SLTI},
			{Instruction.setBitfield(0x13, 0b011, 12), RiscvOp.SLTIU},
			{Instruction.setBitfield(0x13, 0b100, 12), RiscvOp.XORI},
			{Instruction.setBitfield(0x13, 0b110, 12), RiscvOp.ORI},
			{Instruction.setBitfield(0x13, 0b111, 12), RiscvOp.ANDI},
			{Instruction.setBitfield(0x13, 0b001, 12), RiscvOp.SLLI},
			{Instruction.setBitfield(0x13, 0b101, 12), RiscvOp.SRLI},
			{Instruction.setBitfield(0x13 | (1 << 30), 0b101, 12), RiscvOp.SRAI},
			{0x33, RiscvOp.ADD},
			{0x33 | (1 << 30), RiscvOp.SUB},
			{0x33 | (1 << 12), RiscvOp.SLL},
			{Instruction.setBitfield(0x33, 0b010, 12), RiscvOp.SLT},
			{Instruction.setBitfield(0x33, 0b011, 12), RiscvOp.SLTU},
			{Instruction.setBitfield(0x33, 0b100, 12), RiscvOp.XOR},
			{Instruction.setBitfield(0x33, 0b101, 12), RiscvOp.SRL},
			{Instruction.setBitfield(0x33 | (1 << 30), 0b101, 12), RiscvOp.SRA},
			{Instruction.setBitfield(0x33, 0b110, 12), RiscvOp.OR},
			{Instruction.setBitfield(0x33, 0b111, 12), RiscvOp.AND},
			{0x73, RiscvOp.ECALL}
		};
		return Arrays.asList(data);
	}

	@Test
	public void testOpGeneration() {
		Instruction inst = new Instruction(source);
		assertEquals(op, inst.op);
	}


	
}
