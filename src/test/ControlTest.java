package test;

import static org.junit.Assert.*;
import org.junit.Test;

import app.*;

public class ControlTest {

	/**
	 * Tests whether the "loop" tester works appropriately
	 */
	@Test
	public void testLoop() throws Exception {
		RiscV rv = new RiscV("asm/loop");
		rv.setDebugMode(true);
		rv.run();
		int[] Reg = rv.getReg();
		int[] expected = {
			0x0, 0x8, 0x100000, 0x0,
			0x0, 0x0, 0x64, 0x0,
			0x0, 0x0, 0xa, 0x1356,
			0x0, 0xc80, 0x64, 0x1356,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0
		};

		assertArrayEquals(expected, Reg);
	}

	/**
	 * Tests whether jal and jalr instructions work correctly
	 */
	@Test
	public void testJump() throws Exception {
		RiscV rv = new RiscV("asm/jump");
		rv.setDebugMode(true);
		rv.run();
		int[] Reg = rv.getReg();
		int[] expected = {
			0x0, 0x1c, 0x100000, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0xa, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x12, 0x0, 0x10, 0x4,
			0x2, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0
		};

		assertArrayEquals(expected, Reg);		
	}
	
}