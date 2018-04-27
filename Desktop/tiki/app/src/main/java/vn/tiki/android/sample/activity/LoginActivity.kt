package vn.tiki.android.sample.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo

import java.util.ArrayList

import vn.tiki.android.sample.R

import android.Manifest.permission.READ_CONTACTS
import android.widget.*
import vn.tiki.android.sample.model.LoginModel
import vn.tiki.android.sample.presenter.LoginPresenter
import vn.tiki.android.sample.view.LoginView

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginView {


    var mLoginPresenter: LoginPresenter? = null
    override fun onLoginSuccess(boolean: Boolean) {
        finish()
    }

    override fun onLoginFaile(boolean: Boolean) {
        mPasswordView?.setError(getString(R.string.error_incorrect_password))
        mPasswordView?.requestFocus()

    }

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//      private var mAuthTask : Boolean = false;
    // UI references.
    var mEmailView: AutoCompleteTextView? = null
    var mLoginFormView: View? = null
    var mPasswordView: EditText? = null
    var mProgressView: View? = null
    var mEmailSignInButton: Button? = null
    var cancel = false
    var focusView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.
        mEmailView = findViewById<View>(R.id.email) as AutoCompleteTextView
        populateAutoComplete()

        mPasswordView = findViewById<View>(R.id.password) as EditText
        mLoginPresenter = LoginModel(this, LoginActivity())
        mPasswordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        mEmailSignInButton = findViewById<View>(R.id.email_sign_in_button) as Button
        mEmailSignInButton?.setOnClickListener {
            attemptLogin()
        }

        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    override fun onShowProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
//        Toast.makeText(this,"show is " + show, Toast.LENGTH_LONG).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
            mLoginFormView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
//            if (mAuthTask != null) {
//              return;
//            }

        // Reset errors.
        mEmailView!!.error = null
        mPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val email = mEmailView!!.text.toString()
        val password = mPasswordView!!.text.toString()


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            mLoginPresenter?.login(email, password)
        }
    }

    override fun checkPassWd(boolean: Boolean) {
        // Check for a valid password, if the user entered one.
        if (!boolean) {
            mPasswordView!!.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }
    }


    override fun checkPhoneNumber(boolean: Boolean) {
        // Check for a valid email address.
        if (!boolean) {
            mEmailView!!.error = getString(R.string.error_field_required)
            focusView = mEmailView
            cancel = true
        }

    }

    fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView!!, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }
        mLoginPresenter?.registerLoader()

    }

    override fun onLoginFaile(error: String) {
        mPasswordView?.setError(getString(R.string.error_incorrect_password))
        mPasswordView?.requestFocus()
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0
    }


    override fun onLoadListPhoneNumber(arrayList: ArrayList<String>) {
        val adapter = ArrayAdapter<String>(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, arrayList)

        mEmailView?.setAdapter<ArrayAdapter<String>>(adapter)
    }
}

