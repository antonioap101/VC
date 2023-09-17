import math

matrix = [
          [1,2,3],
          [4,5,6],
          [7,8,9],   
]

print(matrix)

matrix = [[x * 2 for x in row] for row in matrix]


print(matrix)

