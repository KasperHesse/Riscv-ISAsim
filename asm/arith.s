# Lots of arithmetic
auipc x1, 100

addi x3, x0, 15
xori x4, x3, -1
ori x5, x4, 215
andi x6, x4, 215
slli x7, x3, 4
slli x8, x3, 31
srli x9, x8, 2
srai x16, x8, 2
add x11, x8, x3
sub x12, x6, x7
xor x13, x6, x12
or x14, x5, x13
and x15, x6, x12
addi x10, x0, 10
ecall

