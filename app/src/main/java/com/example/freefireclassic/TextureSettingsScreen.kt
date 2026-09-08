package com.example.freefireclassic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextureSettingsScreen(
    currentSettings: GameSettings,
    soundManager: SoundManager,
    onSaveSettings: (GameSettings) -> Unit,
    onBack: () -> Unit
) {
    var texturePreset by remember { mutableStateOf(currentSettings.texturePreset) }
    var oldFreeFireFilter by remember { mutableStateOf(currentSettings.oldFreeFireFilter) }
    var targetFps by remember { mutableIntStateOf(currentSettings.targetFps) }
    var sensitivityGeneral by remember { mutableFloatStateOf(currentSettings.sensitivityGeneral) }
    var sensitivityScope2x by remember { mutableFloatStateOf(currentSettings.sensitivityScope2x) }
    var soundEffects by remember { mutableStateOf(currentSettings.soundEffects) }
    var hapticFeedback by remember { mutableStateOf(currentSettings.hapticFeedback) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0D14), Color(0xFF141924), Color(0xFF090A0F))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFB703)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SMOOTH & TEXTURE ENGINE",
                        color = Color(0xFFFFB703),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Customize 60/90 FPS, retro Bermuda textures & touch control",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // SECTION 1: GRAPHICS PRESET
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GRAPHICS QUALITY PRESET",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf("Smooth", "Standard", "Ultra HD", "MAX")
                        presets.forEach { preset ->
                            val isSelected = preset == texturePreset
                            Button(
                                onClick = { texturePreset = preset },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFFFFB703) else Color(0xFF222838)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = preset,
                                    color = if (isSelected) Color.Black else Color.LightGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // SECTION 2: OLD FREE FIRE NOSTALGIC TEXTURE FILTER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "OLD FREE FIRE RETRO TEXTURE FILTER",
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Restores the nostalgic 2018-2020 Bermuda saturated grass, warm sunset lighting, and classic yellow crosshair bloom.",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = oldFreeFireFilter,
                            onCheckedChange = { oldFreeFireFilter = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00E5FF),
                                checkedTrackColor = Color(0xFF003852)
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // SECTION 3: FRAME RATE (60 / 90 / 120 FPS)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TARGET FRAME RATE (ULTRA SMOOTH)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val fpsOptions = listOf(60, 90, 120)
                        fpsOptions.forEach { fps ->
                            val isSelected = fps == targetFps
                            Button(
                                onClick = { targetFps = fps },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF222838)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$fps FPS",
                                    color = if (isSelected) Color.Black else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // SECTION 4: AIM SENSITIVITY SLIDERS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOUCH AIM SENSITIVITY",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Text("General Look: ${(sensitivityGeneral * 100).toInt()}%", color = Color(0xFFFFB703), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = sensitivityGeneral,
                        onValueChange = { sensitivityGeneral = it },
                        valueRange = 0.3f..1.8f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFFFB703), activeTrackColor = Color(0xFFFFB703))
                    )

                    Spacer(Modifier.height(6.dp))

                    Text("Scope 2X / ADS: ${(sensitivityScope2x * 100).toInt()}%", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = sensitivityScope2x,
                        onValueChange = { sensitivityScope2x = it },
                        valueRange = 0.2f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // SECTION 5: AUDIO & HAPTICS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Weapon Sound Synthesizer", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Switch(checked = soundEffects, onCheckedChange = { soundEffects = it })
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibrations & Recoil Haptics", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Switch(checked = hapticFeedback, onCheckedChange = { hapticFeedback = it })
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // SAVE & APPLY BUTTON
            Button(
                onClick = {
                    val updated = currentSettings.copy(
                        texturePreset = texturePreset,
                        oldFreeFireFilter = oldFreeFireFilter,
                        targetFps = targetFps,
                        sensitivityGeneral = sensitivityGeneral,
                        sensitivityScope2x = sensitivityScope2x,
                        soundEffects = soundEffects,
                        hapticFeedback = hapticFeedback
                    )
                    onSaveSettings(updated)
                    soundManager.playBooyahVictory()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "APPLY GRAPHICS & SMOOTH ENGINE",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        }
    }
}
