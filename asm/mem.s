.text
	li x2, 1048576
	li x5, 0xffff
	li x6, 0xefef
	li x7, 0x1ffff
	li x8, -1
	addi sp, sp, -28
#Attempt to use sw
	sw x5, 0(sp)
	sw x6, 4(sp)
	sw x7, 8(sp)
	sw x8, 12(sp)
#Attempt to use sh
	sh x5, 16(sp)
	sh x6, 18(sp)
	sh x7, 20(sp)
	sh x8, 22(sp)
#Attempt to use wb
	sb x5, 24(sp)
	sb x6, 25(sp)
	sb x7, 26(sp)
	sb x8, 27(sp)

#Load it all back out
#Load and load-unsigneds
	lb x3, 27(sp)
	lb x4, 26(sp)
	lb x5, 25(sp)
	lb x6, 24(sp)
	lbu x7, 27(sp)
	lbu x8, 26(sp)
	lbu x9, 25(sp)
	lbu x23, 24(sp)

#Load  half, signed/unsigned
	lh x11, 22(sp)
	lh x12, 20(sp)
	lh x13, 18(sp)
	lh x14, 16(sp)
	lhu x15, 22(sp)
	lhu x16, 20(sp)
	lhu x17, 18(sp)
	lhu x18, 16(sp)
	lw x19, 12(sp)
	lw x20, 8(sp)
	lw x21, 4(sp)
	lw x22, 0(sp)
	addi x10, x0, 10
	ecall
