package com.example.android.politicalpreparedness.network.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_election_table")
data class MyElection(@PrimaryKey val id: Int)