package com.instantonlinematka.instantonlinematka.model

import android.graphics.drawable.Drawable

data class GameModes(
    val gameId: String? = null,
    val gameImage: Drawable? = null,
    val gameName: String? = null,
    val gameColor: Int? = null,
    val expired: Boolean? = null,
    val gameCatId: String? = null,
    val gameTypeId: String? = null,
    val Status: String? = null,
    val GameDate: String? = null,
    val GameTime: String? = null,
    val GameNumbers: String? = null,
)