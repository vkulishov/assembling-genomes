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

