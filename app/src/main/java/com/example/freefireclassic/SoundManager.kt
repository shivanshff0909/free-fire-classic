package com.example.freefireclassic

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playGunshot(isEvo: Boolean = false, isSniper: Boolean = false) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                if (isSniper) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 90)
                } else if (isEvo) {
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 45)
                } else {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
                }
            } catch (_: Exception) {}
        }
    }

    fun playHeadshot() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 110)
            } catch (_: Exception) {}
        }
    }

    fun playGlooWallDeploy() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_CONFIRM, 95)
            } catch (_: Exception) {}
        }
    }

    fun playMedkitHeal() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 130)
            } catch (_: Exception) {}
        }
    }

    fun playSkillActive() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 160)
            } catch (_: Exception) {}
        }
    }

    fun playBooyahVictory() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
            } catch (_: Exception) {}
        }
    }

    fun triggerHaptic(durationMs: Long = 35) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
