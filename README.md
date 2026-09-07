package com.example.freefireclassic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ITEM DATA
data class CollectionItem(val name: String, val type: String, val rarity: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "lobby") {
                composable("lobby") { LobbyScreen(navController) }
                composable("collection") { CollectionScreen { navController.popBackStack() } }
            }
        }
    }
}

@Composable
fun LobbyScreen(navController: androidx.navigation.NavController) {
    Column(
        Modifier.fillMaxSize().background(Color(0xFF0A0A0A)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("FREE FIRE CLASSIC", color = Color(0xFFFFC800), fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text("Unlimited Version - By Shivansh", color = Color.White, fontSize = 12.sp)
        Spacer(Modifier.height(30.dp))
        Button(onClick = { /* start game */ }, modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFFFC800))
        ) { Text("START GAME", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { navController.navigate("collection") }, modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF1E1E1E))
        ) { Text("UNLIMITED COLLECTION VAULT 🔥", color = Color(0xFF00E676), fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Text("No Proxy | All Unlocked | No Ban", color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun CollectionScreen(onBack: () -> Unit) {
    val allItems = listOf(
        CollectionItem("Cobra Rage Bundle", "BUNDLE", "MYTHIC"),
        CollectionItem("Red Criminal Bundle", "BUNDLE", "MYTHIC"),
        CollectionItem("Bunny Warrior", "BUNDLE", "LEGENDARY"),
        CollectionItem("Hip Hop Bundle", "BUNDLE", "LEGENDARY"),
        CollectionItem("AK - Blue Flame Draco", "GUN", "EVO"),
        CollectionItem("MP40 - Flashing Spade", "GUN", "LEGENDARY"),
        CollectionItem("M1887 - One Punch Man", "GUN", "MYTHIC"),
        CollectionItem("AWM - Duke Swallowtail", "GUN", "LEGENDARY"),
        CollectionItem("Cobra Rage Emote", "EMOTE", "MYTHIC"),
        CollectionItem("Gloo - Cobra Rage", "GLOO", "MYTHIC")
    )
    var selectedType by remember { mutableStateOf("ALL") }
    val types = listOf("ALL", "BUNDLE", "GUN", "EMOTE", "GLOO")

    Column(Modifier.fillMaxSize().background(Color(0xFF121212)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(Color(0xFFFFC800))) {
                Text("BACK", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text("VAULT [UNLIMITED]", color = Color(0xFFFFC800), fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { t ->
                FilterChip(selected = selectedType == t, onClick = { selectedType = t },
                    label = { Text(t, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFC800))
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        val filtered = if (selectedType == "ALL") allItems else allItems.filter { it.type == selectedType }
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered.size) { i ->
                val item = filtered[i]
                Card(colors = CardDefaults.cardColors(Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp), modifier = Modifier.height(100.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(item.rarity, color = Color(0xFFFFC800), fontSize = 9.sp)
                        Spacer(Modifier.weight(1f))
                        Box(Modifier.background(Color(0xFF00E676), RoundedCornerShape(4.dp)).padding(4.dp)) {
                            Text("UNLOCKED ✓", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}