package com.project.finalmap.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.project.finalmap.R
import com.project.finalmap.data.models.User
import com.project.finalmap.data.vm.AuthVm
import com.project.finalmap.ui.components.AppTextField
import com.project.finalmap.ui.components.PhoneField
import com.project.finalmap.util.security.Helper
import com.project.finalmap.util.security.PhoneUtils

@Composable
fun ProfileScreen(onLogout: () -> Unit, modifier: Modifier = Modifier, vm: AuthVm = viewModel()) {
    val user by vm.users.collectAsState()
    var editing by remember { mutableStateOf(false) }
    var showAlertLogout by remember { mutableStateOf(false) }
    var showAlertDelete by remember { mutableStateOf(false) }
    var confirmPw by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val nopp = painterResource(R.drawable.nopp)
        AsyncImage(
            model = user?.photo,
            contentDescription = "Foto profil",
            placeholder = nopp,
            error = nopp,
            fallback = nopp,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 16.dp)
                .size(120.dp)
                .clip(CircleShape)
                .border(BorderStroke(3.dp, MaterialTheme.colorScheme.primary), CircleShape)
        )
        Text(user?.nama ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        val u = user ?: return@Column
        if (editing) EditProfileCard(u, vm, onClose = { editing = false })
        else {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    InfoRow(Icons.Default.AlternateEmail, "Username", u.username)
                    HorizontalDivider()
                    InfoRow(Icons.Default.Badge, "Nama", u.nama)
                    HorizontalDivider()
                    InfoRow(Icons.Default.Phone, "Phone Number", u.phone)
                }
            }
            Button(
                onClick = {
                    editing = true
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, null)
                Text("Edit Profile", Modifier.padding(start = 8.dp))
            }

            OutlinedButton(
                onClick = {
                    showAlertDelete = true
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Red,
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Hapus Akun")
            }

            OutlinedButton(
                onClick = {
                    showAlertLogout = true
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Text("Logout", Modifier.padding(start = 8.dp))
            }
        }
    }

    if(showAlertDelete){
        AlertDialog(
            onDismissRequest = {
                showAlertDelete = false
            },
            text = {
                OutlinedTextField(
                    value = confirmPw,
                    onValueChange = {confirmPw = it},
                    label = {
                        Text("Konfirmasi Password")
                    },
                    placeholder = {},
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAlertDelete = false
                    }
                ) {
                    Text("Batal")
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        if (confirmPw.isBlank()) {
                            Toast.makeText(context, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }

                        showAlertDelete = false
                        vm.deleteProfile(
                            confirmPw,
                            onDone = {
                                Toast.makeText(context, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()
                                vm.logout()
                                onLogout()
                            },
                        )
                    }
                ) {
                    Text("Konfirmasi")
                }
            }
        )
    }

    if(showAlertLogout){
        AlertDialog(
            onDismissRequest = {
                showAlertLogout = false
            },
            text = {
                Text("Yakin ingin logout?")
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAlertLogout = false
                    }
                ) {
                    Text("Batal")
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        showAlertLogout = false
                        vm.logout()
                        onLogout()
                    }
                ) {
                    Text("Yakin")
                }
            }
        )

    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        , verticalAlignment = Alignment.CenterVertically)
    {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.padding(start = 16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun EditProfileCard(user: User, vm: AuthVm, onClose: () -> Unit) {
    val (initCountry, initPhone) = remember(user.phone) { PhoneUtils.split(user.phone) }
    var name by remember { mutableStateOf(user.nama) }
    var country by remember { mutableStateOf(initCountry) }
    var phone by remember { mutableStateOf(initPhone) }

    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Edit Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            AppTextField(
                user.username,
                {},
                "Username",
                Icons.Default.AlternateEmail,
                enabled = false)
            AppTextField(
                name,
                { name = it },
                "Name",
                Icons.Default.Badge)
            PhoneField(
                country,
                { country = it },
                phone,
                { phone = it }
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClose) { Text("Batal") }
                Button(
                    onClick = {
                        val error = Helper.checkValidationProfile(user.username, name, phone, country)
                        if (error != null) vm.showError(error)
                        else vm.updateProfile(name, PhoneUtils.format(phone, country), onClose)
                    },
                    enabled = !vm.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}
