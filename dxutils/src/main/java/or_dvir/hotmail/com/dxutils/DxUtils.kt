package or_dvir.hotmail.com.dxutils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import retrofit2.Call
import java.util.*
import kotlin.math.roundToInt

typealias retroSuccess<T> = ((originalCall: Call<T>, result: T, requestCode: Int) -> Unit)
typealias retroErrorCode<T> = ((originalCall: Call<T>, serverErrorCode: Int, requestCode: Int) -> Unit)
typealias retroException<T> = ((originalCall: Call<T>, exception: Exception, requestCode: Int) -> Unit)
typealias retroTimeout<T> = ((originalCall: Call<T>, timeoutMillis: Long, requestCode: Int) -> Unit)
typealias retroErrorOrException<T> = ((originalCall: Call<T>, serverErrorCode: Int?, exception: Exception?, requestCode: Int) -> Unit)
typealias simpleCallback = () -> Unit

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

fun Int.dpToPx() =  this.toFloat().dpToPx()
fun Int.pxToDp() = this / Resources.getSystem().displayMetrics.density
fun Float.dpToPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

fun View.makeVisible(){ visibility = View.VISIBLE }
fun View.makeInvisible(){ visibility = View.INVISIBLE }
fun View.makeGone(){ visibility = View.GONE }

/**
 * returns TRUE if at least one item in [objects] is not null, or FALSE if all [objects] are null
 */
fun atLeastOneNotNull(vararg objects: Any?) = objects.any { it != null }

/**
 * returns TRUE if at least one item in [objects] is null, or FALSE if all [objects] are not null
 */
fun atLeastOneNull(vararg objects: Any?) = objects.any { it == null }

/**
 * @param context Context
 * @return Int the width of the screen in pixels
 */
fun getScreenWidthPx(context: Context) = context.resources.displayMetrics.widthPixels
/**
 * @param context Context
 * @return Int the height of the screen in pixels
 */
fun getScreenHeightPx(context: Context) = context.resources.displayMetrics.heightPixels

/**
 * displays the default back button on this activity's support action bar (if not null)
 */
fun AppCompatActivity.setHomeUpEnabled(enabled: Boolean) = supportActionBar?.setDisplayHomeAsUpEnabled(enabled)

/**
 * @param requestedPermissions the permissions requested by the app
 * @param grantResults the users' results of allowing/denying [requestedPermissions]
 * @return a list of the denied permissions
 */
fun getUserDeniedPermissions(requestedPermissions: Array<String>,
                             grantResults: IntArray)
        : List<String>
{
    val denied = ArrayList<String>(requestedPermissions.size)

    grantResults.forEachIndexed { index, value ->
        if(value == PackageManager.PERMISSION_DENIED)
            denied.add(requestedPermissions[index])
    }

    return denied.apply { trimToSize() }
}

/**
 * This function is intended to be used if the user denied some permissions
 * and you want to explain to them why the app needs those permissions.
 *
 * @param message The message for the dialog to display.
 * @param posBtnOpenSettings If TRUE, the positive button will open the apps' settings page
 * where the user can manually control permissions (useful if the user clicked "never ask again"
 * on some permissions).
 * @param posBtnTxt The positive button text.
 * @param posBtnListener Optional listener to invoke when the positive button is clicked.
 * If [posBtnOpenSettings] is TRUE, this will NOT be invoked.
 * @param negBtnTxt Optional negative button text (default has no negative button).
 * @param negBtnListener Optional listener to invoke when the negative button is clicked.
 * @param isCancelable Whether or not this [AlertDialog] is cancelable (defaults to FALSE)
 */
fun AppCompatActivity.showPermissionsRationaleDialog(@StringRes message: Int,
                                                     posBtnOpenSettings: Boolean,
                                                     @StringRes posBtnTxt: Int,
                                                     posBtnListener: simpleCallback? = null,
                                                     @StringRes negBtnTxt: Int = -1,
                                                     negBtnListener: simpleCallback? = null,
                                                     isCancelable: Boolean = false)
{
    AlertDialog.Builder(this).apply {
        setCancelable(isCancelable)
        setMessage(message)

        var posListener = posBtnListener

        if (posBtnOpenSettings)
        {
            posListener = {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    startActivity(this)
                }
            }
        }

        setPositiveButton(posBtnTxt) { _, _ -> posListener?.invoke() }
        if(negBtnTxt != -1)
            setNegativeButton(negBtnTxt) { _, _ -> negBtnListener?.invoke() }
    }.show()
}

