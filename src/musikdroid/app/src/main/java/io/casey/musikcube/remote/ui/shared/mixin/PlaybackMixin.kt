package io.casey.musikcube.remote.ui.shared.mixin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import io.casey.musikcube.remote.framework.MixinBase
import io.casey.musikcube.remote.service.playback.IPlaybackService
import io.casey.musikcube.remote.service.playback.PlaybackServiceFactory
import io.casey.musikcube.remote.ui.settings.constants.Prefs

class PlaybackMixin(var listener: (() -> Unit)? = null): MixinBase() {
    private lateinit var prefs: SharedPreferences

    var service: IPlaybackService = PlaybackServiceFactory.instance(context)
        private set

    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        prefs = context.getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE)
        connect()
    }

    override fun onPause() {
        super.onPause()
        disconnect()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    fun reload() {
        if (active) {
            disconnect()
            connect()
        }
    }

    fun onKeyDown(keyCode: Int): Boolean {
        val streaming = prefs.getBoolean(
            Prefs.Key.STREAMING_PLAYBACK, Prefs.Default.STREAMING_PLAYBACK)

        if (!streaming) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                service.volumeDown()
                return true
            }
            else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                service.volumeUp()
                return true
            }
        }
        return false
    }

    private fun connect() {
        service = PlaybackServiceFactory.instance(context)
        val listener = this.listener
        if (listener != null) {
            service.connect(listener)
        }
    }

    private fun disconnect() {
        val listener = this.listener
        if (listener != null) {
            service.disconnect(listener)
        }
    }
}