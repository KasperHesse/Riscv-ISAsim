package test;

import static org.junit.Assert.*;
import org.junit.Test;

import app.*;

/**
 * Performs a bunch of memory testing, using load/stores
 */
public class MemTest {

	/**
	 * Tests whether the stack works appropriately, using all types of load/store operations
	 * @throws Exception
	 */
	@Test
	public void testMem() throws Exception {
		// RiscV rv = new RiscV("bin/tests/mem");
		RiscV rv = new RiscV("asm/mem");
		rv.setDebugMode(true);
		rv.run();
		int[] Reg = rv.getReg();
		int[] expected = {
			0x0, 0x0, 0xfffe4, -1,
			-1, -17,-1, 255,
			255, 239, 0xa, -1,
			-1, -4113, -1, 65535,
			65535, 61423, 65535, -1,
			131071, 61423, 65535, 255,
			0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0
		};
		assertArrayEquals(expected, Reg);		
	}
}
