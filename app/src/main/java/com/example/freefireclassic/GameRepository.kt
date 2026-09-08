package com.example.freefireclassic

import androidx.compose.ui.graphics.Color

object GameRepository {

    val characters = listOf(
        GameCharacter(
            id = "alok",
            name = "DJ Alok",
            title = "The Beat Master",
            skill = CharacterSkill(
                name = "Drop the Beat",
                type = "Active",
                cooldownSeconds = 25,
                description = "Creates a 5m glowing audio aura that increases move speed by 15% and restores 5 HP/sec for 10s.",
                icon = "🎵"
            ),
            bio = "World-renowned DJ who brings heart-pumping beats and life-saving rhythmic aura to the Bermuda battleground.",
            speedMultiplier = 1.15f,
            damageResistance = 0.05f,
            avatarEmoji = "🎧",
            primaryColor = Color(0xFFFFB703),
            voiceLine = "Feel the sound! Let's drop the beat!"
        ),
        GameCharacter(
            id = "kelly",
            name = "Kelly The Swift",
            title = "Track Star",
            skill = CharacterSkill(
                name = "Dash",
                type = "Passive",
                cooldownSeconds = 0,
                description = "Increases sprint speed by 14% permanently. Awakens into deadly first shot burst damage.",
                icon = "⚡"
            ),
            bio = "High school sprinting phenom who dashes through crossfires like lightning in her iconic bright yellow tracksuit.",
            speedMultiplier = 1.25f,
            avatarEmoji = "🏃‍♀️",
            primaryColor = Color(0xFFFFD166),
            voiceLine = "Catch me if you can! Born to run!"
        ),
        GameCharacter(
            id = "hayato",
            name = "Hayato Shimada",
            title = "Legendary Samurai",
            skill = CharacterSkill(
                name = "Bushido",
                type = "Passive",
                cooldownSeconds = 0,
                description = "When HP drops below 80%, armor penetration increases by 12% for each 10% decrease in HP.",
                icon = "⚔️"
            ),
            bio = "Honor-bound modern samurai carrying the ancestral flame sword and unbreakable fighting spirit.",
            damageResistance = 0.10f,
            avatarEmoji = "🗡️",
            primaryColor = Color(0xFFE63946),
            voiceLine = "My blade strikes with ancestral fury!"
        ),
        GameCharacter(
            id = "chrono",
            name = "Chrono",
            title = "Time Traveler",
            skill = CharacterSkill(
                name = "Time Turner",
                type = "Active",
                cooldownSeconds = 35,
                description = "Generates a cybernetic dome shield blocking 800 incoming bullet damage while firing from within.",
                icon = "🛡️"
            ),
            bio = "Futuristic operative equipped with dimension-shifting shield technology from another timeline.",
            avatarEmoji = "🌐",
            primaryColor = Color(0xFF00E5FF),
            voiceLine = "Timeline stabilized! Stand behind my shield!"
        ),
        GameCharacter(
            id = "maxim",
            name = "Maxim",
            title = "Competitive Eater",
            skill = CharacterSkill(
                name = "Gluttony",
                type = "Passive",
                cooldownSeconds = 0,
                description = "Consumes Medkits and Mushrooms 30% faster, getting you back into the fight instantly.",
                icon = "🍔"
            ),
            bio = "Energetic speedster whose rapid metabolism allows him to recover health twice as fast as normal survivors.",
            avatarEmoji = "🍜",
            primaryColor = Color(0xFF06D6A0),
            voiceLine = "Yum! Full health and ready to rumble!"
        ),
        GameCharacter(
            id = "kla",
            name = "Kla",
            title = "Muay Thai Champion",
            skill = CharacterSkill(
                name = "Muay Thai",
                type = "Passive",
                cooldownSeconds = 0,
                description = "Fist damage increased by 400%, dealing instant lethal one-punch knockouts on landing.",
                icon = "🥊"
            ),
            bio = "Master martial artist who went missing from the ring to conquer the ruthless Bermuda survival zone.",
            avatarEmoji = "🥋",
            primaryColor = Color(0xFFFF5400),
            voiceLine = "One strike is all I need!"
        ),
        GameCharacter(
            id = "moco",
            name = "Moco",
            title = "Elite Hacker",
            skill = CharacterSkill(
                name = "Hacker's Eye",
                type = "Passive",
                cooldownSeconds = 0,
                description = "Tags enemies that you hit with bullet fire for 5 seconds on the minimap and radar.",
                icon = "👁️"
            ),
            bio = "Cyber legend known as Chat Noir who tracks every movement across Bermuda with military radar tech.",
            avatarEmoji = "💻",
            primaryColor = Color(0xFF7209B7),
            voiceLine = "Target locked. You can't hide from my radar!"
        )
    )

