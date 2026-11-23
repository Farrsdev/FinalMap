package com.project.finalmap

import com.project.finalmap.data.models.Country
import com.project.finalmap.util.security.Helper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HelperTest {
    private val id = Country("Indonesia", "ID", 62, "")

    @Test
    fun password() {
        assertEquals("Password minimal 8 karakter!", Helper.checkValidationPassword("Ab1!"))
        assertEquals("Password tidak boleh mengandung spasi!", Helper.checkValidationPassword("Abcd 123!"))
        assertEquals("Password harus mengandung special karakter!", Helper.checkValidationPassword("Abcd1234"))
        assertNull(Helper.checkValidationPassword("Abcd123!"))
    }

    // Phone dicek terakhir, jadi kasus username/nama tidak butuh PhoneUtils (Android context)
    @Test
    fun usernameAndName() {
        assertEquals("Username tidak boleh kosong!", Helper.checkValidationProfile(" ", "Budi", "", id))
        assertEquals("Username 4-20 karakter, hanya huruf, angka, _ dan .", Helper.checkValidationProfile("bu di", "Budi", "", id))
        assertEquals("Username tidak boleh diawali/diakhiri titik atau titik ganda!", Helper.checkValidationProfile("bu..di", "Budi", "", id))
        assertEquals("Nama 3-50 karakter dan hanya berisi huruf!", Helper.checkValidationProfile("budi_1", "Budi123", "", id))
        assertEquals("Nomor telepon tidak boleh kosong!", Helper.checkValidationProfile("budi_1", "Budi Santoso", "", id))
    }
}
