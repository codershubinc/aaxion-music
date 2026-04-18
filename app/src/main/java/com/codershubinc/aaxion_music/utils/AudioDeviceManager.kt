package com.codershubinc.aaxion_music.utils

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class AudioDeviceManager(context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun getCurrentOutputDeviceName(): String {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            if (device.isSink) {
                return when (device.type) {
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Phone Speaker"
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "Bluetooth Device"
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Wired Headphones"
                    AudioDeviceInfo.TYPE_USB_DEVICE, AudioDeviceInfo.TYPE_USB_HEADSET -> "USB Audio"
                    else -> "External Device"
                }
            }
        }
        return "Internal Speaker"
    }
}

@Composable
fun rememberCurrentAudioDevice(): String {
    val context = LocalContext.current
    val manager = remember { AudioDeviceManager(context) }
    var deviceName by remember { mutableStateOf(manager.getCurrentOutputDeviceName()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            deviceName = manager.getCurrentOutputDeviceName()
            kotlinx.coroutines.delay(2000)
        }
    }

    return deviceName
}
