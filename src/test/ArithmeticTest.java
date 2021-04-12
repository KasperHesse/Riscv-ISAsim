package test;

import static org.junit.Assert.*;
import org.junit.Test;

import app.*;

public class ArithmeticTest {

	/**
	 * Tests whether the "arith" tester works appropriately
	 */
	@Test
	public void testArithmetic() throws Exception {
		// RiscV rv = new RiscV("bin/tests/arith");
		RiscV rv = new RiscV("asm/arith");
		rv.setDebugMode(true);
		rv.run();
		int[] Reg = rv.getReg();
		int[] expected = {
			0x0, 0x64000, 0x0, 0xf,
			0xfffffff0, 0xfffffff7, 0xd0, 0xf0,
			0x80000000, 0x20000000, 0xa, 0x8000000f,
			0xffffffe0, 0xffffff30, 0xfffffff7, 0xc0,
			0xe0000000, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0
		};
		assertArrayEquals(expected, Reg);
	}
}
