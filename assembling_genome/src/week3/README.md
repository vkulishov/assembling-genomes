Week 3 has 6 tasks.

Problem 1:
Finding a Circulation in a Network

Task: Given a network with lower bounds and capacities on edges, find a circulation if it exists.

Input: Input Format. The first line contains integers 𝑛 and 𝑚 — the number of vertices and the number of edges,
       respectively. Each of the following 𝑚 lines specifies an edge in the format “u v l c”: the edge (𝑢, 𝑣)
       has a lower bound 𝑙 and a capacity 𝑐. (As usual, we assume that the vertices of the network are
       {1, 2, . . . , 𝑛}.) The network does not contain self-loops, but may contain parallel edges

Output Format. If there exists a circulation, output YES in the first line. In each of the next 𝑚 lines output
the value of the flow along an edge (assuming the same order of edges as in the input). If there is no
circulation, output NO.

Problem 2:
Selecting the Optimal 𝑘-mer Size

Task: Given a list of error-free reads, return an integer 𝑘 such that, when a de Bruijn graph is created from
the 𝑘-length fragments of the reads, the de Bruijn graph has a single possible Eulerian Cycle.

Dataset: The input consist of 400 reads of length 100, each on a separate line. The reads contain no
sequencing errors. Note that you are not given the 100-mer composition of the genome (i.e., some
100-mers may be missing).

Output: A single integer 𝑘 on one line.

Problem 3:
Bubble Detection

Task. Given a list of error-prone reads and two integers, 𝑘 and 𝑡, construct a de Bruijn graph from the
𝑘-mers created from the reads and perform the task of bubble detection on this de Bruijn graph with
a path length threshold of 𝑡.

Dataset. The first line of the input contains two integers, 𝑘 and 𝑡, separated by a single space. Each
subsequent line of the input contains a single read. The reads are given to you in alphabetical order
because their true order is hidden from you. Each read is 100 nucleotides long and contains a single
sequencing error (i.e., one mismatch per read) in order to simulate the 1% error rate of Illumina
sequencing machines. Note that you are not given the 100-mer composition of the genome (i.e., some
100-mers may be missing).

Output. A single integer (the number of (𝑣, 𝑤)-bubbles) on one line.


