package or_dvir.hotmail.com.dxutils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.util.Locale
import kotlin.math.roundToInt

typealias simpleCallback = () -> Unit

//region sms
fun Context.sendSMS(number: String, @StringRes textRes: Int = -1): Boolean {
    val text =
        if (textRes == -1) {
            ""
        } else {
            resources.getString(textRes)
        }

    return sendSMS(number, text)
}

fun Context.sendSMS(number: String, text: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
//endregion

fun Context.makeCall(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

//region email
fun Context.email(
    email: String,
    @StringRes subjectRes: Int = -1,
    @StringRes textRes: Int = -1
): Boolean {
    val subject =
        if (subjectRes == -1) {
            ""
        } else {
            resources.getString(subjectRes)
        }

    val text =
        if (textRes == -1) {
            ""
        } else {
            resources.getString(textRes)
        }

    return email(email, subject, text)
}

fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))

        if (subject.isNotEmpty()) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (text.isNotEmpty()) {
            putExtra(Intent.EXTRA_TEXT, text)
        }
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }

    return false
}
//endregion

//region browse
fun Context.browse(@StringRes urlRes: Int, newTask: Boolean = false) =
    browse(resources.getString(urlRes), newTask)

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)

            if (newTask) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}
//endregion

//region share
fun Context.share(@StringRes textRes: Int, @StringRes subjectRes: Int = -1): Boolean {
    val text = resources.getString(textRes)
    val subject =
        if (subjectRes == -1) {
            ""
        } else {
            resources.getString(subjectRes)
        }

    return share(text, subject)
}

fun Context.share(text: String, subject: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }

        startActivity(Intent.createChooser(intent, null))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}
//endregion

inline fun <reified T : Any> AppCompatActivity.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Context.startActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

/**
 * calls [String.lowercase] with the default locale
 */
fun String.toLowerCaseDefaultLocale() = this.lowercase(Locale.getDefault())

fun Any?.isNull() = this == null
fun Any?.isNotNull() = this != null

/**
 * @return Boolean whether or not at least one object in the given [Iterable] matches the given
 * predicate
 */
inline fun <T> Iterable<T>.atLeastOne(predicate: (T) -> Boolean) = none(predicate).not()

/**
 * @return Boolean whether or this this object equals any of the given objects
 */
fun Any.equalsAnyOf(vararg objects: Any): Boolean {
    for (obj in objects) {
        if (this == obj) {
            return true
        }
    }

    return false
}

/**
 * @receiver List<String> the source list to check for duplicates
 * @return Map<String, Int> where the key is the repeated string, and the value is the number of
 * times it is repeated
 */
fun List<String>.getDuplicates(): Map<String, Int> =
    this.groupingBy { it }.eachCount().filterValues { it > 1 }

/**
 * checks whether this array of strings contains the given [str], where all strings are trimmed and
 * case is ignored.
 * @param skipStr String? an optional string in this array to be ignored by this function
 * @param str String the string to look for in this array
 */
fun Array<String>.containsIgnoreCaseTrimmed(str: String, skipStr: String? = null): Boolean {
    val inputTrimmed = str.trim()
    var currentTrimmed: String

    for (it in this) {
        currentTrimmed = it.trim()

        if (skipStr.isNotNull() && skipStr.equals(currentTrimmed, true)) {
            continue
        }

        if (currentTrimmed.equals(inputTrimmed, true)) {
            return true
        }
    }

    return false
}

fun Int.dpToPx() = this.toFloat().dpToPx()
fun Int.pxToDp() = this / Resources.getSystem().displayMetrics.density
fun Float.dpToPx() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

fun View.makeVisibleOrGone(isVisible: Boolean) {
    if (isVisible) {
        makeVisible()
    } else {
        makeGone()
    }
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

/**
 * @param flag see [InputMethodManager.showSoftInput] for details
 */
fun showKeyBoard(view: View, flag: Int) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(view, flag)
}

/**
 * @param flag see [InputMethodManager.hideSoftInputFromWindow] for details
 */
fun hideKeyBoard(view: View, flag: Int) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(view.windowToken, flag)
}

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
fun AppCompatActivity.setHomeUpEnabled(enabled: Boolean) =
    supportActionBar?.setDisplayHomeAsUpEnabled(enabled)

/**
 * @param requestedPermissions the permissions requested by the app
 * @param grantResults the users' results of allowing/denying [requestedPermissions]
 * @return a list of the denied permissions
 */
fun getUserDeniedPermissions(
    requestedPermissions: Array<String>,
    grantResults: IntArray
)
        : List<String> {
    val denied = ArrayList<String>(requestedPermissions.size)

    grantResults.forEachIndexed { index, value ->
        if (value == PackageManager.PERMISSION_DENIED)
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
fun AppCompatActivity.showPermissionsRationaleDialog(
    @StringRes message: Int,
    posBtnOpenSettings: Boolean,
    @StringRes posBtnTxt: Int,
    posBtnListener: simpleCallback? = null,
    @StringRes negBtnTxt: Int = -1,
    negBtnListener: simpleCallback? = null,
    isCancelable: Boolean = false
) {
    AlertDialog.Builder(this).apply {
        setCancelable(isCancelable)
        setMessage(message)

        var posListener = posBtnListener

        if (posBtnOpenSettings) {
            posListener = {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    startActivity(this)
                }
            }
        }

        setPositiveButton(posBtnTxt) { _, _ -> posListener?.invoke() }
        if (negBtnTxt != -1)
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
fun AppCompatActivity.showSimpleDialog(
    @StringRes message: Int,
    @StringRes btnTxt: Int,
    @StringRes title: Int = -1,
    onClickListener: simpleCallback? = null,
    isCancelable: Boolean = false
) {
    AlertDialog.Builder(this).apply {
        if (title != -1)
            setTitle(title)

        setMessage(message)
        setCancelable(isCancelable)
        setPositiveButton(btnTxt) { _, _ -> onClickListener?.invoke() }
    }.show()
}