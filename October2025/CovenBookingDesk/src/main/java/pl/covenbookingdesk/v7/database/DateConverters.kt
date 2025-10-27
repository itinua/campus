package pl.covenbookingdesk.v7.database

import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverters {
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
}