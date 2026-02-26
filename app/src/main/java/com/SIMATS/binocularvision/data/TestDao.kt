package com.SIMATS.binocularvision.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface  TestDao {
    @Query("SELECT * FROM tests WHERE userId = :userId ORDER BY date DESC")
    fun getTestsByUserId(userId: String): Flow<List<Test>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: Test)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTests(tests: List<Test>)

    @Query("DELETE FROM tests WHERE userId = :userId")
    suspend fun deleteTestsByUserId(userId: String)
}
