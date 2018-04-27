package vn.tiki.android.sample.activity

import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import vn.tiki.android.sample.model.LoginModel

class LoginActivityTest {

    var mLogMode : LoginModel? = null

    @Before
    fun setUp() {
        mLogMode = LoginModel()
    }



    @Test
    fun testPhoneNumberValid() {
        assertTrue(mLogMode?.checkNumberPhone("0982389487")!!)
    }

    @Test
    fun testPhoneNumberlNotValid() {
        assertFalse(mLogMode?.checkNumberPhone("abc")!!)
    }

    @Test
    fun testPassIsValid() {
        assertTrue(mLogMode?.checkPassWd("1234567")!!)
    }

    @Test
    fun testPassNotValid() {
        assertFalse(mLogMode?.checkPassWd("1234")!!)
    }

    @After
    fun tearDown() {
    }
}