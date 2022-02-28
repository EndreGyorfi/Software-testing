package edu.codespring.ro.apiaapp.spref

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

actual typealias SPref = Application

actual fun SPref.getInt(key: String) : Int {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    return prefs.getInt(key, -1)
}


actual fun SPref.setInt(key: String, value: Int) {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putInt(key, value)
    editor.apply()
}

actual fun SPref.getFloat(key: String) : Float {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    return prefs.getFloat(key, -1F)
}


actual fun SPref.setFloat(key: String, value: Float) {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putFloat(key, value)
    editor.apply()
}

actual fun SPref.getLong(key: String) : Long {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    return prefs.getLong(key, -1)
}


actual fun SPref.setLong(key: String, value: Long) {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putLong(key, value)
    editor.apply()
}

actual fun SPref.getBoolean(key: String) : Boolean {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    return prefs.getBoolean(key, false)
}


actual fun SPref.setBoolean(key: String, value: Boolean) {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putBoolean(key, value)
    editor.apply()
}

actual fun SPref.getString(key: String) : String? {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    return prefs.getString(key, null)
}


actual fun SPref.setString(key: String, value: String) {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putString(key, value)
    editor.apply()
}

actual fun SPref.getStringSet(key: String) : Set<String>? {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    return prefs.getStringSet(key, null)
}


actual fun SPref.setStringSet(key: String, value: Set<String>) {
    val prefs: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putStringSet(key, value)
    editor.apply()
}
