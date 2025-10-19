
package dev.miguelehr.conversordedivisas.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import dev.miguelehr.conversordedivisas.data.model.ConversionRecord
import dev.miguelehr.conversordedivisas.data.repo.ConversionsRepo
import dev.miguelehr.conversordedivisas.data.repo.RatesRepo
import dev.miguelehr.conversordedivisas.ui.components.CurrencyDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertScreen(
    onOpenHistory: () -> Unit,
    onLogout: () -> Unit,
    ratesRepo: RatesRepo = RatesRepo(),
    convRepo: ConversionsRepo = ConversionsRepo()
) {
    // Bloquea el botón Atrás aquí
    BackHandler(enabled = true) { /* no-op */ }

    val ctx = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var amount by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("EUR") }
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var loadingRates by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf<Double?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    val currencies = remember { listOf("USD", "EUR", "PEN", "GBP", "JPY") }
    val fallback = remember {
        mapOf("USD" to 1.0, "EUR" to 0.92, "PEN" to 3.75, "GBP" to 0.79, "JPY" to 148.0)
    }

    LaunchedEffect(Unit) {
        ratesRepo.fetchRates(
            onSuccess = { map ->
                rates = if (map.isNotEmpty()) map else fallback
                loadingRates = false
            },
            onError = {
                Toast.makeText(ctx, "Error cargando tasas. Usando valores locales.", Toast.LENGTH_SHORT).show()
                rates = fallback
                loadingRates = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversor de Monedas") },
                actions = { TextButton(onClick = onOpenHistory) { Text("Historial") } }
            )
        },
        bottomBar = {
            BottomAppBar {
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil")
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = {
                        menuExpanded = false
                        onLogout()
                    })
                }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            OutlinedTextField(
                value = amount,
                onValueChange = { value -> amount = value.filter { it.isDigit() || it == '.' } },
                label = { Text("Monto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyDropdown(label = "De", value = from, items = currencies, onChange = { from = it })
                CurrencyDropdown(label = "A",  value = to,   items = currencies, onChange = { to   = it })
            }

            Spacer(Modifier.height(16.dp))
            val amountDouble = amount.toDoubleOrNull()
            Button(
                enabled = !loadingRates && amountDouble != null && amountDouble > 0.0 && rates.isNotEmpty(),
                onClick = {
                    val a = amountDouble!!
                    val rFrom = rates[from] ?: 1.0
                    val rTo = rates[to] ?: 1.0
                    val res = a * (rTo / rFrom)
                    result = res

                    if (uid.isNotEmpty()) {
                        val rec = ConversionRecord(uid = uid, amount = a, from = from, to = to, result = res)
                        convRepo.save(
                            rec,
                            onSuccess = { /* ok */ },
                            onError = { Toast.makeText(ctx, "No se guardó el historial", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            ) { Text(if (loadingRates) "Cargando..." else "Convertir") }

            Spacer(Modifier.height(16.dp))
            result?.let { r ->
                Text("${amount.ifBlank { "0" }} $from = ${"%.4f".format(r)} $to", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}