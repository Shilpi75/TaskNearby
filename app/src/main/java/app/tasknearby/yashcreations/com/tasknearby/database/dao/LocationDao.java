package app.tasknearby.yashcreations.com.tasknearby.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.models.Location;

/**
 * @author shilpi
 */

@Dao
public interface LocationDao {

    @Insert
    public long insertLocation(Location location);

    @Insert
    public List<Long> insertLocations(List<Location> locationList);

    @Update
    public void updateLocation(Location location);

    @Delete
    public void deleteLocation(Location location);

    @Query("SELECT * FROM locations")
    public List<Location> getAllLocations();

    @Query("SELECT * FROM locations WHERE id = :locationId")
    public Location getLocationWithId(long locationId);
}
