package com.project.finalmap.ui.screen

import android.view.RoundedCorner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", fontSize = 24.sp)
        OutlinedTextField(
            value = username,
            onValueChange = {username = it},
            label = {
                Text("Username")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = {
                Text("Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = {phone = it},
            label = {
                Text("Phone")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {
                Text("Password")
            },
            visualTransformation = if(!showPassword) PasswordVisualTransformation() else VisualTransformation.None  ,
            trailingIcon = {
                IconButton(
                    onClick = {
                        showPassword=!showPassword
                    }
                ) {
                    Icon(if(showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {confirmPassword = it},
            label = {
                Text("Confirm Password")
            },
            visualTransformation = if(!showPassword) PasswordVisualTransformation() else VisualTransformation.None  ,
            trailingIcon = {
                IconButton(
                    onClick = {
                        showPassword=!showPassword
                    }
                ) {
                    Icon(if(showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            Text("Daftar")
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top=10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have account?")
            Text("Login", color = Color.Blue)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
    RegisterScreen()
}