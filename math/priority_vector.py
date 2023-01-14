import numpy as np
from sys import argv
from math import sqrt

def main():
    N = int(sqrt(len(argv) - 1))
    Matrix = np.array(argv[1:], dtype=float).reshape(N, N)

    eig_vec = np.linalg.eig(Matrix)[1][:, 0]
    p = eig_vec / eig_vec.sum()
    np.set_printoptions(linewidth=np.inf)
    print(p.real)

if __name__ == "__main__":
    main()