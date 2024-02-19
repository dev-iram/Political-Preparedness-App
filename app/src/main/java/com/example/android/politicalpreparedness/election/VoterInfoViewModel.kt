package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoteInfo
import com.example.android.politicalpreparedness.network.models.asVoteInfo
import com.example.android.politicalpreparedness.ElectionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(election: Election, application: Application) :
    AndroidViewModel(application) {

    //TODO: Add live data to hold voter info
    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionsRepository(database)
    private val _electionData = MutableLiveData<Election>()
    val voteInfo = MutableLiveData<VoteInfo>()
    val isFollow = electionRepository.isFollow

    val electionData: LiveData<Election>
        get() = _electionData

    fun followActionElection(){
        viewModelScope.launch {
            isFollow.value?.let {
                if(it){
                    electionRepository.removeFollowById(electionData.value!!.id)
                }else{
                    electionRepository.addFollowById(electionData.value!!.id)
                }
                electionRepository.getFollowById(electionData.value!!.id)
            }
        }
    }
//
//    fun unFollowElection(){
//        viewModelScope.launch {
//            electionRepository.removeFollowById(electionData.value!!.id)
//            electionRepository.getFollowById(electionData.value!!.id)
//        }
//    }
    private suspend fun getVoteInfoAndIsFollow(electionId: Int, address: String) {
        withContext(Dispatchers.IO) {
            try {
                electionRepository.getFollowById(electionId)
                val data = CivicsApi.retrofitService.getVoterInfo(electionId, address).asVoteInfo()
                voteInfo.postValue(data)
            } catch (err: Exception) {
                Log.e("refreshData", err.printStackTrace().toString())
            }
        }
    }

    val votingLocationLink: LiveData<String> = Transformations.map(voteInfo) {
        it?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl ?: ""
    }

    val ballotInfoLink: LiveData<String> = Transformations.map(voteInfo) {
        it?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl ?: ""
    }

    class Factory(
        private val election: Election, private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            when (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
                true -> return VoterInfoViewModel(election, application) as T
                else -> throw IllegalArgumentException()
            }
        }
    }

    init {
        _electionData.value = election
        viewModelScope.launch {
            electionData.value?.apply {
                getVoteInfoAndIsFollow(
                    id,
                    division.id
                )
            }
        }
    }

}