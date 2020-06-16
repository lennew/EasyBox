package android.example.easybox.activities

import android.content.Intent
import android.example.easybox.App
import android.example.easybox.R
import android.example.easybox.Session
import android.example.easybox.data.AppDatabase
import android.example.easybox.data.model.User
import android.example.easybox.databinding.ActivityLoginBinding
import android.example.easybox.utils.Constants
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var usr: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this.deleteDatabase("easyBoxDatabase")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        App.database = AppDatabase.getAppDataBase(this)!!

        binding.loginButton.setOnClickListener {
            login()
        }

        binding.linkRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    fun login() {
        resetFields()

        if (!validate()) {
            onLoginFailed()
            return
        }

        usr = App.database.users().getUserByLogin(binding.loginTextInput.text.toString())

        if (!authenticate()) {
            onLoginFailed()
            return
        }
        onLoginSuccess()
    }

    private fun authenticate(): Boolean {
        var success = true

        if (isPasswordValid()) {
            binding.errorMessage.text = resources.getString(R.string.wrong_login_password)
            binding.errorMessage.visibility = View.VISIBLE
            success = false
        }
        return success
    }

    private fun validate(): Boolean {
        var valid = true

        val login = binding.loginTextInput.text.toString()
        val password = binding.passwordTextInput.text.toString()

        if (login.isEmpty() || login.length < Constants.MIN_CHARS_LOGIN) {
            binding.loginTextInputLayout.error = resources.getString(R.string.at_least_chars, Constants.MIN_CHARS_LOGIN)
            valid = false
        }

        if (password.isEmpty() || password.length < Constants.MIN_CHARS_PASSWORD) {
            binding.passwordTextInputLayout.error = resources.getString(R.string.at_least_chars, Constants.MIN_CHARS_PASSWORD)
            valid = false
        }
        return valid
    }

    private fun onLoginFailed() {
        Toast.makeText(this, resources.getString(R.string.check_form), Toast.LENGTH_LONG).show()
        binding.loginButton.isEnabled = true
    }

    private fun onLoginSuccess() {
        Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_SHORT).show()
        setSession(usr!!.id)
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setSession(id: Int) {
        Session.usrId = id
        Session.locId = -id
        Session.boxId = -id
    }

    private fun isPasswordValid(): Boolean {
        return usr?.password != binding.passwordTextInput.text.toString() || usr == null
    }

    private fun resetFields() {
        binding.loginTextInputLayout.error = null
        binding.passwordTextInputLayout.error = null
        binding.errorMessage.visibility = View.INVISIBLE
        binding.loginButton.isEnabled = false
    }
}
