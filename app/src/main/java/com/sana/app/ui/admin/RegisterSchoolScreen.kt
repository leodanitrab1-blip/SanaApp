package com.sana.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.repository.SchoolRecord
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.utils.Constants

@Composable
fun RegisterSchoolScreen(repo: FirebaseRepository, isDark: Boolean, onBack: () -> Unit, onSchoolRegistered: () -> Unit) {
    var schoolName by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var teacherCount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var generatedCodes by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Registrar Escuela", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de la escuela") }, leadingIcon = { Icon(Icons.Default.School, null) }, shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = directorName, onValueChange = { directorName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del director") }, leadingIcon = { Icon(Icons.Default.Person, null) }, shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = teacherCount, onValueChange = { teacherCount = it.filter { c -> c.isDigit() } }, modifier = Modifier.fillMaxWidth(), label = { Text("Número de docentes") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            if (schoolName.isNotBlank() && directorName.isNotBlank()) {
                val sc = repo.generateCode("ESC"); val ac = repo.generateCode("ADM"); val count = teacherCount.toIntOrNull() ?: 0
                val tcs = mutableListOf<String>()
                for (i in 1..count) { val tc = repo.generateCode("DOC"); tcs.add(tc); repo.saveUser(UserRecord(code = tc, role = Constants.ROLE_TEACHER, name = "Docente $i", schoolCode = sc)) }
                repo.saveUser(UserRecord(code = ac, role = Constants.ROLE_DIRECTOR, name = directorName, schoolCode = sc))
                repo.saveSchool(SchoolRecord(code = sc, name = schoolName, adminCode = ac, directorName = directorName, teacherCount = count, teacherCodes = tcs))
                val sb = StringBuilder(); sb.appendLine("🏫 ESCUELA: $sc"); sb.appendLine("👔 DIRECTOR: $ac")
                if (tcs.isNotEmpty()) { sb.appendLine("\n👨‍🏫 DOCENTES:"); tcs.forEachIndexed { i, c -> sb.appendLine("  ${i+1}. $c") } }
                generatedCodes = sb.toString(); message = "✅ Subido a Firebase ☁️"; onSchoolRegistered()
                schoolName = ""; directorName = ""; teacherCount = ""
            } else { message = "⚠️ Completa los campos" }
        }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(8.dp)); Text("Registrar y Subir a la Nube ☁️", fontWeight = FontWeight.Bold) }
        if (message.isNotEmpty()) { Spacer(Modifier.height(16.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (message.startsWith("✅")) DarkPalette.SuccessContainer else DarkPalette.ErrorContainer)) { Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) } }
        if (generatedCodes.isNotEmpty()) { Spacer(Modifier.height(16.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.PrimaryContainer)) { Column(modifier = Modifier.padding(16.dp)) { Text("🔑 CÓDIGOS:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(8.dp)); Text(generatedCodes, style = MaterialTheme.typography.bodyMedium) } } }
    }
}
