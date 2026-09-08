package com.example.freefireclassic

import androidx.compose.ui.graphics.Color

enum class ItemCategory(val displayName: String) {
    ALL("ALL"),
    EVO_GUNS("EVO GUNS 🔥"),
    BUNDLES("BUNDLES 👕"),
    GLOO_WALLS("GLOO WALLS 🛡️"),
    EMOTES("EMOTES 🕺"),
    MELEE("MELEE ⚔️")
}

enum class ItemRarity(val label: String, val color: Color, val bgGradient: List<Color>) {
    EVO("EVO LVL 7", Color(0xFF00E5FF), listOf(Color(0xFF003852), Color(0xFF00B4D8))),
    MYTHIC("MYTHIC", Color(0xFFFF2A6D), listOf(Color(0xFF4A0E2E), Color(0xFFFF007F))),
    LEGENDARY("LEGENDARY", Color(0xFFFFB703), listOf(Color(0xFF432800), Color(0xFFFB8500))),
    RARE("RARE", Color(0xFF8338EC), listOf(Color(0xFF240046), Color(0xFF7B2CBF)))
}

data class VaultItem(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val rarity: ItemRarity,
    val description: String,
    val attributeBuffs: String,
    val isEquipped: Boolean = false,
    val drawableRes: Int? = null,
    val iconEmoji: String = "🔥"
)

data class CharacterSkill(
    val name: String,
    val type: String, // "Active" or "Passive"
    val cooldownSeconds: Int,
    val description: String,
    val icon: String
)

data class GameCharacter(
    val id: String,
    val name: String,
    val title: String,
    val skill: CharacterSkill,
    val bio: String,
    val speedMultiplier: Float = 1.0f,
    val damageResistance: Float = 0.0f,
    val avatarEmoji: String = "👤",
    val primaryColor: Color = Color(0xFFFFB703),
    val voiceLine: String = "Let's rock the battleground!"
)

data class Weapon(
    val id: String,
    val name: String,
    val skinName: String,
    val category: String, // AR, SMG, SG, SR, MELEE
    val damage: Int,
    val headshotMultiplier: Float,
    val fireRateMs: Long,
    val magSize: Int,
    val currentAmmo: Int,
    val maxReserveAmmo: Int,
    val reloadTimeMs: Long,
    val range: Float,
    val spreadAngle: Float,
    val bulletColor: Color,
    val isEvo: Boolean = false,
    val soundFrequency: Int = 400
)

data class GlooWallEntity(
    val id: String,
    val x: Float,
    val y: Float,
    val angle: Float,
    val hp: Int,
    val maxHp: Int = 300,
    val skinName: String = "Cobra Rage",
    val color: Color = Color(0xFF00E5FF)
)

data class EnemyBot(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
    var angle: Float,
    var hp: Int,
    val maxHp: Int = 200,
    val weapon: Weapon,
    var isAlive: Boolean = true,
    var targetX: Float = 0f,
    var targetY: Float = 0f,
    var nextShootTime: Long = 0L,
    var lastMovementChange: Long = 0L,
    val characterName: String = "Kelly"
)

data class FloatingDamage(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val isHeadshot: Boolean,
    val isHeal: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class BulletTracer(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Color,
    val isDragonFire: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class KillNotification(
    val id: Long,
    val killer: String,
    val victim: String,
    val weaponName: String,
    val isHeadshot: Boolean
)

data class GameSettings(
    val texturePreset: String = "Ultra HD", // Smooth, Standard, Ultra HD, MAX
    val oldFreeFireFilter: Boolean = true, // Retro 2018-2020 Bermuda saturated lighting
    val targetFps: Int = 60, // 60, 90, 120
    val sensitivityGeneral: Float = 0.85f,
    val sensitivityRedDot: Float = 0.80f,
    val sensitivityScope2x: Float = 0.75f,
    val sensitivityScope4x: Float = 0.70f,
    val soundEffects: Boolean = true,
    val hapticFeedback: Boolean = true
)
