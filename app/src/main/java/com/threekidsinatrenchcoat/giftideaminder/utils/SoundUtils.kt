package com.threekidsinatrenchcoat.giftideaminder.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager

object SoundUtils {
    
    fun playDingSound(context: Context) {
        try {
            // Create a simple ding sound using ToneGenerator
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
            
            // Clean up after a short delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                toneGenerator.release()
            }, 500)
        } catch (e: Exception) {
            // Fail silently if sound can't be played
        }
    }
    
    fun playSuccessSound(context: Context) {
        try {
            // Create a pleasant completion sound
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)
            
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                toneGenerator.release()
            }, 500)
        } catch (e: Exception) {
            // Fail silently
        }
    }
}