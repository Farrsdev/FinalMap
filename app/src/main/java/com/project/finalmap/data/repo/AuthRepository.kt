package com.project.finalmap.data.repo

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.firestore
import com.project.finalmap.data.models.User
import com.project.finalmap.util.security.Helper

class AuthRepository {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    fun register(user: User, password:String, onResult: (Boolean, String) -> Unit){
        db.collection("users")
            .whereEqualTo("username", user.username.trim())
            .get()
            .addOnSuccessListener { s->
                if(!s.isEmpty){
                    onResult(false, "Username sudah digunakan!")
                } else{
                    auth.createUserWithEmailAndPassword(
                        user.username.trim() + "@gmail.com",
                        password
                    ).addOnCompleteListener {
                        if(it.isSuccessful){
                            val firebaseUser = it.result?.user

                            if(firebaseUser != null){
                                val finalUser = user.copy(
                                    uid = firebaseUser.uid,
                                    username = user.username.trim(),
                                    nama = user.nama.trim(),
                                    phone = user.phone.trim(),
                                    //password = Helper.hashPassword(password),
                                    createdAt = System.currentTimeMillis()
                                )

                                db.collection("users")
                                    .document(firebaseUser.uid)
                                    .set(finalUser)
                                    .addOnCompleteListener { r->
                                        if(r.isSuccessful){
                                            logout()
                                            onResult(true, "Register berhasil!")
                                        } else {
                                            onResult(false, "Gagal menyimpan data")
                                        }
                                    }
                            } else{
                                onResult(false, "Gagal mendapatkan UID user")
                            }
                        } else{
                            onResult(false, it.exception?.message ?: "Terjadi kesalahan")
                        }
                    }
                }
            }
            .addOnFailureListener { onResult(false, "Gagal memeriksa username: ${it.message}") }
    }

    fun login(username:String, password: String, onResult: (Boolean, String) -> Unit){
        val fakeEmail = username.trim() + "@gmail.com"

        auth.signInWithEmailAndPassword(
            fakeEmail,
            password
        ).addOnCompleteListener { r->
            if(r.isSuccessful){
                onResult(true, "Login berhasil!")
            } else{
                val errorMsg = when(r.exception){
                    is FirebaseAuthInvalidUserException -> "Akun tidak ditemukan!"
                    is FirebaseAuthInvalidCredentialsException -> "Username atau Password salah!"
                    is FirebaseNetworkException -> "Periksa koneksi internet anda!"
                    else -> r.exception?.message ?: "Login gagal!"
                }
                onResult(false, errorMsg)
            }
        }
    }

    fun getCurrentUser(onResult: (User?, String?) -> Unit){
        val uid = auth.currentUser?.uid

        if(uid == null){
            onResult(null, "Sesi login tidak ditemukan!")
            return
        }

        db.collection("users")
            .document( uid)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.exists()){
                    val users =  doc.toObject(User::class.java)
                    onResult(users, null)
                } else{
                    onResult(null, "User tidak ditemukan!")
                }
            }
            .addOnFailureListener {
                onResult(null, "Gagal memuat profil: ${it.message}")
            }
    }
    fun updateProfile(nama: String, phone: String, onResult: (Boolean, String) -> Unit){
        val uid = auth.currentUser?.uid ?: return onResult(false, "Sesi login tidak ditemukan!")

        db.collection("users")
            .document(uid)
            .update(mapOf("nama" to nama.trim(), "phone" to phone.trim()))
            .addOnSuccessListener { onResult(true, "Profil berhasil diperbarui!") }
            .addOnFailureListener { onResult(false, "Gagal memperbarui profil: ${it.message}") }
    }

    fun deleteProfile(password:String, onResult: (Boolean, String) -> Unit){
        val usr = auth.currentUser
        if(usr == null){
            onResult(false, "Pengguna tidak ditemukan atau belum login")
            return
        }

        val email = usr.email
        if(email.isNullOrEmpty()){
            onResult(false, "Email pengguna tidak valid")
            return
        }
        val cred = EmailAuthProvider.getCredential(email, password)

        usr.reauthenticate(cred).addOnCompleteListener { reauthTask ->
            if(reauthTask.isSuccessful){
                val uid = usr.uid
                db.collection("users").document(uid)
                    .delete()
                    .addOnSuccessListener {
                        usr.delete().addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                onResult(true, "Akun berhasil dihapus")
                            } else {
                                val errorMsg = authTask.exception?.message ?: "Gagal menghapus akun"
                                onResult(false, errorMsg)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Gagal menghapus data profil: ${e.message}")
                    }
            } else{
                onResult(false, "Password salah")
            }
        }

    }

    fun logout(){
        auth.signOut()
    }
}