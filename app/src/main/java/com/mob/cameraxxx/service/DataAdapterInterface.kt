package com.mob.cameraxxx.service

import com.mob.cameraxxx.data.App
import com.mob.cameraxxx.data.Section

public interface DataAdapterInterface {
    fun initData(filename: String): Boolean
    fun getSections(): ArrayList<Section>
    fun getSection(sectionId: String): Section?
    fun saveSection(section: Section): Boolean
    fun updateSection(sectionId: String, section: Section): Boolean
    fun deleteSection(sectionId: String): Boolean
    fun nextLevel(currentSectionId: String):Section?
    fun getAppConfig(): App
}