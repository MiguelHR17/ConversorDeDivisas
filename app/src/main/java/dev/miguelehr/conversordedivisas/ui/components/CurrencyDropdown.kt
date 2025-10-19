
package dev.miguelehr.conversordedivisas.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    value: String,
    items: List<String>,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier.width(160.dp)
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value, onValueChange = {},
            readOnly = true, label = { Text(label) },
            modifier = Modifier.menuAnchor().then(modifier)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onChange(opt); expanded = false }
                )
            }
        }
    }
}