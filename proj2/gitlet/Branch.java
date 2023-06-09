package gitlet;

import java.io.Serializable;
import java.util.*;
/*
* Represents a branch
* Branches are stacks of commits
* We only really care about the one at the top
* We keep the rest of the info just in case
 */

public class Branch implements Serializable {
    private Stack<Commit> branchStack = new Stack<Commit>();
    private String name;
    public Branch(Commit c, String n) {
        branchStack.push(c);
        name = n;
        Repository.updateBranchMap(n, this);
    }
    public Commit peek() {
        return branchStack.peek();
    }
    public void push(Commit c) {
        branchStack.push(c);
        Repository.updateBranchMap(this.name, this);
    }
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return branchStack.toString();
    }
}
