package app.tasknearby.yashcreations.com.tasknearby.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;


/**
 * @author shilpi
 */

@Entity(tableName = "attachments")
public class Attachment {

    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * Stores to content of the note associated with the task.
     */
    private String content;

    @Ignore
    public Attachment() {
    }

    public Attachment(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
