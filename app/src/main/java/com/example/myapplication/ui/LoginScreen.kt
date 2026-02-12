package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.session.SessionViewModel

@Composable
fun LoginScreen(vm: SessionViewModel) {
    var err by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        Text("Endterm", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Quick login (anonymous Firebase)")

        if (err) {
            Spacer(Modifier.height(8.dp))
            Text("Login failed. Check internet / Firebase json.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { vm.signInAnon(onErr = { err = true }) }, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }
    }
}
