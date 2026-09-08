package com.example.freefireclassic

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.*
import kotlin.random.Random

data class MapObstacle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val name: String,
    val color: Color
)

class BattlegroundEngine(
    val soundManager: SoundManager,
    val selectedCharacter: GameCharacter,
    val settings: GameSettings,
    val onMatchEnded: (isBooyah: Boolean, kills: Int, headshots: Int, damageDealt: Int, survivalSeconds: Int) -> Unit
) {
    private val idGen = AtomicLong(1000)

    // World bounds
    val worldSize = 1600f

    // Player State
    var playerX by mutableFloatStateOf(800f)
    var playerY by mutableFloatStateOf(800f)
    var playerAngle by mutableFloatStateOf(0f)
    var playerHp by mutableIntStateOf(200)
    var playerEp by mutableIntStateOf(100)
    var isAlive by mutableStateOf(true)
    var isSprinting by mutableStateOf(false)
    var isCrouching by mutableStateOf(false)
    var isScoping by mutableStateOf(false)
    var isShooting by mutableStateOf(false)
    var isSkillActive by mutableStateOf(false)
    var skillCooldownRemaining by mutableIntStateOf(0)

    // Inventory & Ammo
    var medkitsCount by mutableIntStateOf(10)
    var glooWallsCount by mutableIntStateOf(15)
    val weapons = mutableStateListOf<Weapon>()
    var selectedWeaponIndex by mutableIntStateOf(0)

    // Match State
    var survivorsLeft by mutableIntStateOf(50)
    var kills by mutableIntStateOf(0)
    var headshots by mutableIntStateOf(0)
    var totalDamageDealt by mutableIntStateOf(0)
    var matchSeconds by mutableIntStateOf(0)
    var isBooyah by mutableStateOf(false)
    var isGameOver by mutableStateOf(false)

    // Safe Zone
    var zoneCenterX by mutableFloatStateOf(800f)
    var zoneCenterY by mutableFloatStateOf(800f)
    var zoneRadius by mutableFloatStateOf(750f)
    var targetZoneRadius by mutableFloatStateOf(200f)
    var isZoneShrinking by mutableStateOf(false)

    // Game Entities
    val enemyBots = mutableStateListOf<EnemyBot>()
    val deployedGlooWalls = mutableStateListOf<GlooWallEntity>()
    val floatingDamages = mutableStateListOf<FloatingDamage>()
    val bulletTracers = mutableStateListOf<BulletTracer>()
    val killNotifications = mutableStateListOf<KillNotification>()

    // Fixed Map Obstacles (Buildings, Clock Tower, Factory, Trees)
    val obstacles = listOf(
        MapObstacle(300f, 300f, 180f, 180f, "Clock Tower Base", Color(0xFF4A3E3D)),
        MapObstacle(1100f, 350f, 220f, 160f, "Factory Depot", Color(0xFF333B42)),
        MapObstacle(400f, 1050f, 160f, 140f, "Peak Mansion", Color(0xFF5A4D41)),
        MapObstacle(1150f, 1100f, 170f, 170f, "Pochinok Warehouse", Color(0xFF3E4349)),
        MapObstacle(750f, 650f, 100f, 100f, "Bimasakti Outpost", Color(0xFF4A5320)),
        MapObstacle(550f, 750f, 40f, 40f, "Bermuda Tree", Color(0xFF1B4332)),
        MapObstacle(950f, 850f, 40f, 40f, "Bermuda Tree", Color(0xFF1B4332)),
        MapObstacle(700f, 450f, 45f, 45f, "Rock Cover", Color(0xFF5C677D)),
        MapObstacle(900f, 1250f, 45f, 45f, "Rock Cover", Color(0xFF5C677D))
    )

    private var lastShootTime = 0L
    private var engineScope: CoroutineScope? = null

    init {
        // Load weapons from repository
        weapons.addAll(GameRepository.createStandardWeapons())
        spawnBots()
    }

    private fun spawnBots() {
        val botNames = listOf(
            "★Viper_99", "ProSniper_FF", "Criminal_Red", "KellyDash_07",
            "AuraHunter", "Titan_Alok", "Shadow_Ninja", "Bermuda_King",
            "Headshot_God", "Chrono_Shield", "Toxic_Beast", "Alpha_Wolf"
        )
        val standardWeapons = GameRepository.createStandardWeapons()
        botNames.forEachIndexed { i, name ->
            val angle = (i * 30) * (PI / 180f)
            val dist = Random.nextFloat() * 400f + 250f
            val bx = (800f + cos(angle) * dist).toFloat().coerceIn(100f, 1500f)
            val by = (800f + sin(angle) * dist).toFloat().coerceIn(100f, 1500f)
            enemyBots.add(
                EnemyBot(
                    id = "bot_$i",
                    name = name,
                    x = bx,
                    y = by,
                    angle = Random.nextFloat() * 360f,
                    hp = 200,
                    maxHp = 200,
                    weapon = standardWeapons[i % standardWeapons.size],
                    characterName = if (i % 2 == 0) "Alok" else "Kelly"
                )
            )
        }
    }

    fun start() {
        engineScope = CoroutineScope(Dispatchers.Default + Job())
        engineScope?.launch {
            gameLoop()
        }
        engineScope?.launch {
            zoneAndMatchTimerLoop()
        }
    }

    fun stop() {
        engineScope?.cancel()
        engineScope = null
    }

    private suspend fun gameLoop() {
        while (isAlive && !isGameOver) {
            val now = System.currentTimeMillis()

            // 1. Update safe zone shrinking
            if (isZoneShrinking && zoneRadius > targetZoneRadius) {
                zoneRadius = (zoneRadius - 0.4f).coerceAtLeast(targetZoneRadius)
            }

            // 2. Safe zone damage check
            val distFromCenter = hypot(playerX - zoneCenterX, playerY - zoneCenterY)
            if (distFromCenter > zoneRadius) {
                // Inside electric blue storm!
                takePlayerDamage(2, isHeadshot = false, isZone = true)
            }

            // 3. Update enemy bots AI
            updateBots(now)

            // 4. Update bullets & floating damage timeouts
            cleanExpiredEntities(now)

            // 5. Alok Drop the beat healing
            if (isSkillActive && selectedCharacter.id == "alok") {
                if (playerHp < 200) {
                    playerHp = (playerHp + 1).coerceAtMost(200)
                }
            }

            delay(16) // ~60 FPS update rate
        }
    }

    private suspend fun zoneAndMatchTimerLoop() {
        while (isAlive && !isGameOver) {
            delay(1000)
            matchSeconds++

            // EP to HP conversion
            if (playerHp < 200 && playerEp > 0) {
                playerHp = (playerHp + 2).coerceAtMost(200)
                playerEp = (playerEp - 1).coerceAtLeast(0)
            }

            // Skill cooldown timer
            if (skillCooldownRemaining > 0) {
                skillCooldownRemaining--
            }

            // Periodic zone announcements
            if (matchSeconds == 15) {
                isZoneShrinking = true
                targetZoneRadius = 450f
            } else if (matchSeconds == 50) {
                targetZoneRadius = 220f
                zoneCenterX = 800f + Random.nextFloat() * 100f - 50f
                zoneCenterY = 800f + Random.nextFloat() * 100f - 50f
            }

            // Background bot-on-bot battle simulation
            if (survivorsLeft > enemyBots.count { it.isAlive } + 1 && Random.nextFloat() < 0.35f) {
                survivorsLeft--
                val mockWeapons = listOf("M1887", "AK-47", "MP40", "AWM", "SCAR")
                val mockKillers = listOf("ToxicPlayer", "ProMax", "Legend_99", "SniperQueen")
                val mockVictims = listOf("Survivor_03", "Bot_Warrior", "NoobSlayer", "Shadow")
                addKillNotification(
                    mockKillers.random(),
                    mockVictims.random(),
                    mockWeapons.random(),
                    isHeadshot = Random.nextBoolean()
                )
            }
        }
    }

    private fun updateBots(now: Long) {
        enemyBots.filter { it.isAlive }.forEach { bot ->
            // Move bot towards target or player
            val distToPlayer = hypot(playerX - bot.x, playerY - bot.y)

            if (distToPlayer < 400f) {
                // Engage player!
                val dx = playerX - bot.x
                val dy = playerY - bot.y
                bot.angle = (atan2(dy, dx) * 180f / PI).toFloat()

                // Shoot at player with realistic cadence
                if (now > bot.nextShootTime) {
                    bot.nextShootTime = now + bot.weapon.fireRateMs + Random.nextLong(200, 600)
                    botFire(bot)
                }

                // Strafe around player
                if (distToPlayer > 120f) {
                    val moveAngle = bot.angle * (PI / 180f)
                    val nextX = bot.x + cos(moveAngle).toFloat() * 1.5f
                    val nextY = bot.y + sin(moveAngle).toFloat() * 1.5f
                    if (!checkObstacleCollision(nextX, nextY, 20f)) {
                        bot.x = nextX
                        bot.y = nextY
                    }
                }
            } else {
                // Roam freely towards safe zone center
                if (now - bot.lastMovementChange > 3000) {
                    bot.lastMovementChange = now
                    bot.targetX = zoneCenterX + (Random.nextFloat() - 0.5f) * zoneRadius
                    bot.targetY = zoneCenterY + (Random.nextFloat() - 0.5f) * zoneRadius
                    val dx = bot.targetX - bot.x
                    val dy = bot.targetY - bot.y
                    bot.angle = (atan2(dy, dx) * 180f / PI).toFloat()
                }

                val moveAngle = bot.angle * (PI / 180f)
                val nextX = bot.x + cos(moveAngle).toFloat() * 1.0f
                val nextY = bot.y + sin(moveAngle).toFloat() * 1.0f
                if (!checkObstacleCollision(nextX, nextY, 20f)) {
                    bot.x = nextX.coerceIn(50f, worldSize - 50f)
                    bot.y = nextY.coerceIn(50f, worldSize - 50f)
                }
            }
        }
    }

    private fun botFire(bot: EnemyBot) {
        val rad = bot.angle * (PI / 180f)
        val bulletEndX = bot.x + cos(rad).toFloat() * bot.weapon.range
        val bulletEndY = bot.y + sin(rad).toFloat() * bot.weapon.range

        // Check if line from bot to bullet intersects player
        val hitPlayer = distanceToLineSegment(playerX, playerY, bot.x, bot.y, bulletEndX, bulletEndY) < 28f

        // Check Gloo Wall deflection
        var blockedByGloo = false
        deployedGlooWalls.forEach { gloo ->
            if (hypot(gloo.x - bot.x, gloo.y - bot.y) < hypot(playerX - bot.x, playerY - bot.y)) {
                if (distanceToLineSegment(gloo.x, gloo.y, bot.x, bot.y, bulletEndX, bulletEndY) < 45f) {
                    blockedByGloo = true
                }
            }
        }

        bulletTracers.add(
            BulletTracer(
                id = idGen.incrementAndGet(),
                startX = bot.x,
                startY = bot.y,
                endX = if (blockedByGloo) (bot.x + playerX) / 2f else if (hitPlayer) playerX else bulletEndX,
                endY = if (blockedByGloo) (bot.y + playerY) / 2f else if (hitPlayer) playerY else bulletEndY,
                color = bot.weapon.bulletColor
            )
        )

        if (hitPlayer && !blockedByGloo) {
            val dmg = (bot.weapon.damage * 0.5f).toInt().coerceAtLeast(10)
            takePlayerDamage(dmg, isHeadshot = false, isZone = false)
        }
    }

    fun onJoystickMove(deltaX: Float, deltaY: Float) {
        if (!isAlive || isGameOver) return
        val len = hypot(deltaX, deltaY)
        if (len > 0.05f) {
            val speedBase = if (isSprinting) 5.5f else if (isCrouching) 2.2f else 3.8f
            val finalSpeed = speedBase * selectedCharacter.speedMultiplier

            val normX = deltaX / len
            val normY = deltaY / len

            val newX = (playerX + normX * finalSpeed).coerceIn(40f, worldSize - 40f)
            val newY = (playerY + normY * finalSpeed).coerceIn(40f, worldSize - 40f)

            if (!checkObstacleCollision(newX, newY, 24f)) {
                playerX = newX
                playerY = newY
            }

            // Face movement direction if not scoping/aiming
            if (!isScoping) {
                playerAngle = (atan2(deltaY, deltaX) * 180f / PI).toFloat()
            }
        }
    }

    fun onAimPan(deltaX: Float, deltaY: Float) {
        if (!isAlive || isGameOver) return
        val sens = if (isScoping) settings.sensitivityScope2x else settings.sensitivityGeneral
        playerAngle = (playerAngle + deltaX * sens).mod(360f)
    }

    fun shootCurrentWeapon() {
        if (!isAlive || isGameOver) return
        val now = System.currentTimeMillis()
        val currentWeapon = weapons[selectedWeaponIndex]

        if (now - lastShootTime < currentWeapon.fireRateMs) return
        if (currentWeapon.currentAmmo <= 0) {
            reloadCurrentWeapon()
            return
        }

        lastShootTime = now
        val updatedWeapon = currentWeapon.copy(currentAmmo = currentWeapon.currentAmmo - 1)
        weapons[selectedWeaponIndex] = updatedWeapon

        // Sound & Haptic
        soundManager.playGunshot(isEvo = currentWeapon.isEvo, isSniper = currentWeapon.category == "SR")
        if (settings.hapticFeedback) {
            soundManager.triggerHaptic(30)
        }

        // Raycast shooting calculation
        val spread = if (isCrouching) currentWeapon.spreadAngle * 0.5f else currentWeapon.spreadAngle
        val angleOffset = (Random.nextFloat() - 0.5f) * spread
        val rad = (playerAngle + angleOffset) * (PI / 180f)

        val bulletEndX = playerX + cos(rad).toFloat() * currentWeapon.range
        val bulletEndY = playerY + sin(rad).toFloat() * currentWeapon.range

        var closestHitBot: EnemyBot? = null
        var closestHitDist = Float.MAX_VALUE
        var isHeadshotHit = false

        enemyBots.filter { it.isAlive }.forEach { bot ->
            val distToLine = distanceToLineSegment(bot.x, bot.y, playerX, playerY, bulletEndX, bulletEndY)
            if (distToLine < 32f) {
                val distFromPlayer = hypot(bot.x - playerX, bot.y - playerY)
                if (distFromPlayer < closestHitDist) {
                    closestHitDist = distFromPlayer
                    closestHitBot = bot
                    // Headshot detection: precise center hit within 12 units
                    isHeadshotHit = distToLine < 13f || (isScoping && distToLine < 18f)
                }
            }
        }

        val actualEndX = closestHitBot?.x ?: bulletEndX
        val actualEndY = closestHitBot?.y ?: bulletEndY

        bulletTracers.add(
            BulletTracer(
                id = idGen.incrementAndGet(),
                startX = playerX,
                startY = playerY,
                endX = actualEndX,
                endY = actualEndY,
                color = currentWeapon.bulletColor,
                isDragonFire = currentWeapon.isEvo
            )
        )

        // Apply Damage
        closestHitBot?.let { bot ->
            val baseDmg = currentWeapon.damage
            val dmg = if (isHeadshotHit) {
                (baseDmg * currentWeapon.headshotMultiplier).toInt()
            } else {
                baseDmg + Random.nextInt(-3, 4)
            }

            bot.hp -= dmg
            totalDamageDealt += dmg

            if (isHeadshotHit) {
                soundManager.playHeadshot()
                if (settings.hapticFeedback) soundManager.triggerHaptic(60)
                headshots++
            }

            floatingDamages.add(
                FloatingDamage(
                    id = idGen.incrementAndGet(),
                    text = if (isHeadshotHit) "💥$dmg HEADSHOT!" else "$dmg",
                    x = bot.x + Random.nextFloat() * 20 - 10,
                    y = bot.y - 30f,
                    isHeadshot = isHeadshotHit
                )
            )

            // Bot knocked down / eliminated
            if (bot.hp <= 0) {
                bot.isAlive = false
                bot.hp = 0
                kills++
                survivorsLeft = (survivorsLeft - 1).coerceAtLeast(1)

                // Reward ammo and medkits
                medkitsCount += 1
                glooWallsCount += 2

                addKillNotification(
                    killer = "YOU (${selectedCharacter.name})",
                    victim = bot.name,
                    weaponName = currentWeapon.skinName,
                    isHeadshot = isHeadshotHit
                )

                // Check Victory (BOOYAH!)
                if (enemyBots.none { it.isAlive } || survivorsLeft <= 1) {
                    triggerBooyah()
                }
            }
        }
    }

    fun deployGlooWall() {
        if (!isAlive || isGameOver || glooWallsCount <= 0) return
        glooWallsCount--

        val rad = playerAngle * (PI / 180f)
        val deployDistance = 55f
        val gx = playerX + cos(rad).toFloat() * deployDistance
        val gy = playerY + sin(rad).toFloat() * deployDistance

        deployedGlooWalls.add(
            GlooWallEntity(
                id = "gloo_${idGen.incrementAndGet()}",
                x = gx,
                y = gy,
                angle = playerAngle + 90f,
                hp = 350
            )
        )

        soundManager.playGlooWallDeploy()
        if (settings.hapticFeedback) soundManager.triggerHaptic(40)
    }

    fun useMedkit() {
        if (!isAlive || isGameOver || medkitsCount <= 0 || playerHp >= 200) return
        medkitsCount--

        val healAmount = if (selectedCharacter.id == "maxim") 85 else 75
        playerHp = (playerHp + healAmount).coerceAtMost(200)
        playerEp = (playerEp + 20).coerceAtMost(200)

        floatingDamages.add(
            FloatingDamage(
                id = idGen.incrementAndGet(),
                text = "+$healAmount HP",
                x = playerX,
                y = playerY - 35f,
                isHeadshot = false,
                isHeal = true
            )
        )

        soundManager.playMedkitHeal()
    }

    fun activateCharacterSkill() {
        if (!isAlive || isGameOver || skillCooldownRemaining > 0) return
        isSkillActive = true
        skillCooldownRemaining = selectedCharacter.skill.cooldownSeconds
        soundManager.playSkillActive()

        engineScope?.launch {
            delay(10000) // Active for 10 seconds
            isSkillActive = false
        }
    }

    fun reloadCurrentWeapon() {
        val currentWeapon = weapons[selectedWeaponIndex]
        if (currentWeapon.currentAmmo >= currentWeapon.magSize) return
        val needed = currentWeapon.magSize - currentWeapon.currentAmmo
        val toReload = needed.coerceAtMost(currentWeapon.maxReserveAmmo)
        weapons[selectedWeaponIndex] = currentWeapon.copy(
            currentAmmo = currentWeapon.currentAmmo + toReload
        )
    }

    fun switchWeapon(index: Int) {
        if (index in weapons.indices) {
            selectedWeaponIndex = index
        }
    }

    private fun takePlayerDamage(amount: Int, isHeadshot: Boolean, isZone: Boolean) {
        if (!isAlive || isGameOver) return

        // Chrono shield protection
        if (isSkillActive && selectedCharacter.id == "chrono") {
            return
        }

        val reducedAmount = (amount * (1.0f - selectedCharacter.damageResistance)).toInt().coerceAtLeast(1)
        playerHp -= reducedAmount

        if (settings.hapticFeedback) {
            soundManager.triggerHaptic(45)
        }

        if (playerHp <= 0) {
            playerHp = 0
            isAlive = false
            isGameOver = true
            onMatchEnded(false, kills, headshots, totalDamageDealt, matchSeconds)
        }
    }

    private fun triggerBooyah() {
        isBooyah = true
        isGameOver = true
        soundManager.playBooyahVictory()
        onMatchEnded(true, kills, headshots, totalDamageDealt, matchSeconds)
    }

    private fun addKillNotification(killer: String, victim: String, weaponName: String, isHeadshot: Boolean) {
        killNotifications.add(
            KillNotification(
                id = idGen.incrementAndGet(),
                killer = killer,
                victim = victim,
                weaponName = weaponName,
                isHeadshot = isHeadshot
            )
        )
        if (killNotifications.size > 5) {
            killNotifications.removeAt(0)
        }
    }

    private fun cleanExpiredEntities(now: Long) {
        bulletTracers.removeAll { now - it.createdAt > 120 }
        floatingDamages.removeAll { now - it.createdAt > 900 }
    }

    private fun checkObstacleCollision(x: Float, y: Float, radius: Float): Boolean {
        obstacles.forEach { obs ->
            if (x + radius > obs.x && x - radius < obs.x + obs.width &&
                y + radius > obs.y && y - radius < obs.y + obs.height
            ) {
                return true
            }
        }
        return false
    }

    private fun distanceToLineSegment(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        if (dx == 0f && dy == 0f) return hypot(px - x1, py - y1)
        val t = (((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy)).coerceIn(0f, 1f)
        val projX = x1 + t * dx
        val projY = y1 + t * dy
        return hypot(px - projX, py - projY)
    }
}
