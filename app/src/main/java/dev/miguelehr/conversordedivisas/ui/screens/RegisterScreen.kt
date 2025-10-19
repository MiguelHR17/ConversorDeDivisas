package dev.miguelehr.conversordedivisas.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current
    val activity = ctx as Activity
    BackHandler { onCancel() } // atrás → volver a Login

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear cuenta") }) }
    ) { p ->
        Column(Modifier.padding(p).padding(24.dp)) {
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña (min 6)") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirm, onValueChange = { confirm = it },
                label = { Text("Confirmar contraseña") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancel, enabled = !loading) { Text("Cancelar") }
                Button(
                    enabled = !loading && email.isNotBlank() && pass.length >= 6 && pass == confirm,
                    onClick = {
                        loading = true
                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, pass)
                            .addOnSuccessListener {
                                Toast.makeText(ctx, "Cuenta creada. Inicia sesión.", Toast.LENGTH_SHORT).show()
                                loading = false
                                onRegistered() // volver a Login
                            }
                            .addOnFailureListener { e ->
                                val msg = when {
                                    e.message?.contains("already in use", true) == true -> "Ese email ya está registrado."
                                    e.message?.contains("badly formatted", true) == true -> "Formato de email inválido."
                                    e.message?.contains("WEAK_PASSWORD", true) == true -> "La contraseña es muy débil."
                                    else -> e.message ?: "Error creando cuenta."
                                }
                                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                                loading = false
                            }
                    }
                ) { Text(if (loading) "Creando..." else "Registrarme") }
            }
        }
    }
}