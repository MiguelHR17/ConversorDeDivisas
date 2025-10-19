package dev.miguelehr.conversordedivisas.ui.screens

import android.icu.text.DateFormat
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import dev.miguelehr.conversordedivisas.data.model.ConversionRecord
import dev.miguelehr.conversordedivisas.data.repo.ConversionsRepo
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    repo: ConversionsRepo = ConversionsRepo()
) {
    val ctx = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var items by remember { mutableStateOf<List<ConversionRecord>>(emptyList()) }

    LaunchedEffect(uid) {
        repo.getMine(
            uid = uid,
            limit = 100,
            onSuccess = { list -> items = list },
            onError = { e ->
                Toast.makeText(ctx, e.message ?: "Error cargando historial", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Historial") }) }) { p ->
        LazyColumn(contentPadding = p) {
            items(items) { rec ->
                ListItem(
                    headlineContent = {
                        Text("${"%.4f".format(rec.amount)} ${rec.from} → ${"%.4f".format(rec.result)} ${rec.to}")
                    },
                    supportingContent = {
                        val df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        Text(df.format(Date(rec.timestamp)))
                    }
                )
                Divider()
            }
        }
    }
}