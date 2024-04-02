package com.example.baseactivity

import android.content.Context
import android.content.SharedPreferences

class MyShared(var context: Context) {


    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)


    companion object {

        const val PERSONAL = "personal"
        const val CONTACT = "contact"
        const val EDUCATION = "education"
        const val SKILL = "skill"
        const val EXPERIENCE = "experience"
        const val PROJECTS = "projects"
        const val ACHIEVEMENT = "achievement"
        const val LANGUAGE = "language"

        const val FIRST_RUN = "first run"

        const val CHANGE_LANGUAGE = "change_language"

        const val DATA = "data"

    }

    /**
     * Function to save auth token refresh
     */

    //Data
    fun saveData(b :Boolean)
    {
        val editor = prefs.edit()
        editor.putBoolean(DATA, b)
        editor.apply()
    }
    fun fetchData(): Boolean? {
        return prefs.getBoolean(DATA, false)
    }



    fun saveRun(b :Boolean)
    {
        val editor = prefs.edit()
        editor.putBoolean(FIRST_RUN, b)
        editor.apply()
    }
    fun fetchRun(): Boolean? {
        return prefs.getBoolean(FIRST_RUN, false)
    }


    //personal
    fun savePersonal(p: String) {
        val editor = prefs.edit()
        editor.putString(PERSONAL, p)
        editor.apply()
    }
    fun fetchPersonal(): String? {
        return prefs.getString(PERSONAL, "")
    }


    //contact
    fun saveContact(p: String) {
        val editor = prefs.edit()
        editor.putString(CONTACT, p)
        editor.apply()
    }
    fun fetchContact(): String? {
        return prefs.getString(CONTACT, "")
    }


    //education
    fun saveEducation(p: String) {
        val editor = prefs.edit()
        editor.putString(EDUCATION, p)
        editor.apply()
    }
    fun fetchEducation(): String? {
        return prefs.getString(EDUCATION, "")
    }


    //skill
    fun saveSkill(p: String) {
        val editor = prefs.edit()
        editor.putString(SKILL, p)
        editor.apply()
    }
    fun fetchSkill(): String? {
        return prefs.getString(SKILL, "")
    }



    //experience
    fun saveExperience(p: String) {
        val editor = prefs.edit()
        editor.putString(EXPERIENCE, p)
        editor.apply()
    }
    fun fetchExperience(): String? {
        return prefs.getString(EXPERIENCE, "")
    }



    //projects
    fun saveProjects(p: String) {
        val editor = prefs.edit()
        editor.putString(PROJECTS, p)
        editor.apply()
    }
    fun fetchProjects(): String? {
        return prefs.getString(PROJECTS, "")
    }


    //achievement
    fun saveAchievement(p: String) {
        val editor = prefs.edit()
        editor.putString(ACHIEVEMENT, p)
        editor.apply()
    }
    fun fetchAchievement(): String? {
        return prefs.getString(ACHIEVEMENT, "")
    }



    //language
    fun saveLanguage(p: String) {
        val editor = prefs.edit()
        editor.putString(LANGUAGE, p)
        editor.apply()
    }
    fun fetchLanguage(): String? {
        return prefs.getString(LANGUAGE, "")
    }


}