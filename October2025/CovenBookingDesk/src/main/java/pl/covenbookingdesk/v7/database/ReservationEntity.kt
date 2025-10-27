package pl.covenbookingdesk.v7.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val date: LocalDate,
    val guestName: String,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)