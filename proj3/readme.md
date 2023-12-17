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
  ```javac byow/Core/*.java```
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


## Raycasting Algorithm

### Interesting Challenges
- I deduped blobs and commits using SHA-1 IDs. This ensured that
  I was never saving the same file twice, saving space.
- Making abbreviated commit search efficient was challenging.
    - My initial design involved implementing a trie, which
      could be an optimization, but is only practical in a
      meaningfully large file system.
    - Ultimately, I opted for a hashtable-like approach similar to
      how git works in real life.
    - I created a map of two letter abbreviations to a Linked List of Commit IDs. Here's how it would look:
      ``` ab: [abcde...] --> [ab123...] --> [abxyz...]...```
    - When a new commit is created, the map is updated.
    - When a user searches for an abbreviated commit,
      the algorithm takes the first two letters of the
      abbreviation as the key. I run down the associated linked list
      until I find a commit ID that prefix matches with the given abbreviation.
- I refactored my class structure a few times because my
  initial design wasn't correct. For example, my initial design
  included an explicit "Branch" class that tracked the
  tree structure of the branch. Ultimately, this wasn't
  necessary since the parent values of the commits held all the
  information I needed to traverse the branches. I removed this
  class and refactored my code accordingly.

### What I would improve if I did it again
- I handled a lot of special cases, especially for the complex merge function.
  Halfway through writing merge, I realized that I needed to break
  the function up into smaller functions that did a specific task.
    - For example, I have a function to find a common ancestor between two commits. Ultimately, I
      wrote three functions to handle this seemingly simple task:
        - findAncestor: master function that orchestrates subtasks
        - generateHistory: generates the history of a commit
        - traverseAncestor: traverses the history and returns whether a commit exists in that history.
    - My code still contains a lot of if statements. If I were to re-write it,
      I'd refactor the if statements by breaking up merge into smaller functions. For example,
      I would write a separate function that would separate the files between two commits by whether
      they were modified, added, deleted, or the same. In my current implementation, all of that is handled
      in a single function, which leads to less readable code.
- Optimal file directory structure. Instead of saving abbreviations in a
  separate map file, I could mimic the file structure similar to how Git is implemented.
