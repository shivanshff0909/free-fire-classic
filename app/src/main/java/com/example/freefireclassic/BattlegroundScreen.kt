package com.example.freefireclassic

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun BattlegroundScreen(
    engine: BattlegroundEngine,
    settings: GameSettings,
    onExitToLobby: () -> Unit,
    onRestartMatch: () -> Unit
) {
    // Joystick state
    var joystickOffset by remember { mutableStateOf(Offset.Zero) }
    val maxJoystickRadius = 55f

    // Scope zoom animation
    val zoomFactor by animateFloatAsState(
        targetValue = if (engine.isScoping) 1.55f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "zoom_anim"
    )

    // Fire button press recoil scale
    var fireRecoilScale by remember { mutableFloatStateOf(1.0f) }

    // Text measurer for in-game canvas labels
    val textMeasurer = rememberTextMeasurer()

    // Infinite animation for safe zone electric ring & Alok aura
    val infiniteTransition = rememberInfiniteTransition(label = "game_fx")
    val zonePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zone_pulse"
    )
    val auraRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura_rot"
    )

    // Main Engine Lifecycle
    DisposableEffect(Unit) {
        engine.start()
        onDispose {
            engine.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. FULL COMBAT CANVAS (60FPS BATTLEGROUND RENDERING)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Right-side pan for camera aim
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        engine.onAimPan(dragAmount.x * 0.35f, dragAmount.y * 0.35f)
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f

            // Viewport matrix: transform world coordinates to screen
            withTransform({
                translate(centerX, centerY)
                scale(zoomFactor, zoomFactor, Offset.Zero)
                translate(-engine.playerX, -engine.playerY)
            }) {
                // A. BERMUDA TERRAIN TEXTURE & GRID
                val grassColor = if (settings.oldFreeFireFilter) Color(0xFF2E6B30) else Color(0xFF224B28)
                val gridColor = if (settings.oldFreeFireFilter) Color(0xFF38803A) else Color(0xFF1E3F23)

                drawRect(
                    color = grassColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(engine.worldSize, engine.worldSize)
                )

                // Grid lines (retro terrain tiles)
                val tileSize = 80f
                var gx = 0f
                while (gx <= engine.worldSize) {
                    drawLine(gridColor, Offset(gx, 0f), Offset(gx, engine.worldSize), strokeWidth = 1.5f)
                    gx += tileSize
                }
                var gy = 0f
                while (gy <= engine.worldSize) {
                    drawLine(gridColor, Offset(0f, gy), Offset(engine.worldSize, gy), strokeWidth = 1.5f)
                    gy += tileSize
                }

                // B. LANDMARKS & BUILDINGS
                engine.obstacles.forEach { obs ->
                    // Building drop shadow
                    drawRect(
                        color = Color(0x66000000),
                        topLeft = Offset(obs.x + 8f, obs.y + 8f),
                        size = Size(obs.width, obs.height)
                    )
                    // Building body
                    drawRect(
                        color = obs.color,
                        topLeft = Offset(obs.x, obs.y),
                        size = Size(obs.width, obs.height)
                    )
                    // Roof outline
                    drawRect(
                        color = obs.color.copy(alpha = 0.6f),
                        topLeft = Offset(obs.x + 8f, obs.y + 8f),
                        size = Size(obs.width - 16f, obs.height - 16f)
                    )
                }

                // Air-drop Crate at center with red smoke
                val dropX = 800f
                val dropY = 800f
                drawCircle(Color(0x33FF0000), radius = 45f * zonePulse, center = Offset(dropX, dropY))
                drawRect(Color(0xFFD90429), topLeft = Offset(dropX - 18f, dropY - 18f), size = Size(36f, 36f))
                drawRect(Color(0xFFFFB703), topLeft = Offset(dropX - 18f, dropY - 5f), size = Size(36f, 10f))

                // C. DEPLOYED GLOO WALLS (ICY SHIELD TEXTURE)
                engine.deployedGlooWalls.forEach { gloo ->
                    withTransform({
                        translate(gloo.x, gloo.y)
                        rotate(gloo.angle)
                    }) {
                        // Ice glow aura
                        drawRoundRect(
                            brush = Brush.horizontalGradient(listOf(Color(0x6600E5FF), Color(0x9900E5FF), Color(0x6600E5FF))),
                            topLeft = Offset(-42f, -12f),
                            size = Size(84f, 24f),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        // Solid core
                        drawRoundRect(
                            color = Color(0xFF00B4D8),
                            topLeft = Offset(-38f, -8f),
                            size = Size(76f, 16f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                        // Cobra shield crest
                        drawCircle(Color(0xFFFF2A6D), radius = 5f, center = Offset.Zero)
                    }
                }

                // D. SAFE ZONE (ELECTRIC BLUE CIRCLE)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color(0x330099FF), Color(0x6600E5FF)),
                        center = Offset(engine.zoneCenterX, engine.zoneCenterY),
                        radius = engine.zoneRadius
                    ),
                    radius = engine.zoneRadius,
                    center = Offset(engine.zoneCenterX, engine.zoneCenterY)
                )
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = zonePulse),
                    radius = engine.zoneRadius,
                    center = Offset(engine.zoneCenterX, engine.zoneCenterY),
                    style = Stroke(width = 4.5f)
                )

                // E. DJ ALOK / CHRONO ACTIVE SKILL VISUALS
                if (engine.isSkillActive) {
                    if (engine.selectedCharacter.id == "alok") {
                        // Glowing green audio equalizer circle
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0x5500FF88), Color(0x1100FF88), Color.Transparent),
                                center = Offset(engine.playerX, engine.playerY),
                                radius = 95f
                            ),
                            radius = 95f,
                            center = Offset(engine.playerX, engine.playerY)
                        )
                        drawCircle(
                            color = Color(0xFF00FF88),
                            radius = 95f,
                            center = Offset(engine.playerX, engine.playerY),
                            style = Stroke(width = 2.5f)
                        )
                    } else if (engine.selectedCharacter.id == "chrono") {
                        // Cyan Forcefield Dome
                        drawCircle(
                            color = Color(0x5500E5FF),
                            radius = 70f,
                            center = Offset(engine.playerX, engine.playerY)
                        )
                        drawCircle(
                            color = Color(0xFF00E5FF),
                            radius = 70f,
                            center = Offset(engine.playerX, engine.playerY),
                            style = Stroke(width = 3.5f)
                        )
                    }
                }

                // F. ENEMY BOTS
                engine.enemyBots.filter { it.isAlive }.forEach { bot ->
                    withTransform({
                        translate(bot.x, bot.y)
                        rotate(bot.angle)
                    }) {
                        // Bot shadow
                        drawCircle(Color(0x55000000), radius = 18f, center = Offset(3f, 3f))
                        // Bot Body (Outfit)
                        drawCircle(Color(0xFFE63946), radius = 16f, center = Offset.Zero)
                        // Bot Head
                        drawCircle(Color(0xFFFFD166), radius = 9f, center = Offset(6f, 0f))
                        // Bot Gun Barrel
                        drawLine(Color(0xFF333333), Offset(6f, 0f), Offset(28f, 0f), strokeWidth = 4f)
                    }

                    // Bot Nametag & Health bar
                    val botHpPercent = (bot.hp.toFloat() / bot.maxHp.toFloat()).coerceIn(0f, 1f)
                    val barWidth = 40f
                    drawRect(Color(0x99000000), Offset(bot.x - barWidth / 2, bot.y - 28f), Size(barWidth, 5f))
                    drawRect(
                        if (botHpPercent > 0.4f) Color(0xFF00E676) else Color(0xFFFF2A6D),
                        Offset(bot.x - barWidth / 2, bot.y - 28f),
                        Size(barWidth * botHpPercent, 5f)
                    )
                }

                // G. PLAYER CHARACTER MODEL
                withTransform({
                    translate(engine.playerX, engine.playerY)
                    rotate(engine.playerAngle)
                }) {
                    // Player shadow
                    drawCircle(Color(0x66000000), radius = 20f, center = Offset(4f, 4f))

                    // Tactical Outfit (Red Criminal or Kelly Yellow or Alok Gold)
                    val bodyColor = if (engine.selectedCharacter.id == "kelly") {
                        Color(0xFFFFD166)
                    } else if (engine.selectedCharacter.id == "hayato") {
                        Color(0xFF4361EE)
                    } else {
                        Color(0xFFFF2A6D) // Red criminal jacket
                    }

                    drawCircle(bodyColor, radius = 17f, center = Offset.Zero)

                    // Head / Mask
                    drawCircle(Color(0xFFFFD166), radius = 10f, center = Offset(7f, 0f))

                    // Equipped Gun Model & Evo Wings
                    val curWeapon = engine.weapons[engine.selectedWeaponIndex]
                    if (curWeapon.isEvo) {
                        // Dragon wing flare
                        drawLine(Color(0xFF00E5FF), Offset(10f, -14f), Offset(22f, -22f), strokeWidth = 3f)
                        drawLine(Color(0xFF00E5FF), Offset(10f, 14f), Offset(22f, 22f), strokeWidth = 3f)
                    }
                    // Gun Barrel
                    drawLine(Color(0xFF1E1E1E), Offset(7f, 0f), Offset(32f, 0f), strokeWidth = 4.5f)

                    // Laser Guide when aiming
                    if (engine.isScoping) {
                        drawLine(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color.Transparent)),
                            start = Offset(32f, 0f),
                            end = Offset(400f, 0f),
                            strokeWidth = 1.5f
                        )
                    }
                }

                // H. BULLET TRACERS
                engine.bulletTracers.forEach { tracer ->
                    drawLine(
                        color = if (tracer.isDragonFire) Color(0xFF00E5FF) else tracer.color,
                        start = Offset(tracer.startX, tracer.startY),
                        end = Offset(tracer.endX, tracer.endY),
                        strokeWidth = if (tracer.isDragonFire) 3.5f else 2.2f,
                        cap = StrokeCap.Round
                    )
                }

                // I. FLOATING DAMAGE NUMBERS
                engine.floatingDamages.forEach { dmg ->
                    val textColor = if (dmg.isHeal) {
                        Color(0xFF00FF88)
                    } else if (dmg.isHeadshot) {
                        Color(0xFFFF2A6D)
                    } else {
                        Color(0xFFFFC800)
                    }
                    val measured = textMeasurer.measure(
                        text = dmg.text,
                        style = TextStyle(
                            color = textColor,
                            fontSize = if (dmg.isHeadshot) 16.sp else 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = dmg.text,
                        topLeft = Offset(dmg.x - measured.size.width / 2, dmg.y),
                        style = TextStyle(
                            color = textColor,
                            fontSize = if (dmg.isHeadshot) 16.sp else 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }

            // CROSSHAIR IN SCREEN CENTER (WHEN SCOPING)
            if (engine.isScoping) {
                val crossColor = Color(0xFFFF2A6D)
                drawCircle(crossColor.copy(alpha = 0.4f), radius = 24f, center = Offset(centerX, centerY), style = Stroke(1.5f))
                drawLine(crossColor, Offset(centerX - 30f, centerY), Offset(centerX - 8f, centerY), strokeWidth = 2f)
                drawLine(crossColor, Offset(centerX + 8f, centerY), Offset(centerX + 30f, centerY), strokeWidth = 2f)
                drawLine(crossColor, Offset(centerX, centerY - 30f), Offset(centerX, centerY - 8f), strokeWidth = 2f)
                drawLine(crossColor, Offset(centerX, centerY + 8f), Offset(centerX, centerY + 30f), strokeWidth = 2f)
                drawCircle(crossColor, radius = 3f, center = Offset(centerX, centerY))
            }
        }

        // 2. COMBAT HUD OVERLAY

        // TOP BAR: Alive Counter, Kill Feed & Minimap
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Survivors Alive & Kills
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC0D0D14))
                            .border(1.dp, Color(0xFFFFB703), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "ALIVE: ${engine.survivorsLeft}",
                            color = Color(0xFFFFB703),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC0D0D14))
                            .border(1.dp, Color(0xFFFF2A6D), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "KILLS: ${engine.kills} 💀 (${engine.headshots} HS)",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Kill notifications ticker
                engine.killNotifications.takeLast(2).forEach { notif ->
                    Text(
                        text = "${notif.killer} ➔ ${notif.victim} [${if (notif.isHeadshot) "💥HS" else "KILL"}]",
                        color = if (notif.isHeadshot) Color(0xFFFF2A6D) else Color(0xFFFFD166),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Right: Tactical Minimap Radar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC0A0F1A))
                    .border(1.5.dp, Color(0xFF00E5FF), CircleShape)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val mapScale = size.width / engine.worldSize
                    // Safe zone ring
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = engine.zoneRadius * mapScale,
                        center = Offset(engine.zoneCenterX * mapScale, engine.zoneCenterY * mapScale),
                        style = Stroke(1.5f)
                    )
                    // Enemy bot dots
                    engine.enemyBots.filter { it.isAlive }.forEach { bot ->
                        drawCircle(
                            color = Color(0xFFFF2A6D),
                            radius = 2.5f,
                            center = Offset(bot.x * mapScale, bot.y * mapScale)
                        )
                    }
                    // Player position
                    drawCircle(
                        color = Color(0xFF00FF88),
                        radius = 4f,
                        center = Offset(engine.playerX * mapScale, engine.playerY * mapScale)
                    )
                }
            }
        }

        // BOTTOM HUD: Controls (Joystick, Weapons, Actions)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp, start = 14.dp, end = 14.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // HEALTH & EP STATUS BARS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    // EP Bar (Yellow)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("EP", color = Color(0xFFFFB703), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(engine.playerEp / 200f)
                                    .background(Color(0xFFFFB703))
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text("${engine.playerEp}/200", color = Color(0xFFFFB703), fontSize = 9.sp)
                    }

                    Spacer(Modifier.height(4.dp))

                    // HP Bar (Green/Red)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0x66000000))
                        ) {
                            val hpRatio = (engine.playerHp / 200f).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(hpRatio)
                                    .background(
                                        Brush.horizontalGradient(
                                            if (hpRatio > 0.35f) {
                                                listOf(Color(0xFF00E676), Color(0xFF00B4D8))
                                            } else {
                                                listOf(Color(0xFFFF2A6D), Color(0xFFFF5400))
                                            }
                                        )
                                    )
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text("${engine.playerHp}/200", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // DUAL TOUCH CONTROLS ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT SIDE: VIRTUAL JOYSTICK & UTILITIES
                    Column {
                        // Gloo Wall & Medkit Quick Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Gloo Wall Button
                            Button(
                                onClick = { engine.deployGlooWall() },
                                modifier = Modifier
                                    .size(52.dp)
                                    .testTag("gloo_wall_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003852)),
                                shape = CircleShape,
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0077B6)))),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛡️", fontSize = 16.sp)
                                    Text("${engine.glooWallsCount}", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            // Medkit Button
                            Button(
                                onClick = { engine.useMedkit() },
                                modifier = Modifier
                                    .size(52.dp)
                                    .testTag("medkit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14361E)),
                                shape = CircleShape,
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.radialGradient(listOf(Color(0xFF00E676), Color(0xFF1B4332)))),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("➕", fontSize = 16.sp)
                                    Text("${engine.medkitsCount}", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Virtual Analog Joystick
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0x55000000))
                                .border(2.dp, Color(0x66FFFFFF), CircleShape)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            joystickOffset = Offset.Zero
                                        },
                                        onDragCancel = {
                                            joystickOffset = Offset.Zero
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val newOffset = joystickOffset + dragAmount
                                        val dist = hypot(newOffset.x, newOffset.y)
                                        joystickOffset = if (dist > maxJoystickRadius) {
                                            Offset(
                                                newOffset.x / dist * maxJoystickRadius,
                                                newOffset.y / dist * maxJoystickRadius
                                            )
                                        } else {
                                            newOffset
                                        }
                                        engine.onJoystickMove(joystickOffset.x, joystickOffset.y)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner Thumb stick
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (joystickOffset.x).dp / 2f,
                                        y = (joystickOffset.y).dp / 2f
                                    )
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFFFFB703), Color(0xFFFB8500))
                                        )
                                    )
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    // CENTER: WEAPON SWITCHER & AMMO
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        val activeWeapon = engine.weapons[engine.selectedWeaponIndex]

                        // Ammo Counter & Reload
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${activeWeapon.currentAmmo}",
                                color = if (activeWeapon.currentAmmo > 5) Color.White else Color(0xFFFF2A6D),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "/${activeWeapon.maxReserveAmmo}",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = { engine.reloadCurrentWeapon() },
                                modifier = Modifier.size(32.dp).testTag("reload_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = Color(0xFFFFB703))
                            }
                        }

                        // Weapon Switch Bar
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            engine.weapons.forEachIndexed { idx, w ->
                                val isSelected = idx == engine.selectedWeaponIndex
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF33260A) else Color(0x991A1A24))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) Color(0xFFFFB703) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { engine.switchWeapon(idx) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = w.name,
                                        color = if (isSelected) Color(0xFFFFB703) else Color.LightGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // RIGHT SIDE: COMBAT ACTIONS (FIRE, SCOPE, SKILL, CROUCH)
                    Column(horizontalAlignment = Alignment.End) {
                        // Active Skill & Scope row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Character Active Skill Button (Alok / Chrono)
                            Button(
                                onClick = { engine.activateCharacterSkill() },
                                enabled = engine.skillCooldownRemaining == 0,
                                modifier = Modifier
                                    .size(50.dp)
                                    .testTag("skill_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (engine.skillCooldownRemaining == 0) Color(0xFF0077B6) else Color(0xFF2B2B36)
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                if (engine.skillCooldownRemaining > 0) {
                                    Text("${engine.skillCooldownRemaining}s", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text(engine.selectedCharacter.skill.icon, fontSize = 18.sp)
                                }
                            }

                            // Scope ADS Button
                            Button(
                                onClick = { engine.isScoping = !engine.isScoping },
                                modifier = Modifier
                                    .size(50.dp)
                                    .testTag("scope_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (engine.isScoping) Color(0xFFFF007F) else Color(0xFF241530)
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("🎯", fontSize = 18.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Sprint & Crouch row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Sprint (Kelly Dash)
                            Button(
                                onClick = { engine.isSprinting = !engine.isSprinting },
                                modifier = Modifier
                                    .size(46.dp)
                                    .testTag("sprint_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (engine.isSprinting) Color(0xFFFFB703) else Color(0xFF1E1E24)
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("⚡", fontSize = 16.sp)
                            }

                            // Crouch
                            Button(
                                onClick = { engine.isCrouching = !engine.isCrouching },
                                modifier = Modifier
                                    .size(46.dp)
                                    .testTag("crouch_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (engine.isCrouching) Color(0xFF00E5FF) else Color(0xFF1E1E24)
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("🧎", fontSize = 16.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // BIG PRIMARY FIRE BUTTON (WITH RECOIL SPRING)
                        Button(
                            onClick = {
                                fireRecoilScale = 0.88f
                                engine.shootCurrentWeapon()
                            },
                            modifier = Modifier
                                .size(74.dp)
                                .scale(fireRecoilScale)
                                .testTag("fire_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF2A6D)
                            ),
                            shape = CircleShape,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFFFFB703), Color(0xFFFF007F))
                                )
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("🔥", fontSize = 28.sp)
                        }

                        // Reset recoil scale after press
                        LaunchedEffect(fireRecoilScale) {
                            if (fireRecoilScale < 1.0f) {
                                delay(60)
                                fireRecoilScale = 1.0f
                            }
                        }
                    }
                }
            }
        }

        // 3. MATCH END MODAL (BOOYAH! OR ELIMINATED)
        if (engine.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            2.dp,
                            if (engine.isBooyah) Color(0xFFFFB703) else Color(0xFFFF2A6D),
                            RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13131A))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (engine.isBooyah) stringResource(R.string.booyah_title) else stringResource(R.string.defeat_title),
                            color = if (engine.isBooyah) Color(0xFFFFB703) else Color(0xFFFF2A6D),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = if (engine.isBooyah) stringResource(R.string.victory_subtitle) else "BETTER LUCK NEXT MATCH",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        // Match Summary Stats Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1C1C28))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Rank Tier:", color = Color.Gray, fontSize = 12.sp)
                                    Text(if (engine.isBooyah) "+78 RP (GRANDMASTER ★)" else "+15 RP", color = Color(0xFFFFB703), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Kills:", color = Color.Gray, fontSize = 12.sp)
                                    Text("${engine.kills} (${engine.headshots} Headshots)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Damage Dealt:", color = Color.Gray, fontSize = 12.sp)
                                    Text("${engine.totalDamageDealt}", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Survival Time:", color = Color.Gray, fontSize = 12.sp)
                                    Text("${engine.matchSeconds}s", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onExitToLobby,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("exit_lobby_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A38)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.back_to_lobby), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Button(
                                onClick = onRestartMatch,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("play_again_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (engine.isBooyah) Color(0xFFFFB703) else Color(0xFFFF2A6D)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.play_again),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
