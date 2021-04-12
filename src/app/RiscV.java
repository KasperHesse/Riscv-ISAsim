package app;

import java.util.*;
import java.io.*;

public class RiscV {

	// Variables for hardware abstraction
	private int PC; 
	private int PCNext; 
	private int[] reg = new int[32];
	private byte[] mem = new byte[1048576]; 
	private boolean running;
	private int source;
	private Instruction ci; 

	// Variables for user input management
	private Scanner scan;
	private Scanner linescan;
	private boolean verbose;
	private int stepCount;
	private boolean debugMode;
	private ArrayList<Integer> breakpointList;

	/**
	 * Instantiates the RISC-V ISA Simulator by loading in the instructions to operate on
	 * @throws Exception If anything bad happens when loading instructions, @see {@link RiscV#loadInstructionsViaCLI()}
	 */
	public RiscV() throws Exception {
		scan = new Scanner(System.in);
		loadInstructionsViaCLI();
		initialSetup();
	}

	public RiscV(String testName) throws Exception {
		scan = new Scanner(System.in);
		loadInstructionsFromFilename(testName);
		initialSetup();
	}

	private void initialSetup() {
		ci = new Instruction();
		PC = 0;
		running = true;
		stepCount = 0;
		verbose = false;
		breakpointList = new ArrayList<Integer>();
	}

