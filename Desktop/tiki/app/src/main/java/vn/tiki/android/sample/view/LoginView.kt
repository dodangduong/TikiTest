package vn.tiki.android.sample.view

interface LoginView {
    fun onLoginSuccess(boolean: Boolean)

    fun onLoginFaile(boolean: Boolean)

    fun onLoadListPhoneNumber(arrayList: ArrayList<String>)

    fun onShowProgress(boolean: Boolean)

    fun onLoginFaile(error: String)

    fun checkPhoneNumber(boolean: Boolean)

    fun checkPassWd(boolean: Boolean)
}