package com.project.finalmap.util.security

import android.content.Context
import com.project.finalmap.data.models.Country
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.Locale

object PhoneUtils {
    private lateinit var util: PhoneNumberUtil

    fun initialize(context: Context) {
        util = PhoneNumberUtil.createInstance(context)
    }

    val countries: List<Country>
        get() = util.supportedRegions.map { iso ->
            Country(
                nama = Locale("", iso).displayCountry,
                iso = iso,
                dialCode = util.getCountryCodeForRegion(iso),
                flag = iso.map {
                    String(Character.toChars(0x1F1E6 + it.code - 65))
                }.joinToString("")
            )
        }.sortedBy { it.nama }

    fun isValid(phone: String, country: Country) : Boolean{
        return runCatching {
            val number = util.parse(phone, country.iso)
            util.isValidNumberForRegion(number, country.iso)
        }.getOrDefault(false)
    }

    fun format(phone:String, country: Country) : String{
        return runCatching {
            val number = util.parse(phone, country.iso)

            util.format(
                number,
                PhoneNumberUtil.PhoneNumberFormat.E164
            )
        }.getOrDefault("")
    }

}