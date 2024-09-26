package com.jaycefr.gain.steps.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Entity(tableName = "steps")
data class StepCount(
    @PrimaryKey val date : String,
    @ColumnInfo(name = "steps") val steps : Long,
)

@Dao
interface StepsDao {
    @Query("Select * from steps")
    suspend fun getAll(): List<StepCount>

    @Query("Select * from steps where date = :date")
    fun getDay(date : String) : Flow<StepCount>

    @Query("Select steps from steps where date = :date")
    fun getTodaySteps(date : String = LocalDate.now().toString()) : Flow<Long>

    @Upsert
    suspend fun upsert(vararg steps: StepCount)

    @Delete
    suspend fun delete(steps: StepCount)

}

@Database(entities = [StepCount::class], version = 1)
abstract class StepAppDatabase : RoomDatabase() {
    abstract fun stepsDao() : StepsDao
}
