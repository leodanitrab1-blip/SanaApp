package com.sana.app.ui.admin

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.repository.GameRecord
import com.sana.app.core.repository.SchoolRecord
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.File

data class SavedGame(val title: String, val description: String, val category: String, val fileName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var teacherCount by remember { mutableStateOf("") }
    var generatedCodes by remember { mutableStateOf("") }
    var schools by remember { mutableStateOf(repo.getAllSchools()) }
    var users by remember { mutableStateOf(repo.getAllUsers()) }
    var isSyncing by remember { mutableStateOf(false) }
    var savedGames by remember { mutableStateOf(loadGames(context)) }
    var gameTitle by remember { mutableStateOf("") }
    var gameDescription by remember { mutableStateOf("") }
    var gameCategory by remember { mutableStateOf("GENERAL") }
    var selectedGame by remember { mutableStateOf<SavedGame?>(null) }
    var moderateTarget by remember { mutableStateOf("") }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val input = context.contentResolver.openInputStream(it)
                val content = input?.bufferedReader()?.readText() ?: ""
                input?.close()
                if (content.isNotBlank() && gameTitle.isNotBlank()) {
                    val fn = gameTitle.lowercase().replace(" ", "_") + ".html"
                    File(context.filesDir, "games").mkdirs()
                    File(context.filesDir, "games/$fn").writeText(content)
                    val games = loadGames(context).toMutableList()
                    games.add(SavedGame(gameTitle, gameDescription.ifBlank { "Juego" }, gameCategory, fn))
                    saveGames(context, games)
                    repo.saveGame(GameRecord(gameTitle, gameDescription, gameCategory, fn))
                    savedGames = games
                    message = "✅ Juego subido"
                    gameTitle = ""; gameDescription = ""
                }
            } catch (e: Exception) { message = "Error: ${e.message}" }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "register" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Registrar Escuela", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de la escuela") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = directorName, onValueChange = { directorName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del director") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = teacherCount, onValueChange = { teacherCount = it.filter { c -> c.isDigit() } }, modifier = Modifier.fillMaxWidth(), label = { Text("Nro de docentes") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = {
                        if (schoolName.isNotBlank() && directorName.isNotBlank()) {
                            val sc = repo.generateCode("ESC"); val ac = repo.generateCode("ADM")
                            val n = teacherCount.toIntOrNull() ?: 0; val tcs = mutableListOf<String>()
                            for (i in 1..n) { val tc = repo.generateCode("DOC"); tcs.add(tc); repo.saveUser(UserRecord(code = tc, role = Constants.ROLE_TEACHER, name = "Docente $i", schoolCode = sc)) }
                            repo.saveUser(UserRecord(code = ac, role = Constants.ROLE_DIRECTOR, name = directorName, schoolCode = sc))
                            repo.saveSchool(SchoolRecord(code = sc, name = schoolName, adminCode = ac, directorName = directorName, teacherCount = n, teacherCodes = tcs))
                            generatedCodes = "ESCUELA: $sc\nDIRECTOR: $ac\nDOCENTES: ${tcs.joinToString(", ")}"
                            message = "✅ Escuela registrada en Firebase"
                            schools = repo.getAllSchools(); users = repo.getAllUsers()
                            schoolName = ""; directorName = ""; teacherCount = ""
                        } else { message = "Completa todos los campos" }
                    }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Registrar y Guardar ☁️", fontWeight = FontWeight.Bold) }
                    if (message.isNotEmpty()) { Spacer(Modifier.height(12.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (message.startsWith("✅")) DarkPalette.SuccessContainer else DarkPalette.ErrorContainer)) { Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) } }
                    if (generatedCodes.isNotEmpty()) { Spacer(Modifier.height(12.dp)); Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.PrimaryContainer)) { Column(modifier = Modifier.padding(20.dp)) { Text("CODIGOS GENERADOS:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(8.dp)); Text(generatedCodes, style = MaterialTheme.typography.bodyMedium) } } }
                }
            }
            
            "schools" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Escuelas (${schools.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { schools = repo.getAllSchools() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Secondary)) { Text("Actualizar") }
                    Spacer(Modifier.height(12.dp))
                    if (schools.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay escuelas", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val size = schools.size
                            for (i in 0 until size) {
                                val s = schools[i]
                                item(key = "school_$i") {
                                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.School, null, tint = DarkPalette.Primary, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(8.dp)); Text(s.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
                                            Spacer(Modifier.height(8.dp))
                                            Text("Escuela: ${s.code}", style = MaterialTheme.typography.bodySmall)
                                            Text("Director: ${s.adminCode} (${s.directorName})", style = MaterialTheme.typography.bodySmall)
                                            Text("Docentes: ${s.teacherCount}", style = MaterialTheme.typography.bodySmall)
                                            if (s.teacherCodes.isNotEmpty()) { Spacer(Modifier.height(4.dp)); s.teacherCodes.forEach { tc -> Text("  - $tc", style = MaterialTheme.typography.labelSmall, color = DarkPalette.Primary) } }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            "codes" -> {
                var selectedRole by remember { mutableStateOf("ALL") }
                val roles = listOf("ALL", "ADMIN", "DIRECTOR", "TEACHER", "STUDENT", "PARENT")
                val filtered = if (selectedRole == "ALL") users else users.filter { it.role == selectedRole }
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Codigos (${users.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { users = repo.getAllUsers() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Tertiary)) { Text("Actualizar") }
                    Spacer(Modifier.height(8.dp))
                    ScrollableTabRow(selectedTabIndex = roles.indexOf(selectedRole)) { roles.forEach { role -> Tab(selected = selectedRole == role, onClick = { selectedRole = role }, text = { Text(if (role == "ALL") "Todos" else role) }) } }
                    Spacer(Modifier.height(8.dp))
                    if (filtered.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay codigos", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val size = filtered.size
                            for (i in 0 until size) {
                                val u = filtered[i]
                                item(key = "code_$i") {
                                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Column(modifier = Modifier.weight(1f)) { Text(u.code, fontWeight = FontWeight.Bold); Text(u.name + " (" + u.role + ")", style = MaterialTheme.typography.bodySmall) }
                                            Surface(shape = RoundedCornerShape(6.dp), color = if (u.active) DarkPalette.Success.copy(alpha = 0.2f) else DarkPalette.Error.copy(alpha = 0.2f)) { Text(if (u.active) "Activo" else "Inactivo", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (u.active) DarkPalette.Success else DarkPalette.Error) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            "games" -> {
                if (selectedGame != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(title = { Text(selectedGame!!.title, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { selectedGame = null }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.InfoContainer))
                        AndroidView(factory = { ctx -> WebView(ctx).apply { webViewClient = WebViewClient(); settings.javaScriptEnabled = true; settings.domStorageEnabled = true; settings.allowFileAccess = true; val f = File(ctx.filesDir, "games/" + selectedGame!!.fileName); if (f.exists()) loadUrl("file://" + f.absolutePath) } }, modifier = Modifier.fillMaxSize())
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Gestionar Juegos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.height(16.dp))
                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Subir nuevo juego HTML", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(value = gameTitle, onValueChange = { gameTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Titulo") }, shape = RoundedCornerShape(12.dp))
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = gameDescription, onValueChange = { gameDescription = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Descripcion") }, shape = RoundedCornerShape(12.dp))
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("GENERAL" to "General", "PRIMARY" to "Primaria", "SECONDARY" to "Secundaria").forEach { (v, l) -> FilterChip(selected = gameCategory == v, onClick = { gameCategory = v }, label = { Text(l) }) } }
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { filePicker.launch("text/html") }, modifier = Modifier.fillMaxWidth(), enabled = gameTitle.isNotBlank(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Info)) { Text("Seleccionar archivo HTML") }
                            }
                        }
                        if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (message.startsWith("✅")) DarkPalette.SuccessContainer else DarkPalette.ErrorContainer)) { Text(message, modifier = Modifier.padding(12.dp)) } }
                        Spacer(Modifier.height(16.dp))
                        Text("Juegos guardados (" + savedGames.size + ")", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (savedGames.isEmpty()) { Text("No hay juegos. Sube uno.", color = DarkPalette.TextMuted) }
                        else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val size = savedGames.size
                                for (i in 0 until size) {
                                    val g = savedGames[i]
                                    item(key = "game_$i") {
                                        Card(modifier = Modifier.fillMaxWidth().clickable { selectedGame = g }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Games, null, tint = DarkPalette.Primary, modifier = Modifier.size(24.dp))
                                                Spacer(Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) { Text(g.title, fontWeight = FontWeight.Bold); Text(g.description, style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted) }
                                                IconButton(onClick = { savedGames = savedGames.filter { it.fileName != g.fileName }; saveGames(context, savedGames) }) { Icon(Icons.Default.Delete, "Eliminar", tint = DarkPalette.Error) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            "moderate" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Moderar Contenido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(20.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Dar de baja por codigo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = moderateTarget, onValueChange = { moderateTarget = it.uppercase() }, modifier = Modifier.fillMaxWidth(), label = { Text("Codigo a dar de baja") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { if (moderateTarget.isNotBlank()) { repo.deactivateUser(moderateTarget); message = "✅ " + moderateTarget + " dado de baja"; users = repo.getAllUsers(); moderateTarget = "" } else { message = "Ingresa un codigo" } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Error)) { Text("Dar de baja") }
                        }
                    }
                    if (message.isNotEmpty()) { Spacer(Modifier.height(12.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (message.startsWith("✅")) DarkPalette.SuccessContainer else DarkPalette.ErrorContainer)) { Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) } }
                }
            }
            
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("Administracion", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))
                    
                    Button(onClick = { scope.launch { isSyncing = true; val c = repo.syncAll(); schools = repo.getAllSchools(); users = repo.getAllUsers(); savedGames = loadGames(context); message = "Sincronizados " + c + " registros"; isSyncing = false } }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Info), shape = RoundedCornerShape(12.dp)) {
                        if (isSyncing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = androidx.compose.ui.graphics.Color.White) else Icon(Icons.Default.CloudSync, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp)); Text(if (isSyncing) "Sincronizando..." else "Sincronizar desde la nube ☁️", fontWeight = FontWeight.Bold)
                    }
                    if (message.isNotEmpty() && currentScreen == "main") { Text(message, modifier = Modifier.padding(horizontal = 16.dp), color = if (message.startsWith("Sincronizados") || message.startsWith("✅")) DarkPalette.Success else DarkPalette.TextMuted) }
                    
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { MenuCard("Registrar\nEscuela", Icons.Default.AddBusiness, "Crear nueva", DarkPalette.Primary, DarkPalette.PrimaryVariant) { message = ""; currentScreen = "register" } }
                        item { MenuCard("Ver\nEscuelas", Icons.Default.ListAlt, schools.size.toString() + " reg.", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { schools = repo.getAllSchools(); currentScreen = "schools" } }
                        item { MenuCard("Codigos", Icons.Default.Key, users.size.toString() + " usuarios", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { users = repo.getAllUsers(); currentScreen = "codes" } }
                        item { MenuCard("Juegos", Icons.Default.Games, savedGames.size.toString() + " juegos", DarkPalette.Info, DarkPalette.InfoContainer) { message = ""; currentScreen = "games" } }
                        item { MenuCard("Moderar", Icons.Default.Gavel, "Dar de baja", DarkPalette.Warning, DarkPalette.WarningContainer) { message = ""; currentScreen = "moderate" } }
                        item { MenuCard("Salir", Icons.Default.Logout, "Cerrar sesion", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCard(title: String, icon: ImageVector, desc: String, c1: androidx.compose.ui.graphics.Color, c2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center); Text(desc, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center) }
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
