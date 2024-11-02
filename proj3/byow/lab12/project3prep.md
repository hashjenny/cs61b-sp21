# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:
> Use the first point on leftmost of the hexagon as the reference point for that hexagon, then obtain the reference points of the hexagons located north, south, northeast, southeast, northwest, and southwest of that point. 
> Use two Set structures: one DrawSet to store the points to be drawn, and one VisitedSet to store the points that have already been drawn. 
> Use the current timestamp as the seed for the random number generator to generate the first hexagon reference point. 
> Store the point in the DrawSet. Then generate neighboring reference points.
> After verifying that all points can be used to draw a regular hexagon on the canvas, draw the hexagon until the DrawSet is empty.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:
>  The process of tessellating hexagons can be analogized to the generation of a procedural world, where each hexagon represents a room or a specific area, and the tessellation process represents the way these areas are connected to form a coherent world.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:

> `init` method :)

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:

> pass
