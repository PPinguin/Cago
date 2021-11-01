package com.cago.repository.managers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cago.models.Pack
import com.cago.models.server.PackInfo
import com.cago.repository.Repository
import com.cago.repository.callbacks.Callback
import com.cago.utils.ErrorType
import com.cago.utils.GlobalUtils.UID
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.net.InetAddress

class FirebaseManager(override val context: Context) : Manager {

    private var auth: FirebaseAuth
    private var stRef: StorageReference
    private var dbRef: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
        stRef = Firebase.storage.reference
        dbRef = Firebase.database.reference
        auth.signInAnonymously()
    }
    
    fun getCurrentUID() = auth.currentUser?.uid

    fun downloadPack(path: String?, callback: Callback<File>) {
        val file = File.createTempFile("ext-", ".cg")
        stRef.child("packages/$path.cg").getFile(file)
            .addOnCompleteListener {
                if(it.isSuccessful)
                    callback.success(file)
                else
                    callback.failure(ErrorType.ERROR_DOWNLOAD)
            }
    }

    fun uploadPack(pack: File, callback: Callback<String>) {
        val name = pack.nameWithoutExtension
        val file = Uri.fromFile(pack)
        val fileRef = stRef.child("packages/$UID/${file.lastPathSegment}")
        fileRef.putFile(file)
            .addOnFailureListener {
                callback.failure(ErrorType.ERROR_UPLOAD)
            }
            .addOnSuccessListener {
                val info = PackInfo(name, UID, true)
                val ref = dbRef.push()
                ref.setValue(info)
                    .addOnCompleteListener {
                        callback.success(ref.key)
                    }
            }
    }

    fun searchByQuery(query: String, callback: Callback<List<PackInfo>>) {
        dbRef.orderByChild("/name").equalTo(query)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.children
                        .map { s -> s.getValue<PackInfo>()!! }
                        .filter { i -> i.path != UID && i.public }
                    callback.success(result)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.failure(ErrorType.ERROR_RESULTS)
                }
            })
    }

    fun delete(pack: Pack) {
        pack.getInfo()?.let {
            stRef.child("package/${it.path}/${it.name}.cg").delete()
            dbRef.child(pack.key!!).removeValue()
        }
    }
}