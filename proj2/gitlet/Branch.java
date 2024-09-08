package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    public String branchName;
    public String currentCommitID;

    public Branch(String branchName, String currentCommitID) {
        this.branchName = branchName;
        this.currentCommitID = currentCommitID;
    }
}
