package edu.codespring.ro.apiaapp.spref

object KMMStorage {
    private lateinit var sPref: SPref

    fun setSPref(sPref: SPref) {
        KMMStorage.sPref = sPref
    }

    fun getInt(key: String): Int {
        return sPref.getInt(key)
    }

    fun putInt(key: String, value: Int) {
        sPref.setInt(key, value)
    }

    fun getFloat(key: String): Float {
        return sPref.getFloat(key)
    }

    fun putFloat(key: String, value: Float) {
        sPref.setFloat(key, value)
    }

    fun getLong(key: String): Long {
        return sPref.getLong(key)
    }

    fun putLong(key: String, value: Long) {
        sPref.setLong(key, value)
    }

    fun getBoolean(key: String): Boolean {
        return sPref.getBoolean(key)
    }

    fun putBoolean(key: String, value: Boolean) {
        sPref.setBoolean(key, value)
    }

    fun getString(key: String): String? {
        return sPref.getString(key)
    }

    fun putString(key: String, value: String) {
        sPref.setString(key, value)
    }

    fun getStringSet(key: String): Set<String>? {
        return sPref.getStringSet(key)
    }

    fun putStringSet(key: String, value: Set<String>) {
        sPref.setStringSet(key, value)
    }
}