    val vaultItems = listOf(
        // EVO GUNS
        VaultItem(
            id = "evo_ak_draco",
            name = "AK-47 Blue Flame Draco",
            category = ItemCategory.EVO_GUNS,
            rarity = ItemRarity.EVO,
            description = "Max Level 7 Evo weapon with glowing cyan dragon wings, dragon fire bullet tracers, and custom kill announcement.",
            attributeBuffs = "Damage ++ | Rate of Fire + | Reload Speed -",
            isEquipped = true,
            iconEmoji = "🐉"
        ),
        VaultItem(
            id = "evo_mp40_cobra",
            name = "MP40 Predatory Cobra",
            category = ItemCategory.EVO_GUNS,
            rarity = ItemRarity.EVO,
            description = "Venomous cobra serpent barrel with blazing red muzzle venom and unmatched close-quarter shredding speed.",
            attributeBuffs = "Rate of Fire ++ | Damage + | Reload Speed -",
            isEquipped = true,
            iconEmoji = "🐍"
        ),
        VaultItem(
            id = "evo_m1014_draco",
            name = "M1014 Green Flame Draco",
            category = ItemCategory.EVO_GUNS,
            rarity = ItemRarity.EVO,
            description = "Emerald beast shotgun delivering devastating dragon breath pellets in close range breaches.",
            attributeBuffs = "Damage ++ | Rate of Fire + | Reload -",
            iconEmoji = "🦖"
        ),
        VaultItem(
            id = "evo_scar_megalodon",
            name = "SCAR Megalodon Alpha",
            category = ItemCategory.EVO_GUNS,
            rarity = ItemRarity.EVO,
            description = "Ocean predator design with razor-sharp dorsal fins and hydrostatic armor penetration bullets.",
            attributeBuffs = "Rate of Fire ++ | Damage + | Accuracy +",
            iconEmoji = "🦈"
        ),
        VaultItem(
            id = "m1887_one_punch",
            name = "M1887 One Punch Man",
            category = ItemCategory.EVO_GUNS,
            rarity = ItemRarity.MYTHIC,
            description = "The ultimate nostalgic double-barrel cannon that wipes out opponents with one clean jump-shot.",
            attributeBuffs = "Damage +++ | Movement Speed + | Reload +",
            iconEmoji = "💥"
        ),
        VaultItem(
            id = "awm_duke_swallowtail",
            name = "AWM Duke Swallowtail",
            category = ItemCategory.EVO_GUNS,
            rarity = ItemRarity.LEGENDARY,
            description = "Long-range neon butterfly sniper rifle that pierces Level 3 helmets for instant 150+ damage.",
            attributeBuffs = "Damage +++ | Range ++ | Armor Pierce ++",
            iconEmoji = "🎯"
        ),

        // BUNDLES
        VaultItem(
            id = "bundle_red_criminal",
            name = "Red Criminal Bundle",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.MYTHIC,
            description = "The legendary crimson clown mask and jumpsuit from the original Incubation season. Ultra prestigious.",
            attributeBuffs = "Prestige Aura | Classic Hit Sound | Fear Effect",
            isEquipped = true,
            iconEmoji = "🤡"
        ),
        VaultItem(
            id = "bundle_hip_hop",
            name = "Hip Hop Bundle",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.LEGENDARY,
            description = "Elite Pass Season 2 legendary baggy track pants, purple retro baseball cap, and nostalgic street dancer jacket.",
            attributeBuffs = "Vintage 2018 Flex | Old Free Fire Style",
            iconEmoji = "🧢"
        ),
        VaultItem(
            id = "bundle_sakura",
            name = "Sakura Blossom Season 1",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.MYTHIC,
            description = "The holy grail of Free Fire - Season 1 Elite Pass Japanese demon Oni mask and blossom robes.",
            attributeBuffs = "Original Season 1 OG | Sakura Embers Trail",
            iconEmoji = "🌸"
        ),
        VaultItem(
            id = "bundle_cobra_rage",
            name = "Cobra Rage Bundle",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.MYTHIC,
            description = "Futuristic cybernetic viper exoskeleton with customizable fiery aura color and cyber bike sprint animation.",
            attributeBuffs = "Aura Color Morph | Cyber Emote Trigger",
            iconEmoji = "⚡"
        ),
        VaultItem(
            id = "bundle_bunny_warrior",
            name = "Bunny Warrior Bundle",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.LEGENDARY,
            description = "Classic Easter event menace rabbit mask with tactical vest and high-top sneakers.",
            attributeBuffs = "Jump Shot Precision | Retro Sound Effect",
            iconEmoji = "🐰"
        ),
        VaultItem(
            id = "bundle_arctic_blue",
            name = "Arctic Blue Bundle",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.LEGENDARY,
            description = "Frost elemental samurai suit with glowing cryogenic flames flowing from shoulders and eyes.",
            attributeBuffs = "Frost Trail | Ice Resistance",
            iconEmoji = "❄️"
        ),
        VaultItem(
            id = "bundle_green_criminal",
            name = "Green Criminal Bundle",
            category = ItemCategory.BUNDLES,
            rarity = ItemRarity.MYTHIC,
            description = "The rarest original Lucky Draw criminal mask in toxic emerald green.",
            attributeBuffs = "Toxic Aura | Vintage Hit Indicator",
            iconEmoji = "🎭"
        ),

        // GLOO WALLS
        VaultItem(
            id = "gloo_cobra_rage",
            name = "Gloo Wall - Cobra Rage",
            category = ItemCategory.GLOO_WALLS,
            rarity = ItemRarity.MYTHIC,
            description = "Impenetrable tactical ice shield emblazoned with a blazing crimson Cobra demon face.",
            attributeBuffs = "HP: 350 | Fast Instant Deploy | Dragon Shield",
            isEquipped = true,
            iconEmoji = "🛡️"
        ),
        VaultItem(
            id = "gloo_bunker",
            name = "Gloo Wall - Bunker Shield",
            category = ItemCategory.GLOO_WALLS,
            rarity = ItemRarity.LEGENDARY,
            description = "Military grade fortified titanium bunker with observation peep slits.",
            attributeBuffs = "HP: 320 | Blast Deflection",
            iconEmoji = "🏰"
        ),
        VaultItem(
            id = "gloo_spikey",
            name = "Gloo Wall - Spikey Spine",
            category = ItemCategory.GLOO_WALLS,
            rarity = ItemRarity.LEGENDARY,
            description = "Menacing spiked bio-organic wall with glowing violet thorns.",
            attributeBuffs = "HP: 300 | Spiked Counter Armor",
            iconEmoji = "🦔"
        ),
        VaultItem(
            id = "gloo_justice",
            name = "Gloo Wall - Justice Fighter",
            category = ItemCategory.GLOO_WALLS,
            rarity = ItemRarity.MYTHIC,
            description = "Golden eagle wings shield designed in collaboration with martial arts cinema.",
            attributeBuffs = "HP: 340 | Golden Shockwave",
            iconEmoji = "🦅"
        ),

        // EMOTES
        VaultItem(
            id = "emote_cobra_bike",
            name = "Cobra Rage Bike",
            category = ItemCategory.EMOTES,
            rarity = ItemRarity.MYTHIC,
            description = "Summons a glowing holographic superbike, pops a high-speed wheelie, and leaves tire burn marks.",
            attributeBuffs = "Interactive 3D Motion | Crowd Cheer",
            isEquipped = true,
            iconEmoji = "🏍️"
        ),
        VaultItem(
            id = "emote_pirate_flag",
            name = "Pirate Flag Plant",
            category = ItemCategory.EMOTES,
            rarity = ItemRarity.MYTHIC,
            description = "Plants a massive pirate skull banner into the ground to claim victory over fallen opponents.",
            attributeBuffs = "Victory Banner | Ground Stomp Shake",
            iconEmoji = "🏴‍☠️"
        ),
        VaultItem(
            id = "emote_throne",
            name = "FFWC Throne",
            category = ItemCategory.EMOTES,
            rarity = ItemRarity.MYTHIC,
            description = "Summons the legendary golden World Cup Emperor throne and sits back with regal supremacy.",
            attributeBuffs = "Golden Glow | Emperor Pose",
            iconEmoji = "👑"
        ),
        VaultItem(
            id = "emote_flowers_love",
            name = "Flowers of Love",
            category = ItemCategory.EMOTES,
            rarity = ItemRarity.LEGENDARY,
            description = "Drops down on one knee and offers a glowing crimson red rose with sparkling romance petals.",
            attributeBuffs = "Rose Petal Cascade",
            iconEmoji = "🌹"
        ),
        VaultItem(
            id = "emote_tea_time",
            name = "Tea Time",
            category = ItemCategory.EMOTES,
            rarity = ItemRarity.LEGENDARY,
            description = "Materializes a classy mahogany table and golden tea cup for a gentlemanly sip amid the battlefield.",
            attributeBuffs = "Classy Clink Sound",
            iconEmoji = "☕"
        ),
        VaultItem(
            id = "emote_lol",
            name = "LOL Laugh",
            category = ItemCategory.EMOTES,
            rarity = ItemRarity.RARE,
            description = "Classic pointing and laughing emote that taunts enemies knocked down in the safe zone.",
            attributeBuffs = "Iconic Nostalgic Taunt",
            iconEmoji = "😂"
        ),

        // MELEE
        VaultItem(
            id = "melee_katana_blood",
            name = "Katana - Blood Moon",
            category = ItemCategory.MELEE,
            rarity = ItemRarity.MYTHIC,
            description = "Tempered crimson blade leaving glowing cherry blossom and flame slash trails.",
            attributeBuffs = "Damage: 95 | Quick Draw Sprint +8%",
            isEquipped = true,
            iconEmoji = "🗡️"
        ),
        VaultItem(
            id = "melee_pan_watermelon",
            name = "Watermelon Frying Pan",
            category = ItemCategory.MELEE,
            rarity = ItemRarity.LEGENDARY,
            description = "Deflects incoming bullets with a satisfying metal ricochet ping and deflects rear shots.",
            attributeBuffs = "Rear Bullet Deflection 100%",
            iconEmoji = "🍳"
        )
    )

