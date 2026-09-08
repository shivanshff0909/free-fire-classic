package com.example.freefireclassic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        soundManager = SoundManager(this)

        setContent {
            val darkColorScheme = darkColorScheme(
                primary = Color(0xFFFFB703),
                secondary = Color(0xFF00E5FF),
                tertiary = Color(0xFFFF2A6D),
                background = Color(0xFF0C0C12),
                surface = Color(0xFF14141E)
            )

            MaterialTheme(colorScheme = darkColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Global Game State
                    var selectedCharacter by remember { mutableStateOf(GameRepository.characters.first()) }
                    val weaponsList = remember { GameRepository.createStandardWeapons().toMutableStateList() }
                    var selectedWeapon by remember { mutableStateOf(weaponsList.first()) }
                    val vaultItems = remember { GameRepository.vaultItems.toMutableStateList() }
                    var equippedVaultItemId by remember { mutableStateOf("evo_ak_draco") }
                    var gameSettings by remember { mutableStateOf(GameSettings()) }

                    // Battleground Engine instance
                    var currentEngine by remember {
                        mutableStateOf<BattlegroundEngine?>(null)
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "lobby"
                    ) {
                        composable("lobby") {
                            LobbyScreen(
                                selectedCharacter = selectedCharacter,
                                selectedWeapon = selectedWeapon,
                                settings = gameSettings,
                                soundManager = soundManager,
                                onStartGame = {
                                    currentEngine = BattlegroundEngine(
                                        soundManager = soundManager,
                                        selectedCharacter = selectedCharacter,
                                        settings = gameSettings,
                                        onMatchEnded = { _, _, _, _, _ -> }
                                    )
                                    navController.navigate("battleground")
                                },
                                onOpenVault = { navController.navigate("vault") },
                                onOpenCharacters = { navController.navigate("characters") },
                                onOpenSettings = { navController.navigate("settings") }
                            )
                        }

                        composable("battleground") {
                            val engine = currentEngine ?: remember {
                                BattlegroundEngine(
                                    soundManager = soundManager,
                                    selectedCharacter = selectedCharacter,
                                    settings = gameSettings,
                                    onMatchEnded = { _, _, _, _, _ -> }
                                ).also { currentEngine = it }
                            }

                            BattlegroundScreen(
                                engine = engine,
                                settings = gameSettings,
                                onExitToLobby = {
                                    engine.stop()
                                    navController.popBackStack("lobby", inclusive = false)
                                },
                                onRestartMatch = {
                                    engine.stop()
                                    currentEngine = BattlegroundEngine(
                                        soundManager = soundManager,
                                        selectedCharacter = selectedCharacter,
                                        settings = gameSettings,
                                        onMatchEnded = { _, _, _, _, _ -> }
                                    )
                                    navController.popBackStack()
                                    navController.navigate("battleground")
                                }
                            )
                        }

                        composable("vault") {
                            VaultScreen(
                                itemsList = vaultItems,
                                equippedItemId = equippedVaultItemId,
                                onEquipItem = { item ->
                                    equippedVaultItemId = item.id
                                    soundManager.playBooyahVictory()
                                    // If weapon, sync with selected weapon
                                    if (item.category == ItemCategory.EVO_GUNS) {
                                        val match = weaponsList.firstOrNull { it.id.contains("ak") && item.id.contains("ak") }
                                            ?: weaponsList.firstOrNull { it.id.contains("mp40") && item.id.contains("mp40") }
                                            ?: weaponsList.firstOrNull { it.id.contains("m1887") && item.id.contains("m1887") }
                                            ?: weaponsList.firstOrNull { it.id.contains("awm") && item.id.contains("awm") }
                                        if (match != null) {
                                            selectedWeapon = match
                                        }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("characters") {
                            CharacterScreen(
                                characters = GameRepository.characters,
                                selectedCharacterId = selectedCharacter.id,
                                soundManager = soundManager,
                                onSelectCharacter = { ch ->
                                    selectedCharacter = ch
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            TextureSettingsScreen(
                                currentSettings = gameSettings,
                                soundManager = soundManager,
                                onSaveSettings = { updated ->
                                    gameSettings = updated
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

