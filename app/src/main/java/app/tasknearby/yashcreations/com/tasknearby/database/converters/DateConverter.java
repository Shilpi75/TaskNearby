package app.tasknearby.yashcreations.com.tasknearby.database.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Converts Date to/from Long
 * @author shilpi
 */


public class DateConverter {
    @TypeConverter
    public Long dateToLong(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Date longToDate(Long value){
        return value == null ? null : new Date(value);
    }
}
