package com.example.remote

import android.view.KeyEvent

object KeyMapper {

    fun mapKeyEvent(event: KeyEvent, isTyping: Boolean = false): RemoteAction? {
        val keyCode = event.keyCode

        // Digits
        when (keyCode) {
            KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> return RemoteAction.D0
            KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> return RemoteAction.D1
            KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> return RemoteAction.D2
            KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> return RemoteAction.D3
            KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> return RemoteAction.D4
            KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> return RemoteAction.D5
            KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> return RemoteAction.D6
            KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> return RemoteAction.D7
            KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> return RemoteAction.D8
            KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> return RemoteAction.D9
        }

        // D-pad
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> return RemoteAction.LEFT
            KeyEvent.KEYCODE_DPAD_RIGHT -> return RemoteAction.RIGHT
            KeyEvent.KEYCODE_DPAD_UP -> return RemoteAction.UP
            KeyEvent.KEYCODE_DPAD_DOWN -> return RemoteAction.DOWN
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_BUTTON_A -> return RemoteAction.OK
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_ESCAPE,
            KeyEvent.KEYCODE_BUTTON_B -> return RemoteAction.BACK
            KeyEvent.KEYCODE_DEL -> {
                if (!isTyping) return RemoteAction.BACK
            }
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_BUTTON_START -> return RemoteAction.MENU
            165, // KEYCODE_INFO
            KeyEvent.KEYCODE_INFO -> return RemoteAction.INFO
            183, // KEYCODE_PROG_RED
            KeyEvent.KEYCODE_PROG_RED -> return RemoteAction.RED
            184, // KEYCODE_PROG_GREEN
            KeyEvent.KEYCODE_PROG_GREEN -> return RemoteAction.GREEN
            185, // KEYCODE_PROG_YELLOW
            KeyEvent.KEYCODE_PROG_YELLOW -> return RemoteAction.YELLOW
            186, // KEYCODE_PROG_BLUE
            KeyEvent.KEYCODE_PROG_BLUE -> return RemoteAction.BLUE
            166, // KEYCODE_CHANNEL_UP
            KeyEvent.KEYCODE_CHANNEL_UP,
            KeyEvent.KEYCODE_PAGE_UP -> return RemoteAction.CH_UP
            167, // KEYCODE_CHANNEL_DOWN
            KeyEvent.KEYCODE_CHANNEL_DOWN,
            KeyEvent.KEYCODE_PAGE_DOWN -> return RemoteAction.CH_DOWN
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY -> return RemoteAction.PLAY
            KeyEvent.KEYCODE_MEDIA_PAUSE -> return RemoteAction.PAUSE
            KeyEvent.KEYCODE_MEDIA_STOP -> return RemoteAction.STOP
            KeyEvent.KEYCODE_MEDIA_NEXT -> return RemoteAction.NEXT
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> return RemoteAction.PREV
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> return RemoteAction.FF
            KeyEvent.KEYCODE_MEDIA_REWIND -> return RemoteAction.RW
            255, // KEYCODE_TV_ZOOM_MODE
            168, // KEYCODE_ZOOM_IN
            KeyEvent.KEYCODE_ZOOM_IN -> return RemoteAction.ZOOM_IN
            169, // KEYCODE_ZOOM_OUT
            KeyEvent.KEYCODE_ZOOM_OUT -> return RemoteAction.ZOOM_OUT
        }

        // Keyboard shortcuts (when not typing)
        if (!isTyping) {
            when (keyCode) {
                KeyEvent.KEYCODE_PLUS, KeyEvent.KEYCODE_EQUALS -> return RemoteAction.ZOOM_IN
                KeyEvent.KEYCODE_MINUS -> return RemoteAction.ZOOM_OUT
                KeyEvent.KEYCODE_R -> return RemoteAction.YELLOW // rotate
                KeyEvent.KEYCODE_SPACE -> return RemoteAction.PLAY // slideshow
                KeyEvent.KEYCODE_F -> return RemoteAction.ZOOM // fullscreen / fit
                KeyEvent.KEYCODE_I -> return RemoteAction.INFO // details
                KeyEvent.KEYCODE_H -> return RemoteAction.RED // favorite
                KeyEvent.KEYCODE_SLASH -> return RemoteAction.BLUE // search
            }
        }

        return null
    }

    fun isRepeatAllowed(action: RemoteAction): Boolean {
        return when (action) {
            RemoteAction.LEFT,
            RemoteAction.RIGHT,
            RemoteAction.UP,
            RemoteAction.DOWN,
            RemoteAction.CH_UP,
            RemoteAction.CH_DOWN,
            RemoteAction.D2,
            RemoteAction.D8 -> true
            else -> false
        }
    }
}
