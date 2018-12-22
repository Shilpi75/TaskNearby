package app.tasknearby.yashcreations.com.tasknearby.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;

/**
 * @author shilpi
 */
@Dao
public interface LocationDao {

    @Insert
    long insertLocation(LocationModel locationModel);

    @Insert
    List<Long> insertLocations(List<LocationModel> locations);

    @Update
    void updateLocation(LocationModel locationModel);

    @Delete
    void deleteLocation(LocationModel locationModel);

    /**
     * Returns all active(not hidden) locations sorted by use_count in decreasing order, the rows
     * having equal use counts are arranged alphabetically.
     */
    @Query("SELECT * FROM locations WHERE is_hidden = 0 ORDER BY use_count DESC, place_name ASC")
    List<LocationModel> getAllLocations();

    @Query("SELECT * FROM locations WHERE id = :locationId")
    LocationModel getLocationWithId(long locationId);
}
