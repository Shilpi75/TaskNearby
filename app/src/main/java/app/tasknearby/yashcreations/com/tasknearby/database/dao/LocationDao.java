package app.tasknearby.yashcreations.com.tasknearby.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("SELECT * FROM locations WHERE is_hidden = 0")
    List<LocationModel> getAllLocations();

    @Query("SELECT * FROM locations WHERE id = :locationId")
    LocationModel getLocationWithId(long locationId);
}
