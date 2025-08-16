package com.example.rvcopilot.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rvcopilot.data.User
import com.example.rvcopilot.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel to retrieve all users in the fire database.
 * manages the User Interface (ui) state of the Home page screen.
 * This calls inherits from the ViewModel: it will persist
 * data across all recompositions.
 * https://developer.android.com/topic/libraries/architecture/viewmodel
 */

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {


    // Firestore provides the Flow<List<User>>
    val allUsers = repository.getAllUsers()

    private val _userBio = MutableStateFlow("")
    val userBio: StateFlow<String> = _userBio

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser
    // for development only.  remove when finished using this
    //init {
    //    _currentUser.value = User(username = "admin", password = "pass")
    //}

    fun updateBio(newBio: String) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null && user.firebaseId.isNotBlank()) {
                println("Updating bio for firebaseId: ${user.firebaseId}")
                repository.updateUserBio(user.firebaseId, newBio)
                println("Bio updated to: $newBio")

                // after new bio is written it must be retrieved from collection DB
                val updatedSnapshot = repository.getUserById(user.firebaseId)
                updatedSnapshot?.let {
                    _currentUser.value = it
                    println("Updated currentUser = $it")
                }

            } else {
                println("updateBio failed: user is null or firebaseId is blank")
            }
        }
    }
    fun setCurrentUser(user: User) {
        println("setCurrentUser in userViewModel: ${user.username}")
        _currentUser.value = user
        _userBio.value = user.userBio
    }

    fun updateUserReview(newReview: String) {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                repository.updateUserReview(user.firebaseId, newReview)
                // Refresh local state after saving
                _currentUser.value = user.copy(reviewBio = newReview)
            }
        }
    }

    fun loadUser(username: String) {
        println("loadUser() was called with: $username")
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            println("found user: $user")
            _currentUser.value = user
            _userBio.value = user?.userBio ?: ""
        }
    }


    /**
     * saves user into the database
     * --loadFavoriteRoutes() updates the User Interface (ui)
     * --asynchronous function because of the coroutine 'launch'
     * --the ui doesn't freeze it because of coroutine
     * --runs in background not front
     * --viewModelScope.launch ensure background operation, otherwise
     * database operation could block the ui.
     * */
    fun addUser(
        username: String,
        password: String,
        userBio:String
    ) {
        viewModelScope.launch {
            val user = User(
                firebaseId = "",
                username = username,
                password = password,
                userBio = userBio
            )
            println("UserViewModel addUser(): calling insertUser for ${user.username}")
            repository.insertUser(user) // generates firebaseId
        }
    }

    /**
     * Load favorite routes from Room Database
     */
    fun findUserByUsername(username: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            onResult(user)
        }
    }
    /**
     * Login checking
     */
    fun validateLogin(
        username: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            onResult(user?.password == password)
        }
    }

    /**
     * handles cases for multiple composables that require the same ViewModel
     * if using NavHost and rememberNavController() anywhere then
     * the provideFactory is the correct method.
     * the ViewModel is now scoped and reused correctly across
     * recompositions and navigation entries.
     * */

    companion object {
        fun provideFactory(repository: UserRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return UserViewModel(repository) as T
                }
            }
    }
}