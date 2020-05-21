package com.mob.cameraxxx.service

import com.mob.cameraxxx.data.Section

public interface DataAdapterInterface {
    fun initData(filename: String): Boolean
    fun getSections(): ArrayList<Section>
    fun getSection(sectionId: Long): Section?
    fun saveSection(section: Section): Boolean
    fun deleteSection(sectionId: Long): Boolean


}