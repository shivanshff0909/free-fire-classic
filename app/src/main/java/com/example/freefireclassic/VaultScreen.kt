package com.example.freefireclassic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VaultScreen(
    itemsList: List<VaultItem>,
    equippedItemId: String,
    onEquipItem: (VaultItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ItemCategory.ALL) }
    var inspectionItem by remember { mutableStateOf<VaultItem?>(itemsList.firstOrNull()) }

    val filteredItems = remember(selectedCategory, itemsList) {
        if (selectedCategory == ItemCategory.ALL) {
            itemsList
        } else {
            itemsList.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0C0C12), Color(0xFF14141E), Color(0xFF09090D))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // TOP HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("vault_back_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFB703)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VAULT [UNLIMITED]",
                            color = Color(0xFFFFB703),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00E676))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("100% UNLOCKED ✓", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text(
                        text = "All Evo Weapons, Mythic Bundles, Gloo Walls & Emotes",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // CATEGORY FILTER CHIPS
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ItemCategory.entries) { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(Color(0xFFFFB703), Color(0xFFFB8500)))
                                } else {
                                    Brush.horizontalGradient(listOf(Color(0xFF1A1A26), Color(0xFF1A1A26)))
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.White else Color(0x33FFB703),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            color = if (isSelected) Color.Black else Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // LIVE ITEM INSPECTOR PREVIEW CARD
            inspectionItem?.let { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, Brush.horizontalGradient(item.rarity.bgGradient), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161622))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Large Icon Display
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(item.rarity.bgGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.iconEmoji, fontSize = 34.sp)
                        }

                        Spacer(Modifier.width(14.dp))

                        // Info & Attributes
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(item.rarity.color)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(item.rarity.label, color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = item.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.attributeBuffs,
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = item.description,
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                maxLines = 2
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        // Equip Button
                        val isEquipped = item.id == equippedItemId
                        Button(
                            onClick = { onEquipItem(item) },
                            enabled = !isEquipped,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEquipped) Color(0xFF00E676) else Color(0xFFFFB703),
                                disabledContainerColor = Color(0xFF1B3B24)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("equip_item_button")
                        ) {
                            if (isEquipped) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("EQUIPPED", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            } else {
                                Text("EQUIP", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ITEMS GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredItems) { item ->
                    val isEquipped = item.id == equippedItemId
                    val isInspected = item.id == inspectionItem?.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isInspected) 2.dp else if (isEquipped) 1.5.dp else 1.dp,
                                color = if (isInspected) Color.White else if (isEquipped) Color(0xFF00E676) else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                inspectionItem = item
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161622))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(item.rarity.color)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(item.rarity.label, color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }

                                if (isEquipped) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF00E676))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("ACTIVE", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Brush.linearGradient(item.rarity.bgGradient)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.iconEmoji, fontSize = 28.sp)
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = item.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = item.attributeBuffs,
                                color = Color(0xFFFFB703),
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
