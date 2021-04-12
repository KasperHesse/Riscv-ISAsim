package test;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.*;
import java.io.*;

import app.*;

/**
 * Tests the instruction class, namely the immediate generation of the instruction class
 */
@RunWith(Parameterized.class)
public class Tasks {

	/**
	 * Here, we define all of our test input parameters and number them accordingly
	 */
	@Parameterized.Parameter(0)
	public int taskNumber;

	/**
	 * Here, we define our datasets. It is described as a 2D-array, with each row corresponding to a test
	 * and each column holding the test data of that kind
	 * @return The test data used by JUnit
	 */
	@Parameterized.Parameters
	public static Collection<Object[]> testData() {
		Object[][] data = new Object[][] {
			{1},
			{2},
			{3},
			{4},
			{5},
			{6},
			{7},
			{8},
			{9},
			{10},
			{11},
			{12},
			{13},
			{14}
		};
		return Arrays.asList(data);
	}

	/**
	 * Tests whether immediate generation works as expected. 
	 */
	@Test
	public void testAllTasks() throws Exception {
		String testName = String.format("bin/tasks/t%d", taskNumber);
		String resName = String.format("bin/tasks/t%d.res", taskNumber);
		RiscV rv = new RiscV(testName);
		rv.setDebugMode(true);
		rv.run();
		int[] actuals = rv.getReg();

		//Load expected values
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(resName)); //By buffering the stream we get better performance
		int[] expected = new int[32]; //Set IM to correct size, divide by 4 since file.length returns bytes
		//Read data from our stream, store in instruction memory
		int i = 0; //Number of operations read
		int j = 0; //Current byte being read
		int read; //Temp value
		while( (read = bis.read()) != -1) { //Read a single byte at a time
			expected[i] |= (read << j*8); //put into array at correct location
			j++; //Increment shamt, or loop back and increment index
			if(j > 3) {
				j = 0;
				i++;
			}
		}
		bis.close();

		//Test each generated immediate vs the expected immediate
		assertArrayEquals(expected, actuals);
	}
}