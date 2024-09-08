package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author hashjenny
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public final String id;
    private final String message;
    private final String timestamp;
    private String parentId;
    private String mergedParentId;
    public HashMap<String, String> commitFiles;

    public Commit(String message) {
        this.message = message;
        this.commitFiles = new HashMap<>();
        this.parentId = "";
        this.mergedParentId = "";

        var unixEpoch = Instant.now();
        if (message.equals("initial commit")) {
            unixEpoch = Instant.EPOCH;
        }
        ZoneId currentZoneId = ZoneId.systemDefault();
        // 将Instant对象转换为当前时区的ZonedDateTime对象
        ZonedDateTime zonedDateTime = unixEpoch.atZone(currentZoneId);
        // 创建DateTimeFormatter对象，并指定英文语言环境
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy XX", Locale.US);
        this.timestamp = zonedDateTime.format(formatter);

        this.id = Utils.sha1(message, timestamp);
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

    public void setParentId(String parentID) {
        this.parentId = parentID;
    }

    public void setMergedParentId(String mergedParentId) {
        this.mergedParentId = mergedParentId;
    }

    public void addFile(String fileName, String blobId) {
        this.commitFiles.put(fileName, blobId);
    }
}
