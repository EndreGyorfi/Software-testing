package edu.codespring.ro.apiaapp

fun ByteArray.isJsonResultEmpty(): Boolean {
    // 2 bytes means that user profile does not exist
    // [] : 2 bytes
    return this.size == 2
}
