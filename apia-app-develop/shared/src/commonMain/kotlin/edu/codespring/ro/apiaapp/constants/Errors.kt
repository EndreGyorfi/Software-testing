package edu.codespring.ro.apiaapp.constants

interface Errors

enum class PlannerErrors: Errors {
    ADD_PACKAGE_ERROR
}
enum class ApiErrors: Errors {
    CLIENT_ERROR,
    SERVER_ERROR,
    REDIRECT_ERROR,
    CONNECT_ERROR,
}

enum class AuthErrors: Errors {
    AUTH_ERROR,
}
