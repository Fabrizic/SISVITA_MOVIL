package com.example.sisvita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sisvita.ui.theme.SISVITATheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SISVITATheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    val tipousuarioid = 1

    val departments = listOf("Department 1", "Department 2", "Department 3")
    val provinces = listOf("Province 1", "Province 2", "Province 3")
    val districts = listOf("District 1", "District 2", "District 3")

    var expandedDepartment by remember { mutableStateOf(false) }
    var expandedProvince by remember { mutableStateOf(false) }
    var expandedDistrict by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido Paterno") },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Apellido Materno") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                TextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Departamento") },
                    modifier = Modifier.fillMaxWidth().clickable { expandedDepartment = true }
                )
                DropdownMenu(
                    expanded = expandedDepartment,
                    onDismissRequest = { expandedDepartment = false }
                ) {
                    departments.forEach { dept ->
                        DropdownMenuItem(onClick = {
                            department = dept
                            expandedDepartment = false
                        }) {
                            Text(dept)
                        }
                    }
                }
            }
            Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                TextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("Provincia") },
                    modifier = Modifier.fillMaxWidth().clickable { expandedProvince = true }
                )
                DropdownMenu(
                    expanded = expandedProvince,
                    onDismissRequest = { expandedProvince = false }
                ) {
                    provinces.forEach { prov ->
                        DropdownMenuItem(onClick = {
                            province = prov
                            expandedProvince = false
                        }) {
                            Text(prov)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("Distrito") },
                modifier = Modifier.fillMaxWidth().clickable { expandedDistrict = true }
            )
            DropdownMenu(
                expanded = expandedDistrict,
                onDismissRequest = { expandedDistrict = false }
            ) {
                districts.forEach { dist ->
                    DropdownMenuItem(onClick = {
                        district = dist
                        expandedDistrict = false
                    }) {
                        Text(dist)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Fecha de Nacimiento") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = dateVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Handle registration logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}

@Composable
fun DropdownMenuItem(onClick: () -> Unit, content: @Composable () -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        text = content
    )
}

@Composable
fun dateVisualTransformation(): VisualTransformation {
    return VisualTransformation { text ->
        val formattedText = buildAnnotatedString {
            append(text.text.chunked(2).joinToString("/"))
        }
        TransformedText(formattedText, OffsetMapping.Identity)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SISVITATheme {
        RegisterScreen()
    }
}