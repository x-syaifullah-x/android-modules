package id.xxx.module.autentication

sealed interface IAuthenticationResult {

    data object Success : IAuthenticationResult

    class Error(val err: Throwable) : IAuthenticationResult
}