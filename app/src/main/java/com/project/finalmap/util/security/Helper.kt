package com.project.finalmap.util.security

import com.project.finalmap.data.models.Country
import java.security.MessageDigest

object Helper {
    private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_.]{4,20}$")
    private val NAME_REGEX = Regex("^[\\p{L} .'-]{3,50}$")

    fun checkValidationPassword(password: String): String? {
        if (password.length < 8) return "Password minimal 8 karakter!"
        if (password.any { it.isWhitespace() }) return "Password tidak boleh mengandung spasi!"
        if (!password.any { it.isUpperCase() }) return "Password harus mengandung huruf besar!"
        if (!password.any { it.isLowerCase() }) return "Password harus mengandung huruf kecil!"
        if (!password.any { it.isDigit() }) return "Password harus mengandung angka!"
        if (!password.any { !it.isLetterOrDigit() }) return "Password harus mengandung special karakter!"
        return null
    }

    fun checkValidationProfile(username: String, nama: String, noTelp: String, country: Country): String? {
        val u = username.trim()
        if (u.isEmpty()) return "Username tidak boleh kosong!"
        if (!USERNAME_REGEX.matches(u)) return "Username 4-20 karakter, hanya huruf, angka, _ dan ."
        if (u.startsWith(".") || u.endsWith(".") || ".." in u) return "Username tidak boleh diawali/diakhiri titik atau titik ganda!"

        val n = nama.trim()
        if (n.isEmpty()) return "Nama tidak boleh kosong!"
        if (!NAME_REGEX.matches(n)) return "Nama 3-50 karakter dan hanya berisi huruf!"

        if (noTelp.isBlank()) return "Nomor telepon tidak boleh kosong!"
        if (!PhoneUtils.isValid(noTelp, country)) return "Nomor telepon tidak valid untuk ${country.nama}!"
        return null
    }

    fun checkValidationRegister(
        username: String,
        nama: String,
        noTelp: String,
        country: Country,
        password: String,
        confirmPassword: String
    ): String? {
        checkValidationProfile(username, nama, noTelp, country)?.let { return it }
        checkValidationPassword(password)?.let { return it }
        if (password != confirmPassword) return "Password tidak sesuai!"
        return null
    }

    /*fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
     */


}
