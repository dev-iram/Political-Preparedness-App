package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.MyElection

@Dao
interface ElectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(election: List<Election>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFollow(myFollow: MyElection )

    @Query("SELECT * FROM election_table ORDER BY electionDay ASC")
    fun getAllElection(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table INNER JOIN my_election_table ON election_table.id = my_election_table.id ORDER BY election_table.electionDay ASC")
    fun getAllMyElection(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id AND division_id = :divisionId LIMIT 1")
    suspend fun getElectionById(id: Int, divisionId: String): Election?

    @Query("SELECT id FROM my_election_table WHERE id = :id")
    suspend fun getFollowById(id: Int): Int?

    @Query("DELETE FROM my_election_table WHERE id = :id")
    suspend fun deleteFollowById(id: Int)

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun clear()

}