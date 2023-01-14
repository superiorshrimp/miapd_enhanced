#python consistency_index.py 1 7 0.1666 0.5 0.25 0.1666 4 0.1428 1 0.3333 5 0.2 0.1428 5 6 3 1 6 3 2 8 2 0.2 0.1666 1 8 0.2 8 4 5 0.3333 0.125 1 0.1111 2 6 7 0.5 5 9 1 2 0.25 0.2 0.125 0.125 0.5 0.5 1
#principal eigenvalue = 11.2344
#CI = 0.705

from numpy.linalg import eigvals as eig
from numpy import array
from math import sqrt
from sys import argv

def main():
    N = int(sqrt(len(argv) - 1))
    Matrix = array(argv[1:], dtype=float).reshape(N,N)
    
    principal_eigenvalue = max(eig(Matrix))
    
    ConsistencyIndex = (principal_eigenvalue - N) / (N - 1)
    
    print(ConsistencyIndex)

if __name__ == "__main__":
    main()