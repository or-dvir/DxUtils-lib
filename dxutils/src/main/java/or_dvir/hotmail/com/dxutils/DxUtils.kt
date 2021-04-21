package or_dvir.hotmail.com.dxutils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt

typealias simpleCallback = () -> Unit


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