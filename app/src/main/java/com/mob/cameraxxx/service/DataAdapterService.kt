package com.mob.cameraxxx.service

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.AppModel
import com.mob.cameraxxx.data.Section
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class DataAdapterService : DataAdapterInterface {
    var _context: Context
    val sharedPreferences: SharedPreferences
    var gson: Gson

    constructor(ctx: Context) {
        _context = ctx
        gson = Gson()
        sharedPreferences = _context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun initData(filename: String): Boolean {
        try {

            val inputStream = _context.assets.open(filename)
            var json = inputStream.bufferedReader().use { it.readText() }
            var model = gson.fromJson(json, AppModel::class.java)

            var appLaunchStatus = sharedPreferences.getBoolean(Constants.IS_FIRST_LAUNCH_KEY, false)
            if (!appLaunchStatus) {
                sharedPreferences.edit().putString(Constants.SECTION_KEY, gson.toJson(model.sections)).apply()
                sharedPreferences.edit().putString(Constants.USER_KEY, gson.toJson(model.users)).apply()
                sharedPreferences.edit().putBoolean(Constants.IS_FIRST_LAUNCH_KEY, true).apply()
            }
            return true
        } catch (ex: Exception) {
            Toast.makeText(_context, ex.localizedMessage, Toast.LENGTH_LONG).show()
            return false
        }
    }

    override fun getSections(): ArrayList<Section> {
        try {
            var sectionsJson = sharedPreferences.all.get(Constants.SECTION_KEY).toString()
            var sections = gson.fromJson(sectionsJson, Array<Section>::class.java)
            if (sections.size > 0)
                return sections.toCollection(ArrayList<Section>())
            return arrayListOf<Section>()
        } catch (ex: Exception) {
            return arrayListOf<Section>()
        }
    }

    override fun getSection(sectionId: Long): Section? {
        return getSections().find { r -> r.id == sectionId }
    }

    override fun saveSection(section: Section): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteSection(sectionId: Long): Boolean {
        var sections = getSections()
        var sectionIndex = sections.map { r -> r.id }.indexOf(sectionId)
        if (sectionIndex >= 0) {
            sections.removeAt(sectionIndex)
            var _sectionStrings = gson.toJson(sections).toString()
            sharedPreferences.edit().remove(Constants.SECTION_KEY).apply()
            sharedPreferences.edit().putString(Constants.SECTION_KEY, _sectionStrings).apply()
            return true
        } else {
            return false
        }
    }


}