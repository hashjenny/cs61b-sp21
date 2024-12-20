package gitlet;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/** Represents a gitlet commit object.
 *  @author hashjenny
 */
public class Commit implements Serializable, Dumpable {
    /*
      List all instance variables of the Commit class here with a useful
      comment above them describing what that variable represents and how that
      variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String id;
    private final String message;
    private final String timestamp;
    private final String parentId;
    private final String mergedParentId;
    // file: filename -> blobId
    private final HashMap<String, String> files;

    // for "gitlet init"
    public Commit(String message) {
        this(message, "", "");
    }

    public Commit(String message, String parentId) {
        this(message, parentId, "");
    }

    public Commit(String message, String parentId, String mergedParentId) {
        this.message = message;
        this.parentId = parentId;
        this.mergedParentId = mergedParentId;
        this.files = new HashMap<>();

        var unixEpoch = Instant.now();
        if (message.equals("initial commit")) {
            unixEpoch = Instant.EPOCH;
        }
        ZoneId currentZoneId = ZoneId.systemDefault();
        // 将Instant对象转换为当前时区的ZonedDateTime对象
        ZonedDateTime zonedDateTime = unixEpoch.atZone(currentZoneId);
        // 创建DateTimeFormatter对象，并指定英文语言环境
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEE MMM d HH:mm:ss yyyy xx", Locale.US);
        this.timestamp = zonedDateTime.format(formatter);

        var sha1Items = new ArrayList<String>();
        for (var entry : files.entrySet()) {
            sha1Items.add(entry.getValue());
        }
        sha1Items.add(parentId);
        sha1Items.add(mergedParentId);
        sha1Items.add(message);
        sha1Items.add(timestamp);
        this.id = Utils.sha1(sha1Items.toArray());
    }

    public void addFile(String fileName, String blobId) {
        this.files.put(fileName, blobId);
    }

    @Override
    public void dump() {
        Utils.message("------------------");
        Utils.message("Commit id: %s", id);
        Utils.message("Commit message: %s", message);
        Utils.message("Commit parentId: %s", parentId);
        Utils.message("Commit mergedParentId: %s", mergedParentId);
        Utils.message("Commit timestamp: %s", timestamp);
        Utils.message("Commit files: ");
        for (var entry : files.entrySet()) {
            Utils.message("    <%s, %s>", entry.getKey(), entry.getValue());
        }
        Utils.message("------------------");
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getParentId() {
        return parentId;
    }

    public String getMergedParentId() {
        return mergedParentId;
    }

    public ArrayList<String> getAllParents() {
        var arr = new ArrayList<String>();
        if (parentId != null) {
            arr.add(parentId);
        }
        if (mergedParentId != null) {
            arr.add(mergedParentId);
        }
        if (arr.isEmpty()) {
            return null;
        }
        return arr;
    }

    public HashMap<String, String> getFiles() {
        return files;
    }
}
