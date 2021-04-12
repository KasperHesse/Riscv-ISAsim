.text
li x2, 1048576
add x24 , x0 , x0
addi x23 , x24 , 4
addi x22 , x24 , 16
addi x24 , x24 , 2
add x20 , x22 , x24 # corresponds to 168
jal x1 , B
j exit #corresponds to 176
B:
addi sp, sp, -12
sw x1, 8(sp)
sw x22, 4(sp)
sw x24, 0(sp)
add x24 , x0 , x23 # corresponds to 240
jal x1 , C # corresponds to 260
lw x22 , 0(x19) #corresponds to 280
lw x1 ,8(sp)
lw x22 , 4(sp)
lw x24 , 0(sp)
addi sp , sp , 12
jalr x0 , 0(x1)
C:
addi sp , sp , -8
sw x20 , 4(sp)
sw x24 , 0(sp)
add x20 , x0 , x21
addi x24 , x20 , 4
lw x20 , 4(sp)
lw x24 , 0(sp)
addi sp , sp , 8
jalr x0 , 0(x1)
exit:
addi x10, x0, 10
ecall
