package com.example.kekodfirstproject.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kekodfirstproject.R

class MainViewModel : ViewModel() {

    private val _switchStates = MutableLiveData<Map<Int, Boolean>>()
    val switchStates: LiveData<Map<Int, Boolean>> get() = _switchStates

    init {

        _switchStates.value = mapOf(
            R.id.swHappiness to false,
            R.id.swOptimism to false,
            R.id.swKindness to false,
            R.id.swGiving to false,
            R.id.swRespect to false,
            R.id.swEgo to true
        )
    }

    fun updateSwitchState(switchId: Int, isChecked: Boolean) {
        val currentStates = _switchStates.value?.toMutableMap() ?: mutableMapOf()
        currentStates[switchId] = isChecked
        _switchStates.value = currentStates
    }
}