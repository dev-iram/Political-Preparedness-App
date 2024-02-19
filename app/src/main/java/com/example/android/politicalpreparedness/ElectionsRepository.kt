package com.example.android.politicalpreparedness

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.MyElection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(private val database: ElectionDatabase) {
    val electionAll: LiveData<List<Election>> = database.electionDao.getAllElection()
    val myAllElection: LiveData<List<Election>> = database.electionDao.getAllMyElection()
    private val _isFollow = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isFollow: LiveData<Boolean>
        get() = _isFollow

    suspend fun removeFollowById(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                database.electionDao.deleteFollowById(id)
                _isFollow.postValue(false)
            } catch (err: Exception) {
                Log.e("removeFollowById", err.printStackTrace().toString())
            }
        }
    }

    suspend fun addFollowById(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                database.electionDao.saveFollow(MyElection(id))
                _isFollow.postValue(true)
            } catch (err: Exception) {
                Log.e("addFollowById", err.printStackTrace().toString())
            }
        }
    }

    suspend fun getFollowById(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val data = database.electionDao.getFollowById(id)
                _isFollow.postValue(data != null)

            } catch (err: Exception) {
                Log.e("getFollowById", err.printStackTrace().toString())
            }
        }
    }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            try {
                val data = CivicsApi.retrofitService.getElections()
                database.electionDao.saveAll(data.elections)
            } catch (err: Exception) {
                Log.e("refreshData", err.printStackTrace().toString())
            }
        }
    }
}