package com.mob.cameraxxx.service

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.App
import com.mob.cameraxxx.data.AppModel
import com.mob.cameraxxx.data.Section
import java.lang.Exception
import kotlin.collections.ArrayList

class DataAdapterService : DataAdapterInterface {
    var _context: Context
    val sharedPreferences: SharedPreferences
    var gson: Gson
    val database: FirebaseDatabase
    val sectionRef: DatabaseReference
    val appRef: DatabaseReference

    constructor(ctx: Context) {
        _context = ctx
        gson = Gson()
        sharedPreferences = _context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        database = Firebase.database
        sectionRef = database.getReference("sections")
        appRef = database.getReference("app")
    }

    override fun initData(filename: String): Boolean {
        try {

            val inputStream = _context.assets.open(filename)
            var json = inputStream.bufferedReader().use { it.readText() }
            var model = gson.fromJson(json, AppModel::class.java)

            var appLaunchStatus = sharedPreferences.getBoolean(Constants.IS_FIRST_LAUNCH_KEY, false) // ilk açılmada false diğer açılmalarda true dönüyor
            if (!appLaunchStatus) {
                sharedPreferences.edit().putString(Constants.SECTION_KEY, gson.toJson(model.sections)).apply()
                sharedPreferences.edit().putString(Constants.USER_KEY, gson.toJson(model.users)).apply()
                sharedPreferences.edit().putString(Constants.APP_CONFIG_KEY, gson.toJson(model.app)).apply()
                sharedPreferences.edit().putBoolean(Constants.IS_FIRST_LAUNCH_KEY, true).apply()
              /*  model.sections.forEach { section ->
                    sectionRef.push().setValue(section)
                }

                appRef.setValue(model.app)*/
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

    override fun getSection(sectionId: String): Section? {
        return getSections().find { r -> r.id == sectionId }
    }

    override fun saveSection(section: Section): Boolean {
        try {
            var sections = getSections()
            sections.add(section)
            var _allSectionJsonString = gson.toJson(sections).toString()
            //sharedPreferences.edit().remove(Constants.SECTION_KEY).apply()
            sharedPreferences.edit().putString(Constants.SECTION_KEY, _allSectionJsonString).commit()
            return true
        } catch (ex: Exception) {
            return false
        }

    }

    override fun updateSection(sectionId: String, section: Section): Boolean {
        var allSections = getSections()
        var a = allSections.withIndex().find { r -> r.value.id == sectionId }!!.index
        var _section = allSections[a]
        if (_section == null)
            return false
        if (_section!!.completed != section.completed)
            _section!!.completed = section.completed
        if (_section.knowedEn != section.knowedEn)
            _section.knowedEn = section.knowedEn
        if (_section.knowedTr != section.knowedTr)
            _section.knowedTr = section.knowedTr
        if (_section.puzzleCompleted != section.puzzleCompleted)
            _section.puzzleCompleted = section.puzzleCompleted

        allSections[a] = _section
        var _allSectionJsonString = gson.toJson(allSections).toString()
        sharedPreferences.edit().putString(Constants.SECTION_KEY, _allSectionJsonString).commit()
        return true
    }

    override fun deleteSection(sectionId: String): Boolean {
        var sections = getSections()
        var sectionIndex = sections.map { r -> r.id }.indexOf(sectionId)
        if (sectionIndex >= 0) {
            sections.removeAt(sectionIndex)
            var _sectionStrings = gson.toJson(sections).toString()
            //sharedPreferences.edit().remove(Constants.SECTION_KEY).apply()
            sharedPreferences.edit().putString(Constants.SECTION_KEY, _sectionStrings).commit()
            return true
        } else {
            return false
        }
    }

    override fun nextLevel(currentSectionId: String): Section? {
        var allsections = getSections()
        var currentIdex = allsections.withIndex().find { r -> r.value.id == currentSectionId }!!.index
        if (currentIdex == -1) {
            return null
        }
        if ((currentIdex + 1) >= allsections.size) {
            return null
        }
        var nextLevel = allsections.get(currentIdex + 1)
        return nextLevel
    }

    override fun getAppConfig(): App {
        try {
            var configString = sharedPreferences.all.get(Constants.APP_CONFIG_KEY).toString()
            var config = gson.fromJson(configString, App::class.java)
            return config
        } catch (ex: Exception) {
            return App("","")
        }
    }


}