package com.mob.cameraxxx.data

data class Section(val id: Long, val order: Int, val image: String, var textTr: String, var textEn: String,
                   var isKnowedTr: Boolean, var isKnowedEn: Boolean, var isCompleted: Boolean, var isPuzzleCompleted: Boolean)