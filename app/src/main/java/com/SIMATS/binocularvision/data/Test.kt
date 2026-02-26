package com.SIMATS.binocularvision.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class Test(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val testType: String,
    val score: Double,
    val percentage: Double,
    val stability: Double = 0.0,
    val tracking: Double = 0.0,
    val accuracy: Double = 0.0,
    val reaction: Double = 0.0,
    val classification: String,
    val date: String
)
