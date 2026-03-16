package com.example.alzcare.ui.patient.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alzcare.common.SingleLiveEvent
import com.example.alzcare.firestore.UsersDao
import com.example.alzcare.model.User
import com.example.alzcare.ui.Message

class SearchViewModel : ViewModel() {

    val events = SingleLiveEvent<SearchViewEvent>()
    val docsLiveData = MutableLiveData<List<User>>()
    val messageLiveData = SingleLiveEvent<Message>()


    fun goHome() {
        events.postValue(SearchViewEvent.NavigateToHome)
    }

    fun loadDocs() {
        UsersDao.getAllUsers { task ->
            if (!task.isSuccessful) {
                // Show message and return
                return@getAllUsers
            }
            val docs = task.result
                .toObjects(User::class.java) // toObjects converts all the data to the type you want
            val doctorUsers = docs.filter { it.role == "Doctor" }
            docsLiveData.postValue(doctorUsers)
        }
    }

    fun searchList(docsList: List<User>, text: String): List<User> {
        return docsList.filter { doc ->
            doc.userName?.toLowerCase()?.contains(text.toLowerCase()) == true
        }
    }
}
