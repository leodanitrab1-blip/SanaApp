package com.sana.app.ui.admin

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class SavedGame(val title: String, val description: String, val category: String, val fileName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesManagerScreen(
    savedGames: List<SavedGame>,
    isDark: Boolean,
    context: Context,
    onBack: () -> Unit,
    onGamesUpdated: () -> Unit
) {
    var gameTitle by remember { mutableStateOf("") }
    var gameDescription by remember { mutableStateOf("") }
    var gameCategory by remember { mutableStateOf("GENERAL") }
    var message by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<SavedGame?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val content = inputStream?.bufferedReader()?.readText() ?: ""
                inputStream?.close()
                if (content.isNotBlank() && gameTitle.isNotBlank()) {
                    val fileName = "${gameTitle.lowercase().replace(" ", "_")}.html"
                    val gamesDir = File(context.filesDir, "games"); gamesDir.mkdirs()
                    File(gamesDir, fileName).writeText(content)
                    val games = loadGames(context).toMutableList()
                    games.add(SavedGame(gameTitle, gameDescription.ifBlank { "Juego educativo" }, gameCategory, fileName))
                    saveGames(context, games)
                    message = "✅ Juego '$gameTitle' guardado"; gameTitle = ""; gameDescription = ""; onGamesUpdated()
                }
            } catch (e: Exception) { message = "❌ Error: ${e.message}" }
        }
    }

    if (selectedGame != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("🎮 ${selectedGame!!.title}", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { selectedGame = null }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.PrimaryContainer))
            AndroidView(factory = { ctx -> WebView(ctx).apply { webViewClient = WebViewClient(); settings.javaScriptEnabled = true; settings.domStorageEnabled = true; settings.allowFileAccess = true; val f = File(ctx.filesDir, "games/${selectedGame!!.fileName}"); if (f.exists()) loadUrl("file://${f.absolutePath}") } }, modifier = Modifier.fillMaxSize())
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🎮 Gestionar Juegos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(24.dp))
            
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Subir nuevo juego", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = gameTitle, onValueChange = { gameTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título del juego") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = gameDescription, onValueChange = { gameDescription = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Descripción") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Categoría:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("GENERAL", "PRIMARY", "SECONDARY").forEach { cat ->
                            FilterChip(selected = gameCategory == cat, onClick = { gameCategory = cat }, label = { Text(when(cat) { "GENERAL" -> "General"; "PRIMARY" -> "Primaria"; else -> "Secundaria" }) })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { filePickerLauncher.launch("text/html") }, modifier = Modifier.fillMaxWidth(), enabled = gameTitle.isNotBlank(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Icon(Icons.Default.UploadFile, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Seleccionar archivo HTML") }
                }
            }
            
            if (message.isNotEmpty()) { Spacer(Modifier.height(12.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (message.startsWith("✅")) DarkPalette.SuccessContainer else DarkPalette.ErrorContainer)) { Text(message, modifier = Modifier.padding(12.dp)) } }
            Spacer(Modifier.height(24.dp))
            
            Text("Juegos guardados (${savedGames.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (savedGames.isEmpty()) { Text("No hay juegos guardados. Sube tu primer juego HTML.", color = DarkPalette.TextMuted) }
            else {
                savedGames.forEach { game ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedGame = game }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Games, null, tint = DarkPalette.Primary, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(game.title, fontWeight = FontWeight.Bold); Text(game.description, style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted) }; Icon(Icons.Default.PlayArrow, "Jugar", tint = DarkPalette.Success) }
                    }
                }
            }
        }
    }
}

fun loadGames(context: Context): List<SavedGame> {
    val json = context.getSharedPreferences("sana_games", Context.MODE_PRIVATE).getString("games_list", "[]") ?: "[]"
    return try { Gson().fromJson(json, object : TypeToken<List<SavedGame>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
}

fun saveGames(context: Context, games: List<SavedGame>) {
    context.getSharedPreferences("sana_games", Context.MODE_PRIVATE).edit().putString("games_list", Gson().toJson(games)).apply()
}
