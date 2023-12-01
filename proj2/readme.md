# Gitlet Version-Control System

## Introduction
Gitlet is a version-control system I wrote in Java from scratch
while auditing U.C. Berkeley's CS61B: Data Structures.
Gitlet mimics the key features of Git.

## Installation
- Ensure Java is installed on your system.
- Clone the Gitlet repository to your folder of choice on your local machine.
- Compile all the Java classes:
```javac gitlet/*.java```

## Usage
Note that abbreviated commits work.
- Initialize a repo: ```java gitlet.Main init```
- Add a file: java ```gitlet.Main add [file name]```
- Remove a file: ```java gitlet.Main rm [file name]```
- Commit: ```java gitlet.Main commit [message]```
- See the commit history: ```java gitlet.Main log```
- See the global commit history: ```java gitlet.Main global-log```
- Search for commits with a given message:
```java gitlet.Main find [commit message]```
- See current status of repo (current branch, staged files, removed files, modified not staged for files, and untracked files):
```java gitlet.Main status```
- Checkout a file from head commit:
```java gitlet.Main checkout -- [file name]```
- Checkout a file from a given commit:
```java gitlet.Main checkout [commit id] -- [file name]```
- Checkout a branch:
```java gitlet.Main checkout [branch name]```
- Create a branch:
```java gitlet.Main branch [branch name]```
- Remove a branch:
```java gitlet.Main rm-branch [branch name]```
- Reset to a commit:
```java gitlet.Main reset [commit id]```
- Merge files from the given branch into the current branch:
```java gitlet.Main merge [branch name]```

## Design
See the basic class structure I used below:
![Structure](https://github.com/konishdutta/cs61b/blob/ed54a1c604c3afb857d2fae143f8da15dfebbf30/proj2/imgs/class_structure.png "Structure")
The persistence layer includes:
- HEAD: file that loads the head commit
- MAP_KV: file that maps strings to commits
- COMMIT_ABBREV: this acts like a hashtable to speed up abbreviated commit search. More below.
- CURRENT_BRANCH: pointer for the current branch
- staging folder: each staged blob is saved here
- commits folder: each commit is saved here
- blobs folder: each blob found in a commit is saved here.
Note that multiple commits may share blobs to save space.

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
  abbreviation, then uses the Boyer-Moore algorithm
    ([see code](https://github.com/konishdutta/cs61b/blob/a5c0ca4a9fe84147ba50db92383edc2bceaba654/proj2/gitlet/KonishAlgos.java#L55))
  on each commit in the list until it finds the appropriate
  match.
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
