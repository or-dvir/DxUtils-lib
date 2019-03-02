package or_dvir.hotmail.com.dxutils

/**
 * a helper class to be used with the function [retroRequestAsync]
 * @param T the type of object this class will handle.
 */
class RetroCallback<T>
{
    var onSuccess: retroSuccess<T>? = null

    /**
     * explicitly handle server error or null server response body
     */
    var onErrorCodeOrNullBody: retroErrorCode<T>? = null

    /**
     * explicitly handle network exception
     */
    var onException: retroException<T>? = null

    /**
     * convenience if handling of null body/error/exception is the same
     */
    var onErrorOrExceptionOrNullBody: retroErrorOrException<T>? = null
}