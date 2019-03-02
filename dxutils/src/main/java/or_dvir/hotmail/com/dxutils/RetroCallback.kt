package or_dvir.hotmail.com.dxutils

/**
 * a helper class to be used with the function [retroRequestAsync] which provides some handy listeners.
 *
 * note that, when applicable, multiple listeners may be invoked.
 *
 * e.g. if you initialize [onTimeout] AND [onException] they will BOTH be invoked (timeout throws an exception)
 * @param T the type of object this class will handle.
 */
class RetroCallback<T>
{
    var onSuccess: retroSuccess<T>? = null

    /**
     * invoked when the request has timed-out
     */
    var onTimeout: retroTimeout<T>? = null

    /**
     * invoked on server error or when server response body was null
     */
    var onErrorCodeOrNullBody: retroErrorCode<T>? = null

    /**
     * invoked on network exception
     */
    var onException: retroException<T>? = null

    /**
     * convenience listener to be invoked on any kind of failure
     * (null response body/server error/network exception/request timeout).
     * this is useful if handling is the same for all failures.
     */
    var onAnyFailure: retroErrorOrException<T>? = null
}