package dev.miguelehr.conversordedivisas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
    val ctx = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: ""

    var amount by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("EUR") }
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var result by remember { mutableStateOf<Double?>(null) }
    val currencies = remember { listOf("USD", "EUR", "PEN", "GBP", "JPY") }

    LaunchedEffect(Unit) {
        ratesRepo.fetchRates(
            onSuccess = { map -> rates = map },
            onError = { e -> Toast.makeText(ctx, e.message ?: "Error cargando tasas", Toast.LENGTH_SHORT).show() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversor de Monedas") },
                actions = {
                    TextButton(onClick = onOpenHistory) { Text("Historial") }
                    TextButton(onClick = onLogout) { Text("Cerrar sesión") }
                }
            )
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
                CurrencyDropdown(
                    label = "De",
                    value = from,
                    items = currencies,
                    onChange = { from = it }
                )

                CurrencyDropdown(
                    label = "A",
                    value = to,
                    items = currencies,
                    onChange = { to = it }
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                enabled = amount.isNotBlank() && rates.isNotEmpty(),
                onClick = {
                    val a = amount.toDoubleOrNull() ?: 0.0
                    val rFrom = rates[from] ?: 1.0
                    val rTo = rates[to] ?: 1.0
                    val res = a * (rTo / rFrom)
                    result = res

                    if (uid.isNotEmpty()) {
                        val rec = ConversionRecord(
                            uid = uid, amount = a, from = from, to = to, result = res
                        )
                        convRepo.save(
                            rec,
                            onSuccess = {},
                            onError = { e ->
                                Toast.makeText(ctx, e.message ?: "No se guardó historial", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            ) { Text("Convertir") }

            Spacer(Modifier.height(16.dp))
            result?.let { r ->
                Text("${amount.ifBlank { "0" }} $from = ${"%.4f".format(r)} $to",
                    style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}