package app.tasknearby.yashcreations.com.tasknearby.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import app.tasknearby.yashcreations.com.tasknearby.database.converters.DateConverter;


/**
 * @author shilpi
 */

@Entity(tableName = "locations")
@TypeConverters({DateConverter.class})
public class Location {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "place_name")
    private String placeName;

    private String latitude;

    private String longitude;

    @ColumnInfo(name = "use_count")
    private int useCount;

    @ColumnInfo(name = "is_hidden")
    private int isHidden;

    @ColumnInfo(name = "date_added")
    private Date dateAdded;

    @Ignore
    public Location() {

    }

    public Location(String placeName, String latitude, String longitude, int useCount, int isHidden, Date dateAdded) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.useCount = useCount;
        this.isHidden = isHidden;
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(int isHidden) {
        this.isHidden = isHidden;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
