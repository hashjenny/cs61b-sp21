package gitlet;

import java.io.Serializable;

public class Head implements Serializable {
    public String commitId;
    public String branchName;

    public Head(String commitId, String branchName) {
        this.commitId = commitId;
        this.branchName = branchName;
    }
}
