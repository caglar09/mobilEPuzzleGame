package com.mob.cameraxxx.service

import com.mob.cameraxxx.data.App
import com.mob.cameraxxx.data.Section

public interface DataAdapterInterface {
    fun initData(filename: String): Boolean
    fun getSections(): ArrayList<Section>
    fun getSection(sectionId: Long): Section?
    fun saveSection(section: Section): Boolean
    fun updateSection(sectionId: Long, section: Section): Boolean
    fun deleteSection(sectionId: Long): Boolean
    fun nextLevel(currentSectionId: Long):Section?
    fun getAppConfig(): App
}