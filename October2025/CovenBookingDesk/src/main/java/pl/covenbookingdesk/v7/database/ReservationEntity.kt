package pl.covenbookingdesk.v7.database

import androidx.room.Entity
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter


enum class SlotTime(val timeSlot: String) {
    SLOT_2300("23:00"),
    SLOT_0000("00:00"),
    SLOT_0100("01:00"),
    SLOT_0200("02:00"),
    SLOT_0300("03:00"),
    SLOT_0400("04:00"),
    SLOT_0500("05:00")
}

@Entity(tableName = "booking", primaryKeys = ["date","slot"])
data class BookingEntity(
    val date: LocalDate,
    val slot: SlotTime,
    val witch: String,
)

class DateTypeConverter {
    companion object {
        private val PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
    @TypeConverter
    fun toSlotTime(databaseValue: String): LocalDate {
        return LocalDate.parse(databaseValue,PATTERN)
    }

    @TypeConverter
    fun fromSlotTime(slot: LocalDate): String {
        return slot.format(PATTERN)
    }
}



class SlotTypeConverter {
    @TypeConverter
    fun toSlotTime(databaseValue: String): SlotTime? {
        return SlotTime.entries.firstOrNull { it.timeSlot == databaseValue }
    }

    @TypeConverter
    fun fromSlotTime(slot: SlotTime?): String? {
        return slot?.timeSlot
    }
}