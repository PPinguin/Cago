package com.cago.core.repository.managers

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import com.cago.core.models.Pack
import com.cago.core.models.server.PackInfo
import com.cago.core.repository.callbacks.Callback
import com.cago.core.utils.ErrorType
import com.cago.core.utils.GlobalUtils.UID
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor(override val context: Context) : Manager {

    private val key = "current_email"
    
    private var auth: FirebaseAuth
    private var stRef: StorageReference
    private var dbRef: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
        stRef = Firebase.storage.reference
        dbRef = Firebase.database.reference
    }

    fun getCurrentInfo(): Map<String, String>? = 
        auth.currentUser?.let { mapOf("uid" to it.uid, "email" to it.email!!) }
    fun getCurrentUID(): String? = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = getCurrentUID() != null

    fun downloadPack(name: String, path: String, callback: Callback<File>) {
        val file = File.createTempFile("ext-", ".cg")
        stRef.child("packages/$path/$name.cg").getFile(file)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    callback.success(file)
                else
                    callback.failure(ErrorType.ERROR_DOWNLOAD)
            }
    }
    
    fun syncPackages(sync: (String, String) -> File?){
        dbRef.orderByChild("/path").equalTo(UID)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { child ->
                        child.child("name").getValue<String>()?.let{ name ->
                            sync(name, child.key!!)?.let { file ->
                                stRef.child("packages/$UID/$name.cg").getFile(file)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun uploadPack(pack: File, update: Boolean, callback: Callback<String>) {
        val name = pack.nameWithoutExtension
        val file = Uri.fromFile(pack)
        val fileRef = stRef.child("packages/$UID/${file.lastPathSegment}")
        fileRef.putFile(file)
            .addOnFailureListener {
                callback.failure(ErrorType.ERROR_UPLOAD)
            }
            .addOnSuccessListener {
                if(!update) {
                    val info = PackInfo(name, UID, true)
                    val ref = dbRef.push()
                    ref.setValue(info)
                        .addOnCompleteListener {
                            callback.success(ref.key)
                        }
                } else callback.success()
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
        pack.getInfo()?.let { it ->
            stRef.child("packages/$UID/${it.name}.cg").delete()
            dbRef.child(pack.key!!).removeValue()
        }
    }

    fun sendLink(email: String, callback: Callback<Nothing>) {
        val actionCodeSettings = actionCodeSettings {
            url = "https://cago.com/finishSignUp?cartId=0"
            setAndroidPackageName(
                "com.example.android",
                true, /* installIfNotAvailable */
                "12" /* minimumVersion */)
            handleCodeInApp = true
        }
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnFailureListener {
                callback.failure(ErrorType.ERROR_SEND_LINK)
            }
            .addOnSuccessListener {
                saveEmail(email)
                callback.success()
            }
    }

    fun logIn(link: String, callback: Callback<Nothing>) {
        if (auth.isSignInWithEmailLink(link)) {
            auth.signInWithEmailLink(loadEmail()!!, link)
                .addOnSuccessListener {
                    callback.success()
                }
                .addOnCanceledListener {
                    callback.failure(ErrorType.ERROR_LOG_IN)
                }
        }
    }

    private fun saveEmail(email: String) {
        context.getSharedPreferences(key, Context.MODE_PRIVATE).edit {
            putString("email", email)
        }
    }

    private fun loadEmail(): String? =
        context.getSharedPreferences(key, Context.MODE_PRIVATE)
            .getString("email", null)

    fun logOut() {
        auth.signOut()
        context.getSharedPreferences(key, Context.MODE_PRIVATE).edit { clear() }
    }
}