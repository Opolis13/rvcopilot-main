package com.example.rvcopilot.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rvcopilot.data.PictureRepository
import com.example.rvcopilot.data.PictureRepository.PictureCategory
import com.example.rvcopilot.data.Pictures
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * ----class extender: The PictureViewModel inherits (extends) from
 * the ViewModel. PictureViewModel is a subclass of ViewModel.
 * ----the state persists when there is a configuration change
 * The ViewModel keeps data from being lost when there is a configuration change
 * it allows the ui components to automatically change when there are state updates.
 *
 * */

class PictureViewModel(
    private val repository: PictureRepository
) : ViewModel() {

    val allPictures: StateFlow<List<Pictures>> =
        repository.getAllPictures(PictureCategory.TRIP).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val tripPictures: StateFlow<List<Pictures>> =
        repository.getTripPictures().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val campsitePictures: StateFlow<List<Pictures>> =
        repository.getCampsitePictures().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    var alreadySeeded = false

    fun insertPicture(picture: Pictures, category: PictureCategory) {
        viewModelScope.launch {
            when (category) {
                PictureCategory.TRIP -> repository.insertTripPicture(picture)
                PictureCategory.CAMPSITE -> repository.insertCampsitePicture(picture)
                //repository.insertTripPicture(picture)
            }
        }
    }
    fun deletePicture(
        picture: Pictures,
        context: Context,
        category: PictureCategory
    ) {
        viewModelScope.launch {
            repository.deletePicture(picture, context, category)
        }
    }


    // load a group of pictures into firebase one time
    fun seedPictures(context: Context) {
        if (alreadySeeded) return
        alreadySeeded = true
        viewModelScope.launch {
            repository.seedPictures(context)
        }
    }
    // get image from helper function in PictureRepository
    fun getImageUriFromDrawable(context: Context, drawableResId: Int): Uri? {
        return repository.getImageUriFromDrawable(context, drawableResId)
    }



    companion object {
        fun provideFactory(repository: PictureRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PictureViewModel(repository) as T
                }
            }
    }
}