# Lots of branches
addi x3, x0, 1
addi x4, x0, 2
addi x5, x0, -1

# Is 1 == 2?
beq x3, x4, L2
addi x9, x0, 1

# Is 1 != 2?
L2:
bne x3, x4, L3
jal x0, L4
L3:
addi x11, x0, 1

# Is -1 < 1?
L4:
blt x5, x3, L5
jal x0, L6
L5:
addi x12, x0, 1

# Is 1 >= 2?
L6:
bge x3, x4, L7
addi x13, x0, 1

# Is -1.U < 1.U
L7:
bltu x5, x3, L8
addi x14, x0, 1

L8:
bgeu x5, x5, L9
jal x0, L10
L9:
addi x15, x0, 1
L10:
addi x10, x0, 10
ecall

