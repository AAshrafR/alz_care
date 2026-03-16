package com.example.alzcare.firestore

import com.example.alzcare.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UsersDao {
    private val db = Firebase.firestore
    private val usersCollection = db.collection(User.CollectionName)

    fun getUserCollection(): CollectionReference {
        return usersCollection
    }

    fun createUser(user: User, onCompleteListener: OnCompleteListener<Void>) {
        val docRef = getUserCollection().document(user.id ?: "")
        docRef.set(user).addOnCompleteListener(onCompleteListener)
    }

    fun getUser(uid: String?, onCompleteListener: OnCompleteListener<DocumentSnapshot>) {
        getUserCollection().document(uid ?: "").get().addOnCompleteListener(onCompleteListener)
    }

    fun getAllUsers(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        getUserCollection()
            .get().addOnCompleteListener(onCompleteListener)
    }
}