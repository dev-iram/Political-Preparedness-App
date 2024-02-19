package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepresentativeViewModel : ViewModel() {

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    val representatives = MutableLiveData<List<Representative>>()

    fun fetchRepresentatives(address: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val data = CivicsApi.retrofitService.getRepresentativeInfoByAddress(address)
                    val dataHandle = data.offices.flatMap { it.getRepresentatives(data.officials) }
                    representatives.postValue(dataHandle)
                } catch (err: Exception) {
                    Log.e("fetchRepresentatives", err.printStackTrace().toString())
                }
            }
        }
    }
}
