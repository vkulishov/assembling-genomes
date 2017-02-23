Week 2 has 4 tasks.

Problem 1:
Puzzle Assembly

Task. Let each square piece be defined by the four colors of its four edges, in the format (up,
left,down,right). Let a “valid placement” be defined as a placement of 𝑛^2 square pieces onto an
𝑛-by-𝑛 grid such that all “outer edges” (i.e., edges that border no other square pieces), and
only these edges, are black, and for all edges that touch an edge in another square piece, the
two touching edges are the same color.

Dataset. Each line of the input contains a single square piece, in the format described above:
(up,left,down,right). You will be given 25 such pieces in total (so 25 lines of input). Note that
all “outer edges” (i.e., edges that border no other square pieces on the puzzle) are black, and none of
the “inner edges” (i.e., edges not on the outside border of the puzzle) are black.

Output. Output a “valid placement" of the inputted pieces in a 5-by-5 grid. Specifically, your
output should be exactly 5 lines long (representing the 5 rows of the grid), and on each line of your output,
you should output 5 square pieces in the exact format described, (up,left,down,right), separated
by semicolons. There should be no space characters at all in your output.


Problem 2:
Finding an Eulerian Cycle in Directed Graph

Task. Given a directed graph, find an Eulerian cycle in the graph or report that none exists.

Input Format. The first line contains integers 𝑛 and 𝑚 — the number of vertices and the number of
edges, respectively. Each of the following 𝑚 lines specifies an edge in the format “u v”. (As usual, we
assume that the vertices of the graph are {1, 2, . . . , 𝑛}.) The graph may contain self-loops (that
is, edges of the form (𝑣, 𝑣)) and parallel edges (that is, several copies of the same edge).
It is guaranteed that the graph is strongly connected.

Output Format. If the graph has no Eulerian cycle, output 0. Otherwise output 1 in the first line and a
sequence 𝑣1, 𝑣2, . . . , 𝑣𝑚 of vertices in the second line. This sequence should traverse an Eulerian cycle in
the graph: (𝑣1, 𝑣2),(𝑣2, 𝑣3), . . . ,(𝑣𝑚−1, 𝑣𝑚),(𝑣𝑚, 𝑣1) should all be edges of the graph and each edge of
the graph should appear in this sequence exactly once. As usual, the graph may contain many Eulerian
cycles (in particular, each Eulerian cycle may be traversed starting from any of its vertices). You may
output any one of them.