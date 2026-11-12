package com.example.remote

enum class RemoteAction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    OK,
    BACK,
    MENU,
    INFO,
    RED,
    GREEN,
    YELLOW,
    BLUE,
    CH_UP,
    CH_DOWN,
    PLAY,
    PAUSE,
    STOP,
    NEXT,
    PREV,
    FF,
    RW,
    ZOOM,
    ZOOM_IN,
    ZOOM_OUT,
    D0, D1, D2, D3, D4, D5, D6, D7, D8, D9;

    val isDigit: Boolean get() = this in D0..D9
    val digitValue: Int get() = when (this) {
        D0 -> 0; D1 -> 1; D2 -> 2; D3 -> 3; D4 -> 4;
        D5 -> 5; D6 -> 6; D7 -> 7; D8 -> 8; D9 -> 9;
        else -> -1
    }
}
