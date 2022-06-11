package com.trootechdemo.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trootechdemo.model.SubCategoryResponseData
import java.lang.reflect.Type


/**
 * Local data stored in Shared Preferences...
 **/
object SessionManager {

    private val PREFS_NAME = "App Preference"

    private val PARAM_SUB_CATEGORY_ALL= "subCategory_all"

    //.........................
    fun clearAppSession(context: Context) {
        try {
            val preferences =
                context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    var alSubCategoryPlayerStatus: ArrayList<SubCategoryResponseData> = ArrayList()
    private var typeTokenSubCategoryDetails: Type = object : TypeToken<List<SubCategoryResponseData>>() {}.type

    fun setSubCategoryStatsData(context: Context, listwriter: List<SubCategoryResponseData>) {
        val preferences = context.getSharedPreferences(PARAM_SUB_CATEGORY_ALL, Context.MODE_PRIVATE)
        this.alSubCategoryPlayerStatus = ArrayList(listwriter)
        val editor = preferences.edit()
        editor.putString(PARAM_SUB_CATEGORY_ALL, Gson().toJson(listwriter))
        editor.commit()
    }

    fun getSubCategoryStatsData(context: Context): List<SubCategoryResponseData> {
        val preferences = context.getSharedPreferences(PARAM_SUB_CATEGORY_ALL, Context.MODE_PRIVATE)
        if (alSubCategoryPlayerStatus == null) {
            alSubCategoryPlayerStatus = Gson().fromJson(
                preferences.getString(PARAM_SUB_CATEGORY_ALL, null), typeTokenSubCategoryDetails
            )
        }
        return alSubCategoryPlayerStatus
    }

}
