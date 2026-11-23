package com.project.finalmap.util.security

import android.util.Patterns
import com.project.finalmap.data.models.Country

object Helper {
    fun checkValidationPassword(password: String): String? {
        if (!password.any { it.isUpperCase() }) return "Password harus mengandung huruf besar!"
        if (!password.any { it.isLowerCase() }) return "Password harus mengandung huruf kecil!"
        if (!password.any { it.isDigit() }) return "Password harus mengandung angka!"
        if (!password.any { !it.isLetterOrDigit() }) return "Password harus mengandung special karakter!"
        return null
    }

    fun checkValidationRegister(
        nik:String,
        email:String,
        password:String,
        confirmPassword:String,
        noTelp: String,
        country: Country)
            : String? {

        if(nik.length != 16){
            return  "NIK Harus 16 digit"
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Email harus valid!"

        if(!PhoneUtils.isValid(noTelp, country)){
            return "Nomor telepon tidak valid!"
        }
        if(!password.any { it.isUpperCase() }) return "Password harus mengandung huruf besar!"
        if(!password.any { it.isLowerCase()} ) return "Password harus mengandung huruf kecil!"
        if(!password.any {it.isDigit()} ) return "Password harus mengandung angka!"
        if(!password.any { !it.isLetterOrDigit() }) return "Password harus mengandung special karakter!"
        if(password != confirmPassword) return "Password tidak sesuai!"

        return null
    }

}