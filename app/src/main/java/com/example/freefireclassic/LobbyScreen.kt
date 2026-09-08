package com.example.freefireclassic

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LobbyScreen(
    selectedCharacter: GameCharacter,
    selectedWeapon: Weapon,
    settings: GameSettings,
    soundManager: SoundManager,
    onStartGame: () -> Unit,
    onOpenVault: () -> Unit,
    onOpenCharacters: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Pulse animation for Start Game button and Evo weapon glow
    val infiniteTransition = rememberInfiniteTransition(label = "lobby_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    var activeEmoteText by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (settings.oldFreeFireFilter) {
                        listOf(Color(0xFF140D07), Color(0xFF241505), Color(0xFF0F0B08))
                    } else {
                        listOf(Color(0xFF0D0D12), Color(0xFF181A20), Color(0xFF08080C))
                    }
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP HEADER: Profile, Rank & Currencies
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x801A1A24))
                    .border(1.dp, Color(0x33FFB703), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile Avatar & Rank
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFFFB703), Color(0xFFFB8500)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedCharacter.avatarEmoji, fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SHIVANSH ★",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFB703))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("Lv.75", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "GRANDMASTER 🏆 • 4,850 RP",
                            color = Color(0xFFFFB703),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Unlimited Currencies
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10141D))
                            .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💎", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("999K", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1710))
                            .border(1.dp, Color(0xFFFFB703), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("9.9M", color = Color(0xFFFFB703), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(36.dp).testTag("settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.LightGray)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // TITLE BANNER: Classic Free Fire Nostalgia
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4A1E00), Color(0xFF7A3E00), Color(0xFF331600))
                        )
                    )
                    .border(1.dp, Color(0xFFFFB703), RoundedCornerShape(16.dp))
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.lobby_title),
                        color = Color(0xFFFFC800),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = stringResource(R.string.lobby_subtitle),
                        color = Color(0xFFFFAA00),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // HERO CHARACTER & EQUIPPED EVO WEAPON CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFFFFB703), Color(0xFF00E5FF))), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Character Display
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        selectedCharacter.primaryColor.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedCharacter.avatarEmoji,
                            fontSize = 62.sp,
                            modifier = Modifier.scale(pulseScale)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = selectedCharacter.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "SKILL: ${selectedCharacter.skill.name} (${selectedCharacter.skill.type})",
                        color = selectedCharacter.primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Equipped Weapon Showcase
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D0D14))
                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = glowAlpha), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔥", fontSize = 14.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = selectedWeapon.skinName,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Text(
                                    text = "Damage: ${selectedWeapon.damage} | Rate: +2 | Draco Wings Active",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF00B4D8))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("MAX LVL 7", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    // Emote playback banner if active
                    activeEmoteText?.let { emote ->
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFF007F).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFFF2A6D), RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🕺 Emote Active: $emote!",
                                color = Color(0xFFFF2A6D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // PRIMARY BIG ACTION BUTTON: START BATTLEGROUND
            Button(
                onClick = {
                    soundManager.playSkillActive()
                    onStartGame()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .scale(pulseScale)
                    .testTag("start_battleground_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB703)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.start_battleground),
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // NAVIGATION GRID: Vault, Characters, Settings & Emotes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Unlimited Vault
                Button(
                    onClick = onOpenVault,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("vault_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F2C)),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF7209B7))))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "VAULT 🔥",
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // Characters
                Button(
                    onClick = onOpenCharacters,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("characters_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F2C)),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFFB703), Color(0xFFFF007F))))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👤", fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "ROSTER",
                            color = Color(0xFFFFB703),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Texture & Smooth Engine
                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("texture_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF191D26)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 15.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "60FPS TEXTURE",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick Emote Play
                Button(
                    onClick = {
                        val emotes = listOf("Cobra Superbike 🏍️", "Pirate Flag Plant 🏴‍☠️", "FFWC Throne 👑", "Tea Time ☕")
                        val chosen = emotes.random()
                        activeEmoteText = chosen
                        soundManager.playBooyahVictory()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("emote_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF191D26)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🕺", fontSize = 15.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "PLAY EMOTE",
                            color = Color(0xFFFF2A6D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Nostalgic Notice
            Text(
                text = "✓ ALL WEAPONS UNLOCKED • NO BAN • ORIGINAL BERMUDA RETRO TEXTURES",
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
