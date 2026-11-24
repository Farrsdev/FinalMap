package com.project.finalmap.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.finalmap.data.models.Country
import com.project.finalmap.util.security.PhoneUtils

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var show by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        singleLine = true,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        visualTransformation = if (isPassword && !show) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { show = !show }) {
                    Icon(
                        if (show) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        if (show) "Sembunyikan password" else "Tampilkan password"
                    )
                }
            }
        } else null,
        modifier = modifier.fillMaxWidth()
    )
}

// Pilih negara (kode dial dari libphonenumber) + input nomor nasional
@Composable
fun PhoneField(
    country: Country,
    onCountryChange: (Country) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .heightIn(min = 61.5.dp)
                .padding(top = 6.dp)
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("${country.flag} +${country.dialCode}")
            Spacer(Modifier.width(3.dp))
            Icon(Icons.Default.ArrowDropDown, "Pilih negara")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 360.dp)
        ) {
            PhoneUtils.countries.forEach { c ->
                DropdownMenuItem(
                    text = { Text("${c.flag}  ${c.nama} (+${c.dialCode})") },
                    onClick = { onCountryChange(c); expanded = false }
                )
            }
        }

        Spacer(Modifier.width(5.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { v -> onPhoneChange(v.filter { it.isDigit() }) },
            label = { Text("Phone") },
            placeholder = { Text("81234567890") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.weight(1f)
        )
    }
}