	/**
	 * Run the simulator. This method will execute the instructions located in IM,
	 * until it encounters ECALL 10 (exit)
	 * If debug mode is not enabled, the user is able to enter inputs via {@link #handleUserInput()}
	 */
	public void run() {
		if(!debugMode) {
			//Flush scanner from file input
			if(scan.hasNextLine()) {
				scan.nextLine();
			}
			System.out.printf("Welcome to the RISC-V ISA Simulator. For help, type \"help\"\n");
		}
		while(running) {
			handleUserInput();
			fetch();
			decode();
			exMemWb();
			if(verbose) {
				System.out.printf("op=%s, rd=%d, rs1=%d, rs2=%d, imm=%d\n", ci.op, ci.rd, ci.rs1, ci.rs2, ci.imm);
			}
			
			PC = PCNext;
			if(stepCount > 0) {
				stepCount--;
			}
		}

		scan.close();
		
		if(!debugMode) {
			System.out.printf("\nExecution has finished. Register dump:\n");
			dumpRegisters();
		}

		File regdump = new File("regdump.res");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(regdump);
			for(int i=0; i<32; i++) {
				for(int j=3; j>=0; j--) {
					fos.write(reg[i] >> j*8);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches the next instruction from memory and updates PCNext
	 */
	private void fetch() {
		this.source = mem[PC] & 0xFF;
		this.source |= (mem[PC + 1] & 0xFF) << 8;
		this.source |= (mem[PC + 2] & 0xFF) << 16;
		this.source |= (mem[PC + 3] & 0xFF) << 24;
		PCNext = PC + 4;
	}

	/**
	 * Decodes the current instruction, updating the values of opcode, rd, rs1 etc
	 */
	private void decode() {
		ci.updateFields(source);
	}
	
	/**
	 * Performs the Execute, Memory and Writeback stages of the datapath
	 */
	private void exMemWb() {
		int base;

		switch(ci.op) {
			case ADD:
				regWrite(reg[ci.rs1] + reg[ci.rs2]);
				break;
			case SUB:
				regWrite(reg[ci.rs1] - reg[ci.rs2]);
				break;
			case AND:
				regWrite(reg[ci.rs1] & reg[ci.rs2]);
				break;
			case OR:
				regWrite(reg[ci.rs1] | reg[ci.rs2]);
				break;
			case XOR:
				regWrite(reg[ci.rs1] ^ reg[ci.rs2]);
				break;
			case SLL:
				regWrite(reg[ci.rs1] << reg[ci.rs2]);
				break;
			case SRL: //Triple > gives a logical shift
				regWrite(reg[ci.rs1] >>> reg[ci.rs2]);
				break;
			case SRA:
				regWrite(reg[ci.rs1] >> reg[ci.rs2]);
				break;
			case SLT:
				regWrite(reg[ci.rs1] < reg[ci.rs2] ? 1 : 0);
				break;
			case SLTU:
				regWrite((reg[ci.rs1] < reg[ci.rs2]) ^ (reg[ci.rs1] < 0) ^ (reg[ci.rs2] < 0) ? 1 : 0);
				break;
			case SLLI:
				regWrite(reg[ci.rs1] << ci.rs2);
				break;
			case SRLI:
				regWrite(reg[ci.rs1] >>> ci.rs2);
				break;
			case SRAI:
				regWrite(reg[ci.rs1] >> ci.rs2);
				break;
			case ADDI:
				regWrite(reg[ci.rs1] + ci.imm);
				break;
			case ANDI:
				regWrite(reg[ci.rs1] & ci.imm);
				break;
			case ORI:
				regWrite(reg[ci.rs1] | ci.imm);
				break;
			case XORI:
				regWrite(reg[ci.rs1] ^ ci.imm);
				break;
			case SLTI:
				regWrite(reg[ci.rs1] < ci.imm ? 1 : 0);
				break;
			case SLTIU:
				regWrite((reg[ci.rs1] < ci.imm) ^ (reg[ci.rs1] < 0) ^ (ci.imm < 0) ? 1 : 0);
				break;
			case JAL:
				PCNext = PC + ci.imm;
				regWrite(PC + 4);
				break;
			case JALR:
				PCNext = reg[ci.rs1] + ci.imm;
				regWrite(PC+4);
				break;
			case LUI:
				regWrite(ci.imm);
				break;
			case AUIPC:
				regWrite(PC + ci.imm);
				break;
			case LBU:
				reg[ci.rd] = mem[reg[ci.rs1] + ci.imm] & 0xFF;
				break;
			case LB:
				reg[ci.rd] = mem[reg[ci.rs1] + ci.imm];
				//Sign extend
				if((reg[ci.rd] & 0x80) > 0) {
					reg[ci.rd] = reg[ci.rd] | 0xFFFFFF00;
				}
				break;
			case LHU:
				base = reg[ci.rs1] + ci.imm;
				reg[ci.rd] = (mem[base] & 0xFF) | (mem[base + 1] << 8);
				reg[ci.rd] &= 0xFFFF;
				break;
			case LH:
				base = reg[ci.rs1] + ci.imm;
				reg[ci.rd] = (mem[base] & 0xFF) | (mem[base+1] << 8);
				//Sign extend
				if((reg[ci.rd] & 0x8000) > 0) {
					reg[ci.rd] = reg[ci.rd] | 0xFFFF0000;
				}
				break;
			case LW:
				base = reg[ci.rs1] + ci.imm;
				reg[ci.rd] = mem[base] & 0xFF;
				reg[ci.rd] |= (mem[base + 1] << 8) & 0xFFFF;
				reg[ci.rd] |= (mem[base + 2] << 16) & 0xFFFFFF;
				reg[ci.rd] |= (mem[base + 3] << 24);
				break;
			case SB:
				mem[reg[ci.rs1] + ci.imm] = (byte) reg[ci.rs2];
				break;
			case SH:
				base= reg[ci.rs1] + ci.imm;
				mem[base] = (byte) (reg[ci.rs2]);
				mem[base + 1] = (byte) (reg[ci.rs2] >> 8);
				break;
			case SW:
				base = reg[ci.rs1] + ci.imm;
				mem[base] = (byte) reg[ci.rs2];
				mem[base + 1] = (byte) (reg[ci.rs2] >> 8);
				mem[base + 2] = (byte) (reg[ci.rs2] >> 16);
				mem[base + 3] = (byte) (reg[ci.rs2] >> 24);
				break;
			case BEQ:
				if(reg[ci.rs1] == reg[ci.rs2]) {
					PCNext = PC+ci.imm;
				}
				break;
			case BNE:
				if(reg[ci.rs1] != reg[ci.rs2]) {
					PCNext = PC + ci.imm;
				}
				break;
			case BLT:
				if(reg[ci.rs1] < reg[ci.rs2]) {
					PCNext = PC + ci.imm;
				}
				break;
			case BGE:
				if(reg[ci.rs1] >= reg[ci.rs2]) {
					PCNext = PC + ci.imm;
				}
				break;
			case BLTU:
				if((reg[ci.rs1] < reg[ci.rs2]) ^ (reg[ci.rs1] < 0) ^ (reg[ci.rs2] < 0)) {
					PCNext = PC + ci.imm;
				}
				break;
			case BGEU:
				if(!((reg[ci.rs1] < reg[ci.rs2]) ^ (reg[ci.rs1] < 0) ^ (reg[ci.rs2] < 0))) {
					PCNext = PC + ci.imm;
				}
				break;
			case ECALL:
				if(reg[10] == 10) {
					running = false;
				}
				break;
			default:
				System.out.printf("Opcode %s is not implemented. Are you sure the binary file is correct?\n", Integer.toBinaryString(ci.opcode));
				break;
		}
	}

	
	/**
	 * Takes user input from the console, allowing the user to view registers, memory and PC and set breakpoints.
	 */
	private void handleUserInput() {
		if(breakpointList.contains(Integer.valueOf(PC))) {
			stepCount = 0; //Reset stepcounter
			System.out.printf("Breakpoint encountered at PC %d\n", PC);
		}
		if (stepCount > 0 || debugMode) {
			return;
		}

		inputHandler: while(true) {
			System.out.print("> ");
			String line = scan.nextLine();
			linescan = new Scanner(line);
			if(!linescan.hasNext()) {
				continue;
			}
			String input = linescan.next();

			int index;
			switch (input) {
				case "r":
				case "run":
					stepCount = Integer.MAX_VALUE;
					break inputHandler;

				case "s":
				case "step":
					if(!linescan.hasNext()) {
						stepCount = 1;
						break inputHandler;
					}
					if(!linescan.hasNextInt()) {
						System.out.printf("Unknown input. Please type \"s X\" to step for X lines, or \"s\" to step for one line\n");
					} else {
						stepCount = linescan.nextInt();
						break inputHandler;
					}
					break;

				case "pc":
					System.out.printf("PC: %d\n", PC);
					break;
				
				case "reg":
					if(!linescan.hasNext()) {
						dumpRegisters();
						break;
					}
					if(!linescan.hasNextInt()) {
						System.out.println("Unknown input. Please type \"reg X\" to view register X, or \"reg\" to dump all registers");
						break;
					}
					index = linescan.nextInt();
					if(index < 0 || index > 31) {
						System.out.println("Illegal register index. Legal values are 0-31\n");
					} else {
						System.out.printf("Reg[%2d]=0x%08x\n", index, reg[index]);
					}
					break;
				
				case "memb":
					if(!linescan.hasNextInt()) {
						System.out.println("Unknown input. Please type \"memb X\" to view the byte at memory location X");
						break;
					}
					index = linescan.nextInt();
					if (index < 0 || index >= mem.length) {
						System.out.printf("Illegal memory location. Legal values are 0-%d\n", mem.length-1);
					} else {
						System.out.printf("Mem[%d]=0x%02x\n", index, mem[index]);
					}
					break;

				case "memh":
					if(!linescan.hasNextInt()) {
						System.out.println("Unknown input. Please type \"memh X\" to view the halfword starting at memory location X");
						break;
					}
					index = linescan.nextInt();
					if (index < 0 || index >= mem.length-1) {
						System.out.printf("Illegal memory location. Legal values are 0-%d\n", mem.length-2);
					} else {
						System.out.printf("Mem[%d]=0x%02x%02x\n", index, mem[index+1], mem[index]);
					}
					break;

				case "mem":
				case "memw":
					if(!linescan.hasNextInt()) {
						System.out.println("Unknown input. Please type \"mem X\" to view the word starting at memory location X");
						break;
					}
					index = linescan.nextInt();
					if (index < 0 || index >= mem.length-3) {
						System.out.printf("Illegal memory location. Legal values are 0-%d\n", mem.length-4);
					} else {
						System.out.printf("Mem[%d]=0x%02x%02x%02x%02x\n", index, mem[index+3], mem[index+2], mem[index+1], mem[index]);				
					}
					break;
				
				case "q":
				case "quiet":
					System.out.println("Quiet mode on. Will not print anything");
					this.verbose = false;
					break;
				case "v":
				case "verbose":
					System.out.println("Verbose mode on. Will print every instruction");
					this.verbose = true;
					break;

				case "reset":
					PC = 0;
					reg = new int[32];
					reg[2] = mem.length;
					mem = new byte[1048576];
					verbose = false;
					System.out.println("Simulator state has been reset");
					break;

				case "ba":
				case "badd":
					if(!linescan.hasNextInt()) {
						System.out.println("Unknown input. Please type \"badd X\" to add a breakpoint at instruction address X");
						break;
					}
					index = linescan.nextInt();
					if (index < 0 || index >= mem.length-4) {
						System.out.printf("Illegal memory location. Legal values are 0-%d\n", mem.length-4);
					} else if(!breakpointList.contains(Integer.valueOf(index))) {
						breakpointList.add(Integer.valueOf(index));			
						System.out.println("Breakpoint added. Type \"r\" to run until the breakpoint");	
					} else {
						System.out.printf("A breakpoint is already set at address %d\n", index);
					}
					break;

				case "br":
				case "brem":
					if(!linescan.hasNextInt()) {
						System.out.println("Unknown input. Please type \"brem X\" to remove the breakpoint at address X");
						break;
					}
					index = linescan.nextInt();
					breakpointList.remove(Integer.valueOf(index));
					System.out.println("Breakpoint removed");
					break;
				
				case "bl":
				case "blist":
					System.out.println("The following breakpoints are set:");
					for(int i=0; i<breakpointList.size(); i++) {
						System.out.printf("%3d: (op=%s, rd=%d, rs1=%d, rs2=%d, imm=%d)\n", breakpointList.get(i), ci.op, ci.rd, ci.rs1, ci.rs2, ci.imm);
					}
					break;

				case "bc":
				case "bclear":
					breakpointList.clear();
					System.out.printf("Breakpoints cleared");
					break;
					
				case "h":
				case "help":
					StringBuilder sb = new StringBuilder(1000);
					sb.append("The following instructions are supported by the simulator\n");
					sb.append("  r, run: Execute the remainder of the program. Will stop if a breakpoint is encountered\n");
					sb.append("  s, step [X]: Step for X instructions. If no X is given, steps for 1 instruction\n");
					sb.append("  pc: Print the current value of the program counter\n");
					sb.append("  reg [X]: Print the contents of register X. If no X is specified, prints the contents of all registers\n");
					sb.append("  memb [X]: Print the value of the byte stored at memory location X\n");
					sb.append("  memh [X]: Print the value of the halfword starting at memory location X\n");
					sb.append("  mem, memw [X]: Prints the value of the word starting at memory location X\n");
					sb.append("  ba, badd [X]: Add a breakpoint at address X\n");
					sb.append("  br, brem [X]: Remove a breakpoint at address X\n    If no breakpoint exists at this address, nothing happens\n");
					sb.append("  bl, blist: List all breakpoints\n");
					sb.append("  bc, bclear: Clear all breakpoints\n");
					sb.append("  q, quiet: Toggle quiet mode on\n");
					sb.append("  v, verbose: Toggle verbose mode on\n");
					sb.append("  reset: Reset the state of the simulator. Clears all registers and memory, sets PC=0, sets quiet mode on, sets SP=mem.length. Will not clear breakpoints.\n");
					sb.append("  h, help: Print this help message\n");
					sb.append("  exit: Exit the simulator");
					System.out.println(sb);
					break;


				
				case "exit":
					System.exit(0); //TODO Make a better implementation of this
					break;

				default:
					System.out.println("Unknown command. Type \"help\" for help");
					break;
			} //End switch on input
		} //End while loop
		linescan.close();
	}

	
	/**
	 * Prints the register contents to the command line
	 */
	private void dumpRegisters() {
		for(int i=0; i<32; i+= 4) {
			System.out.printf("Reg[%2d]=0x%08x  Reg[%2d]=0x%08x  Reg[%2d]=0x%08x  Reg[%2d]=0x%08x\n", i, reg[i], i+1, reg[i+1], i+2, reg[i+2], i+3, reg[i+3]);
		}	
	}

	/**
	 * Writes into {@code reg[rd]} as specified by the current intruction. If {@code rd == 0}, nothing is written
	 * @param value The value to write into {@code rd}
	 */
	private void regWrite(int value) {
		if(ci.rd != 0) {
			reg[ci.rd] = value;
		}
	}

	/**
	 * Returns a copy of the register file
	 * @return
	 */
	public int[] getReg() {
		return reg.clone();
	}

	public void setDebugMode(boolean mode) {
		this.debugMode = mode;
	}

	/**
	 * Loads the instructions into the simulator by prompting the user for a file
	 * stored in "src/tests/<filename>.bin"
	 * 
	 * @return The number of instructions read into the instruction memory
	 * @throws FileNotFoundException If <filename> is not a valid file
	 * @throws IOException           If the input stream is closed or something
	 *                               funky happens (shouldn't ever happen)
	 */
	private void loadInstructionsViaCLI() throws FileNotFoundException, IOException  {
		System.out.printf("Input name of test (without extension)\nLooking for files in %s\n", System.getProperty("user.dir"));
		File file;
		while(true) {
			String name = scan.next();
			if(name.equals("exit")) {
				System.out.printf("Exiting\n");
				System.exit(0);
			}
			file = new File(String.format("%s.bin", name));
			if(!file.exists()) {
				System.out.printf("File %s does not exists. Please try again, or type \"exit\" to quit\n", file.getAbsolutePath());
			} else {
				break;
			}
		}
		loadInstructions(file);
	}

	/**
	 * Loads the instructions into the simulator by loading a file stored in "bin/tests/<filename>.bin"
	 * NOTICE: This is really funky: We need to use bin/tests instead of src/tests when running with junit
	 * 
	 * @return The number of instructions read into the instruction memory
	 * @throws FileNotFoundException If <filename> is not a valid file
	 * @throws IOException           If the input stream is closed or something
	 *                               funky happens (shouldn't ever happen)
	 */
	private void loadInstructionsFromFilename(String name) throws FileNotFoundException, IOException {
		loadInstructions(new File(String.format("%s.bin", name)));
	}

	/**
	 * Performs the actual loading of instructions into instruction memory
	 * @param file The file which contains the instructions to be read
	 * @throws FileNotFoundException If <filename> is not a valid file
	 * @throws IOException           If the input stream is closed or something
	 *                               funky happens (shouldn't ever happen)
	 * @return The number of instructions read into memory
	 */
	private void loadInstructions(File file) throws IOException, FileNotFoundException {
		System.out.printf("Looking for file in %s\n", file.getAbsolutePath());
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); //By buffering the stream we get better performance

		//Read data from our stream, store in instruction memory
		int i = 0; //Number of bytes read
		int read;
		while((read = bis.read()) != -1) { //Read a single byte at a time
			mem[i] = (byte) read; //put into array at correct location
			i++;
		}
		bis.close();
	}
}
