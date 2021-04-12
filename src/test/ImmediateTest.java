package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.*;

import app.*;

/**
 * Tests the instruction class, namely the immediate generation of the instruction class
 */
@RunWith(Parameterized.class)
public class ImmediateTest {

	/**
	 * Here, we define all of our test input parameters and number them accordingly
	 */
	@Parameterized.Parameter(0)
	public int input;
	@Parameterized.Parameter(1)
	public int B;
	@Parameterized.Parameter(2)
	public int I;
	@Parameterized.Parameter(3)
	public int J;
	@Parameterized.Parameter(4)
	public int U;
	@Parameterized.Parameter(5)
	public int S;

	/**
	 * Here, we define our datasets. It is described as a 2D-array, with each row corresponding to a test
	 * and each column holding the test data of that kind
	 * Below test data is generated via a Chisel tester (who would've known, writing the imm-generation code)
	 * as hardware was actually easier
	 * @return The test data used by JUnit
	 */
	@Parameterized.Parameters
	public static Collection<Object[]> testData() {
		Object[][] data = new Object[][] {
			{
				0xc6318c63,
				0xfffff478,
				0xfffffc63,
				0xfff18c62,
				0xc6318000,
				0xfffffc78
			}, {
				0x39ce739c,
				0xb86,
				0x39c,
				0xe739c,
				0x39ce7000,
				0x387
			}, {
				0xffffffff,
				0xfffffffe,
				0xffffffff,
				0xfffffffe,
				0xfffff000,
				0xffffffff
			}, {
				0x0,
				0x0,
				0x0,
				0x0,
				0x0,
				0x0
			}, {
				0x7fffffff,
				0xffe,
				0x7ff,
				0xffffe,
				0x7ffff000,
				0x7ff
			}, {
				0x4c70f07d,
				0x4c0,
				0x4c7,
				0xfcc6,
				0x4c70f000,
				0x4c0
			}
		};
		return Arrays.asList(data);
	}

	/**
	 * Tests whether immediate generation works as expected. 
	 */
	@Test
	public void testImmediateGeneration() {
		int Jtype = 0x6f;
		int Utype1 = 0x37;
		int Utype2 = 0x17;
		int Itype1 = 0x03;
		int Itype2 = 0x67;
		int Itype3 = 0x13;
		int Stype = 0x23;
		int Btype = 0x63;
		
		//All I-type and U-type opcodes have been tested, but only one is included below for simplicitys sake
		int[] Types = {
			Btype,
			Itype1,
			Jtype,
			Utype1,
			Stype
		};

		//Generate our instructions for testing, one for each type of opcode
		Instruction insts[] = new Instruction[5];
		for(int i=0; i<insts.length; i++) {
			insts[i] = new Instruction(((input & ~0x7f)) | Types[i]);
		}

		//Test each generated immediate vs the expected immediate
		assertEquals(B, insts[0].imm);
		assertEquals(I, insts[1].imm);
		assertEquals(J, insts[2].imm);
		assertEquals(U, insts[3].imm);
		assertEquals(S, insts[4].imm);
	}
}