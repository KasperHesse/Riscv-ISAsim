# Testing all of the SLT instructions
addi x3, x0, 1
addi x4, x0, 2
addi x5, x0, -1

# SLT performs a signed comparison
slt x28, x3, x4 # 1
slt x11, x4, x3 # 0
slt x12, x3, x5 # 0
slt x13, x5, x3 # 1

# SLTU performs unsigned comparsion
sltu x14, x3, x4 #1
sltu x15, x4, x3 #0
sltu x16, x3, x5 #1
sltu x17, x5, x3 #0

# And now with immediates
slti x20, x3, 2
slti x21, x4, 1
slti x22, x3, -1
slti x23, x5, 1

#Unsigned immediates
sltiu x24, x3, 2
sltiu x25, x4, 1
sltiu x26, x3, -1
sltiu x27, x5, 1
addi x10, x0, 10
ecall
