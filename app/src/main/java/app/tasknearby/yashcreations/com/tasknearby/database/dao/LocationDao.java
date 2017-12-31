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
    long insertLocation(Location location);

    @Insert
    List<Long> insertLocations(List<Location> locationList);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

    @Query("SELECT * FROM locations")
    List<Location> getAllLocations();

    @Query("SELECT * FROM locations WHERE id = :locationId")
    Location getLocationWithId(long locationId);
}
