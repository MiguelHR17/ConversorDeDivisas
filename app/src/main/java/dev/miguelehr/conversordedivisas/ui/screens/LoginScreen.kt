package dev.miguelehr.conversordedivisas.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.miguelehr.conversordedivisas.data.repo.AuthRepo

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onGoToRegister: () -> Unit,     // ⬅️ nuevo parámetro
    auth: AuthRepo = AuthRepo()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val activity = ctx as Activity

    // Botón atrás cierra la app desde Login
    BackHandler(enabled = true) {
        activity.finish()
    }

    Column(Modifier.padding(24.dp)) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            enabled = !loading && email.isNotBlank() && pass.isNotBlank(),
            onClick = {
                loading = true
                auth.signIn(
                    email, pass,
                    onSuccess = { onSuccess(); loading = false },
                    onError = { e ->
                        Toast.makeText(ctx, e.message ?: "Error", Toast.LENGTH_SHORT).show()
                        loading = false
                    }
                )
            }
        ) {
            Text(if (loading) "Ingresando..." else "Ingresar")
        }

        Spacer(Modifier.height(8.dp))

        // ⬇️ Botón para ir al registro
        TextButton(onClick = onGoToRegister) {
            Text("Crear cuenta")
        }
    }
}