package pl.covenbookingdesk.v7.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate


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
    val date: String,
    val slot: SlotTime,
    val witch: String,
)

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