/**
 * Shows an [AlertDialog] with a single (positive) button which dismisses the dialog when clicked.
 * @param message the message for the dialog to display.
 * @param btnTxt the button text.
 * @param title optional title of the dialog (default has no title).
 * @param onClickListener optional listener to invoked when the (positive) button is clicked.
 * @param isCancelable whether or not this [AlertDialog] is cancelable (defaults to FALSE).
 */
fun AppCompatActivity.showSimpleDialog(@StringRes message: Int,
                                       @StringRes btnTxt: Int,
                                       @StringRes title: Int = -1,
                                       onClickListener: simpleCallback? = null,
                                       isCancelable: Boolean = false)
{
    AlertDialog.Builder(this).apply {
        if (title != -1)
            setTitle(title)

        setMessage(message)
        setCancelable(isCancelable)
        setPositiveButton(btnTxt) { _, _ -> onClickListener?.invoke() }
    }.show()
}

/**
 * Creates a [ProgressDialog].
 * @param message the message for the dialog to show.
 * @param isCancelable whether or not this [ProgressDialog] is cancelable (defaults to FALSE).
 * @param shouldShow whether or not this [ProgressDialog] should be displayed immediately (defaults to false).
 */
@Deprecated("The use of ProgressDialog has been deprecated by Google")
fun AppCompatActivity.createProgressDialog(@StringRes message: Int,
                                           isCancelable: Boolean = false,
                                           shouldShow: Boolean = false): ProgressDialog
{
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setMessage(getString(message))
        if(shouldShow)
            show()
    }
}

/**
 * a helper method using the Retrofit library to *asynchronously* perform a network request
 * using kotlin's co-routines, where **T** is the type of object to return.
 * @param logTag String a tag for the log in case of an error.
 * @param scope CoroutineScope the scope on which [callback] will be invoked.
 * note that the actual request is always performed using [Dispatchers.Default]
 * @param call the Retrofit [Call] object
 * @param callback [RetroCallback] a callback to be invoked when the request has finished.
 * @param timeoutMillis a timeout for the request in milliseconds. default value is 10,000  (10 seconds).
 * @param requestCode Int an optional request code to differentiate between different calls.
 * @return the [Job] created by the co-routine, in case you wish to perform some actions on it (e.g. [cancel][Job.cancel])
 */
fun <T> retroRequestAsync(logTag: String,
                          scope: CoroutineScope,
                          call: Call<T>,
                          callback: RetroCallback<T>,
                          timeoutMillis: Long = 10_000,
                          requestCode: Int = -1)
        : Job
{
    return scope.launch {

        try
        {
            val response = withTimeout(timeoutMillis) {
                withContext(Dispatchers.Default) { call.execute() }
            }

            val responseCode = response.code()
            val responseBody = response.body()

            //note:
            //this should be initialized to null and NOT an empty string
            //in case that the servers' response message is empty
            var errorMessage: String? = null

            //NOTE:
            //response.isSuccessful() returns true for all values between 200 and 300!!!
            //HTTP code 200 = OK
            when
            {
                responseCode != 200  -> { errorMessage = response.message() }
                responseBody == null -> { errorMessage = "server response body was NULL" }
                //code is 200, and body is not null = success
                else                 -> callback.onSuccess?.invoke(call, responseBody, requestCode)
            }

            if(errorMessage != null)
            {
                Log.e(logTag, "server error:\n" +
                              "server return code: $responseCode\n" +
                              "error message: $errorMessage\n" +
                              "original call was: ${call.request()}")

                callback.onErrorCodeOrNullBody?.invoke(call, responseCode, requestCode)
                callback.onAnyFailure?.invoke(call, responseCode, null, requestCode)
            }
        }

        catch (e: Exception)
        {
            Log.e(logTag, "${e.message}\noriginal call was: ${call.request()}", e)

            callback.apply {
                if(e is TimeoutCancellationException)
                    onTimeout?.invoke(call, timeoutMillis, requestCode)

                onException?.invoke(call, e, requestCode)
                onAnyFailure?.invoke(call, null, e, requestCode)
            }
        }
    }
}