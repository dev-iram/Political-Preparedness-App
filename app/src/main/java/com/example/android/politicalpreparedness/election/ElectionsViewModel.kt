package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.ElectionsRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(application: Application) : AndroidViewModel(application){

    //TODO: Create live data val for upcoming elections
    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionsRepository(database)
    val upcomingElections = electionRepository.electionAll
    val myAllElection = electionRepository.myAllElection

    private val _navigateElection = MutableLiveData<Election?>()

    val navigateData: MutableLiveData<Election?>
        get() = _navigateElection
    fun setNullNav() {
        _navigateElection.value = null
    }

    fun goToDetailPage(election: Election) {
        _navigateElection.value = election
    }
    init {
        viewModelScope.launch {
            electionRepository.refreshData()
        }
    }
}