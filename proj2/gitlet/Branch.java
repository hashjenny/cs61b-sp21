package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

public class Branch implements Serializable {
    public String branchName;
    public String currentCommitID;

    public Branch(String branchName, String currentCommitID) {
        this.branchName = branchName;
        this.currentCommitID = currentCommitID;
    }
}
