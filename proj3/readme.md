# Theseus: Random World Generation & Raycasting

## Introduction
Theseus is a random world generator based on the myth
of the Minotaur that implements raycasting. I built an algorithm
to generate a random amount of randomly sized rooms and hallways.
The algorithm ensures there are *no islands*, meaning all rooms and hallways
are accessible. The algorithm generates random blue and red lights in rooms, along with a single
golden thread (representing the thread that Theseus followed out of the labyrinth).

I then built a rendering engine that implements raycasting to calculate both
the player's FOV and the impact of flickering lights. The FOV expands and recedes
depending on the light near the player. The FOV and light sources are impacted by
walls: the player cannot see and light cannot reach beyond walls.

See video below for how this works. When the player is between the blue and red lights, they can
see both; but the space in between is dark. Imagine standing in a dark alley and seeing a light
far away. You would see what's around the light, but the path in between would be dark.

https://github.com/konishdutta/cs61b/assets/16747354/8f95b3fb-774d-46da-991e-70aa3f9b44a1

## Installation & Usage
- Clone the Gitlet repository to your folder of choice on your local machine.
- Compile all the Java classes:
  ```javac byow/*.java```
- Run the main class
- N kicks off a new game. L loads the previous game (if one exists).
- Enter a number of your choosing. This is the seed of the pseudo-random generator.
- S starts the game.
- W, S, A, D are the controls to get around.
- Pressing ':' then 'Q' quits the game with a save.
- During gameplay, if you want to see the whole world, you can turn on "Zeus Mode" by hitting Z.

## Design
See the basic class structure I used below. I've omitted the class I used for persistence.
![World Generator Class Structure](https://github.com/konishdutta/cs61b/assets/16747354/d873549e-c9e7-42a8-be28-a1e6c7ee366e)
![Renderer Class Structure](https://github.com/konishdutta/cs61b/assets/16747354/3a1b43be-aeff-4099-82c2-d8b2dd864af2)

## World Generator Algorithm
The algorithm does the following at a high level:
- For a random number of times, generate a room of random size.
   - There are some safety checks to ensure rooms don't overlap.
- For a random number of times, generate hallways of random sizes that come out of these rooms.
   - This involves understanding which direction a hallway should emerge. For example, a hallway should be oriented east if it's coming out from the east wall of a room. There is a function that handles this.
- If a hallway hits another room, stop the hallway and add a door.
- After the initial set of random calls, check if all hallways and rooms are connected.
- While not all spaces are connected:
   - If they are not connected, pick a random room. Check every direct neighbor (north, south, east, west) the room has.
   - If a neighbor is not connected to the room, draw a hallway to that neighbor.
   - If no room has a direct neighbor, draw another random hallway and try again.

There are some checks in place to ensure that while loops are forced to terminate.

## Raycasting Algorithm
### Field of View (FOV) generation
The FOV algorithm simulates the visible area around a player, determining how far they can see within a virtual environment. It operates as follows:
1. **Initialization**: The algorithm starts with initial coordinates (x, y) and directional increments (dx, dy), alongside a specified light radius that represents the maximum distance the player can see.
2. **Traversal and Obstacle Detection**: Iteratively, the algorithm advances (x, y) by (dx, dy) while tracking the total distance traveled. At each step, it checks if the new (x, y) location hits a wall:
   * If a wall is encountered, the current location is marked as visible within the FOV (since light reaches up to the obstacle), and the loop terminates for this ray, simulating the obstruction of sight.
   * If no obstacle is encountered and the distance traveled is within the light radius, the location is marked as within the FOV, and the algorithm continues to the next iteration.
3. **Distance Calculation**: The distance traveled is updated using a Euclidean metric, ensuring accurate simulation of sight range.
### Light Generation
To simulate dynamic lighting within the environment, a modified FOV algorithm is employed that considers light sources and their interaction with the surroundings:
1. **Light Source Processing**: For each light source, a specialized FOV algorithm calculates the spread of light, incorporating a decay factor based on distance to simulate the dimming effect. This process introduces a degree of randomness to simulate flickering lights, enhancing the realism of the environment.
2. **Light Blending**: The intensities and colors of light from different sources are blended at each point in the environment, considering the cumulative effect of multiple light sources and creating a cohesive lighting effect.
3. **Visibility Enhancement**: The player's FOV is augmented by checking for direct lines of sight to light sources. If a direct path from the player to a light source is unobstructed by walls, the visibility in that direction is enhanced, simulating the ability to see lights from afar even if the intermediate space is not within the direct FOV.

Vectors were instrumental in modeling the direction and magnitude of both light and visibility rays, allowing for precise control over their propagation through the game environment. By representing direction increments (dx, dy) as vectors, we could accurately simulate the raycasting mechanism underlying the FOV and lighting systems.

Matrices played a crucial role in performing complex transformations necessary for simulating realistic lighting effects. This includes calculating the attenuation of light intensity over distance and blending the colors of multiple light sources. By employing matrix operations, I could efficiently compute the cumulative impact of multiple lights on the game’s environment, resulting in a nuanced and dynamic lighting model.

