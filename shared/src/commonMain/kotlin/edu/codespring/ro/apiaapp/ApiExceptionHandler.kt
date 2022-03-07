package edu.codespring.ro.apiaapp

import edu.codespring.ro.apiaapp.constants.ApiErrors
import io.ktor.client.features.RedirectResponseException
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.network.sockets.ConnectTimeoutException

inline fun <R> runCatching(block: () -> R, success: (R) -> Unit, error: (ApiErrors) -> Unit) {
    return try {
        success(block())
    } catch (ignore: ConnectTimeoutException) {
        error(ApiErrors.CONNECT_ERROR)
    } catch (ignore: RedirectResponseException) {
        error(ApiErrors.REDIRECT_ERROR)
    } catch (ignore: ClientRequestException) {
        error(ApiErrors.CLIENT_ERROR)
    } catch (ignore: ServerResponseException) {
        error(ApiErrors.SERVER_ERROR)
    }
}
