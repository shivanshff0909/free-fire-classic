package com.example.freefireclassic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
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
fun CharacterScreen(
    characters: List<GameCharacter>,
    selectedCharacterId: String,
    soundManager: SoundManager,
    onSelectCharacter: (GameCharacter) -> Unit,
    onBack: () -> Unit
) {
    var inspectingChar by remember {
        mutableStateOf(characters.firstOrNull { it.id == selectedCharacterId } ?: characters.first())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F0E14), Color(0xFF181524), Color(0xFF0A090F))
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
                    modifier = Modifier.testTag("character_back_button")
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
                        text = "CHARACTER ROSTER",
                        color = Color(0xFFFFB703),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Equip iconic survivors with active & passive combat skills",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // HORIZONTAL CHARACTER SELECTOR CAROUSEL
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(characters) { ch ->
                    val isInspected = ch.id == inspectingChar.id
                    val isEquipped = ch.id == selectedCharacterId

                    Card(
                        modifier = Modifier
                            .width(86.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isInspected) 2.dp else if (isEquipped) 1.5.dp else 1.dp,
                                color = if (isInspected) Color(0xFFFFB703) else if (isEquipped) Color(0xFF00E676) else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                inspectingChar = ch
                                soundManager.playSkillActive()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A28))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(ch.primaryColor, Color(0xFF222233)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ch.avatarEmoji, fontSize = 26.sp)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = ch.name,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            if (isEquipped) {
                                Text("ACTIVE", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Black)
                            } else {
                                Text(ch.skill.type, color = Color.Gray, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // INSPECTED CHARACTER MAIN PROFILE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, Brush.horizontalGradient(listOf(inspectingChar.primaryColor, Color(0xFF00E5FF))), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141320))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Character Avatar Showcase
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(inspectingChar.primaryColor, Color(0xFF201C30))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(inspectingChar.avatarEmoji, fontSize = 54.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = inspectingChar.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = inspectingChar.title,
                        color = inspectingChar.primaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Bio
                    Text(
                        text = inspectingChar.bio,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // SKILL CARD BREAKDOWN
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1D1B2C))
                            .border(1.dp, inspectingChar.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(inspectingChar.skill.icon, fontSize = 20.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = inspectingChar.skill.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (inspectingChar.skill.type == "Active") Color(0xFF00E5FF) else Color(0xFFFFB703))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${inspectingChar.skill.type.uppercase()} ${if (inspectingChar.skill.cooldownSeconds > 0) "(${inspectingChar.skill.cooldownSeconds}s CD)" else ""}",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = inspectingChar.skill.description,
                                color = Color(0xFFD4D4E0),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Voice quote banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33000000))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💬", fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "\"${inspectingChar.voiceLine}\"",
                                color = Color(0xFFFFD166),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // EQUIP BUTTON
                    val isCurrentlyEquipped = inspectingChar.id == selectedCharacterId
                    Button(
                        onClick = {
                            onSelectCharacter(inspectingChar)
                            soundManager.playSkillActive()
                        },
                        enabled = !isCurrentlyEquipped,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("equip_character_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrentlyEquipped) Color(0xFF00E676) else Color(0xFFFFB703),
                            disabledContainerColor = Color(0xFF1B3B24)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isCurrentlyEquipped) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676))
                                Spacer(Modifier.width(6.dp))
                                Text("CURRENTLY EQUIPPED", color = Color(0xFF00E676), fontWeight = FontWeight.Black)
                            }
                        } else {
                            Text("EQUIP THIS CHARACTER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
