package com.example.myapplication.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.vm.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FeedScreen(onOpenDetails: (Double, Double, String) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Feed", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Demo home screen. Use Search to find places.")
        Spacer(Modifier.height(12.dp))
        OutlinedCard {
            Column(Modifier.padding(12.dp)) {
                Text("Tip")
                Text("Open Search tab → type a city → tap item → Details.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
private fun niceTitle(full: String): String =
    full.split(",").firstOrNull()?.trim().orEmpty().ifBlank { full.take(32) }

private fun niceSub(full: String): String =
    full.split(",").drop(1).joinToString(",") { it.trim() }.trim()
        .ifBlank { "—" }
        .take(60)

@Composable
fun SearchScreen2(onOpenDetails: (Double, Double, String) -> Unit) {
    val vm: SearchVm = koinViewModel()
    val ui by vm.ui.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Text("Search", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.q,
            onValueChange = vm::setQuery,
            label = { Text("Type (debounced)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ui.err?.let { Text(it, color = MaterialTheme.colorScheme.tertiary) }

        Spacer(Modifier.height(10.dp))
        if (ui.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        LazyColumn(Modifier.fillMaxWidth()) {
            items(ui.items, key = { it.id }) { p ->
                val title = niceTitle(p.name)
                val sub = niceSub(p.name)

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onOpenDetails(p.lat, p.lon, title) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Spacer(Modifier.height(2.dp))
                        Text(sub, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "lat %.4f, lon %.4f".format(p.lat, p.lon),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                Button(onClick = vm::loadMore, modifier = Modifier.fillMaxWidth(), enabled = ui.q.isNotBlank()) {
                    Text("Load more")
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(onOpenDetails: (Double, Double, String) -> Unit) {
    // To look like friend's "Favorites", we reuse Realtime Board list as "saved notes".
    val vm: BoardVm = koinViewModel()
    val ui by vm.ui.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Text("Favorites", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text("Tap item to edit, Delete to remove. (Realtime Firebase)", style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = ui.input,
            onValueChange = vm::setInput,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (ui.editId == null) "New note" else "Edit note") },
            enabled = ui.ready
        )

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = vm::save, enabled = ui.ready) {
                Text(if (ui.editId == null) "Add" else "Update")
            }
            Spacer(Modifier.width(8.dp))
            if (ui.editId != null) OutlinedButton(onClick = vm::cancelEdit) { Text("Cancel") }
        }

        Spacer(Modifier.height(14.dp))
        LazyColumn {
            items(ui.items, key = { it.id }) { x ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                    Column(Modifier.weight(1f).clickable { vm.pickEdit(x) }) {
                        Text(x.text)
                        Text("tap to edit", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { vm.delete(x.id) }) { Text("Delete") }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun DetailsScreen2(lat: Double, lon: Double, name: String, onOpenComments: () -> Unit) {
    val vm: DetailsVm = koinViewModel(parameters = { parametersOf(lat, lon) })
    val ui by vm.ui.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Text("Details", style = MaterialTheme.typography.titleLarge)
        Text(name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        if (ui.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        ui.forecast?.let { f ->
            Text("Temp: ${f.tempC} °C")
            Text("Wind: ${f.wind} m/s")
        }
        ui.err?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = { vm.refresh(forceRemote = false) }) { Text("Use cache") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { vm.refresh(forceRemote = true) }) { Text("Refresh") }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenComments, modifier = Modifier.fillMaxWidth()) {
            Text("Open comments")
        }
    }
}

@Composable
fun CommentsScreen(name: String) {
    Column(Modifier.padding(16.dp)) {
        Text("Comments", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text("For: $name", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        Text("Demo screen to match navigation structure.", style = MaterialTheme.typography.bodyMedium)
    }
}
