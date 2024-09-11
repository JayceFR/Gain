package com.jaycefr.gain

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "steps")
data class StepCount(
    @ColumnInfo(name = "steps") val steps : Long,
    @ColumnInfo(name = "created_at") val createdAt : String,
    @PrimaryKey(autoGenerate = true) val id : Int = 0
)

@Dao
interface StepsDao {
    @Query("Select * from steps")
    suspend fun getAll(): List<StepCount>

    @Query("Select * from steps where created_at >= date(:startDateTime)" + " And created_at < date(:startDateTime, '+1 day')")
    suspend fun loadAllStepsFromToday(startDateTime : String) : Array<StepCount>

    @Insert
    suspend fun insertAll(vararg steps: StepCount)

    @Delete
    suspend fun delete(steps: StepCount)

}

@Database(entities = [StepCount::class], version = 1)
abstract class StepAppDatabase : RoomDatabase() {
    abstract fun stepsDao() : StepsDao
}
