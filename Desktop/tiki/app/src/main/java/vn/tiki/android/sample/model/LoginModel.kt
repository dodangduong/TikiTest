package vn.tiki.android.sample.model

import android.R
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract
import vn.tiki.android.sample.activity.LoginActivity
import vn.tiki.android.sample.presenter.LoginPresenter
import vn.tiki.android.sample.view.LoginView
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class LoginModel : LoginPresenter, LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")

    var mAuthTask: UserLoginTask? = null;


    var mLoginView: LoginView? = null

    var mLoginActivity: LoginActivity? = null


    constructor(loginView: LoginView, loginActivity: LoginActivity) {
        this.mLoginView = loginView
        this.mLoginActivity = loginActivity
    }

    constructor() {

    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor>? {
        return CursorLoader(mLoginActivity,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    private interface ProfileQuery {
        companion object {

            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            val ADDRESS = 0
            val IS_PRIMARY = 1
        }
    }


    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        mLoginView?.onLoadListPhoneNumber(emailAddressCollection as ArrayList<String>)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }


    override fun login(phoneNumber: String, passWord: String) {
        if (checkNumberPhone(phoneNumber) && checkPassWd(passWord)) {
            mLoginView?.onShowProgress(true)
            mAuthTask = UserLoginTask(phoneNumber, passWord)
            mAuthTask!!.execute(null as Void?)
        } else {
            return
        }

    }

    override fun register(phoneNumber: String, passWord: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }

            for (credential in DUMMY_CREDENTIALS) {
                val pieces = credential.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (pieces[0] == mEmail) {
                    // Account exists, return true if the password matches.
                    return pieces[1] == mPassword
                } else {
                    return false
                }
            }

            // TODO: register the new account here.
            return true
        }

        override fun onCancelled() {
            mAuthTask = null
            mLoginView?.onShowProgress(false)
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            mLoginView?.onShowProgress(false)
            if (success!!) {
                mLoginView?.onLoginSuccess(success)
            } else {
                mLoginView?.onLoginFaile(success)
            }
        }
    }

    fun checkPassWd(passWord: String) : Boolean {
        if (!passWord.isEmpty() && passWord.length > 6) {
            mLoginView?.checkPassWd(true)
            return true
        } else {
            mLoginView?.checkPhoneNumber(false)
            return false
        }

    }

    fun checkNumberPhone(number: String) : Boolean {
        val pattern = Pattern.compile("^[0-9]*$")
        if (pattern.matcher(number).matches()) {
            return true
        }else {
            return false
        }
    }

    override fun registerLoader() {
        mLoginActivity?.loaderManager?.initLoader(0, null, this)
    }


}