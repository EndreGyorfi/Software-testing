package edu.codespring.ro.apiaapp.spref

expect class SPref

expect fun SPref.getInt(key: String) : Int
expect fun SPref.setInt(key: String, value: Int)

expect fun SPref.getFloat(key: String) : Float
expect fun SPref.setFloat(key: String, value: Float)

expect fun SPref.getLong(key: String) : Long
expect fun SPref.setLong(key: String, value: Long)

expect fun SPref.getBoolean(key: String) : Boolean
expect fun SPref.setBoolean(key: String, value: Boolean)

expect fun SPref.getString(key: String) : String?
expect fun SPref.setString(key: String, value: String)

expect fun SPref.getStringSet(key: String) : Set<String>?
expect fun SPref.setStringSet(key: String, value: Set<String>)