    fun createStandardWeapons(): List<Weapon> {
        return listOf(
            Weapon(
                id = "ak47_draco",
                name = "AK-47",
                skinName = "Blue Flame Draco (Evo Lv.7)",
                category = "AR",
                damage = 38,
                headshotMultiplier = 2.4f,
                fireRateMs = 110L,
                magSize = 30,
                currentAmmo = 30,
                maxReserveAmmo = 180,
                reloadTimeMs = 1400L,
                range = 550f,
                spreadAngle = 3.5f,
                bulletColor = Color(0xFF00E5FF),
                isEvo = true,
                soundFrequency = 480
            ),
            Weapon(
                id = "mp40_cobra",
                name = "MP40",
                skinName = "Predatory Cobra (Evo Lv.7)",
                category = "SMG",
                damage = 27,
                headshotMultiplier = 2.2f,
                fireRateMs = 70L,
                magSize = 32,
                currentAmmo = 32,
                maxReserveAmmo = 240,
                reloadTimeMs = 1200L,
                range = 380f,
                spreadAngle = 4.2f,
                bulletColor = Color(0xFFFF2A6D),
                isEvo = true,
                soundFrequency = 620
            ),
            Weapon(
                id = "m1887_opm",
                name = "M1887",
                skinName = "One Punch Man Edition",
                category = "SG",
                damage = 110,
                headshotMultiplier = 2.1f,
                fireRateMs = 380L,
                magSize = 2,
                currentAmmo = 2,
                maxReserveAmmo = 40,
                reloadTimeMs = 1500L,
                range = 260f,
                spreadAngle = 7.5f,
                bulletColor = Color(0xFFFFB703),
                isEvo = false,
                soundFrequency = 260
            ),
            Weapon(
                id = "awm_duke",
                name = "AWM",
                skinName = "Duke Swallowtail",
                category = "SR",
                damage = 150,
                headshotMultiplier = 2.5f,
                fireRateMs = 1200L,
                magSize = 5,
                currentAmmo = 5,
                maxReserveAmmo = 30,
                reloadTimeMs = 2100L,
                range = 800f,
                spreadAngle = 0.5f,
                bulletColor = Color(0xFF7209B7),
                isEvo = false,
                soundFrequency = 180
            )
        )
    }
}
