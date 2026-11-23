package com.project.finalmap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.finalmap.data.models.User
import com.project.finalmap.data.vm.AuthVm
import com.project.finalmap.ui.components.AppTextField
import com.project.finalmap.ui.components.PhoneField
import com.project.finalmap.util.security.Helper
import com.project.finalmap.util.security.PhoneUtils

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AuthVm = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf(PhoneUtils.defaultCountry()) }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.PersonAdd, null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Text("Create Account", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Isi data diri untuk mendaftar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        AppTextField(username, { username = it }, "Username", Icons.Default.AlternateEmail)
        AppTextField(name, { name = it }, "Name", Icons.Default.Badge)
        PhoneField(country, { country = it }, phone, { phone = it })
        AppTextField(password, { password = it }, "Password", Icons.Default.Lock, isPassword = true)
        AppTextField(confirmPassword, { confirmPassword = it }, "Confirm Password", Icons.Default.Lock, isPassword = true)

        Button(
            onClick = {
                val error = Helper.checkValidationRegister(username, name, phone, country, password, confirmPassword)
                if (error != null) vm.showError(error)
                else vm.register(
                    User(username = username, nama = name, phone = PhoneUtils.format(phone, country)),
                    password,
                    onRegisterSuccess
                )
            },
            enabled = !vm.isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(50.dp)
        ) {
            if (vm.isLoading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            else Text("Daftar")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have account?")
            TextButton(onClick = onLoginClick) { Text("Login") }
        }
    }
}
