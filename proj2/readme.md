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




