# CoastGuard

You are a member of the coast guard force in charge of a rescue boat that goes into the sea to rescue other sinking ships. When rescuing a ship, you need to rescue any living people on it and to retrieve its black box after there are no more passengers thereon to rescue. If a ship sinks completely, it becomes a wreck and you still have to retrieve the black box before it is damaged. Each ship loses one passenger every time step. Additionally, each black box incurs an additional damage point every time step once the ship becomes a wreck. One time step is counted every time an action is performed. You reach your goal when there are no living passengers who are not rescued, there are no undamaged boxes which have not been retrieved, and the rescue boat is not carrying any passengers. You would also like to rescue as many people as possible and retrieve as many black boxes as possible. The area in the sea that you can navigate is an mn grid of cells where 5 <= m; n <= 15.

This problem is approached using the following search strategies:
a) Breadth-first search.
b) Depth-first search.
c) Iterative deepening search.
d) Greedy search with at least two heuristics.
e) A* search with at least two admissible heuristics.



"Part of the Artificial intelligence Course @ GUC"
