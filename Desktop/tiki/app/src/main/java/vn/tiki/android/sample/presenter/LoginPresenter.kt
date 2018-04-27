package vn.tiki.android.sample.presenter

interface LoginPresenter {

    fun login(phoneNumber: String, passWord: String)

    fun register(phoneNumber: String, passWord: String)

    fun registerLoader()
}