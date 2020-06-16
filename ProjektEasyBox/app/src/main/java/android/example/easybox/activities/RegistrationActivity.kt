package android.example.easybox.activities

import android.example.easybox.App
import android.example.easybox.R
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Localization
import android.example.easybox.data.model.User
import android.example.easybox.databinding.ActivityRegistrationBinding
import android.example.easybox.utils.Constants
import android.example.easybox.utils.DatabaseAccess
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            register()
        }

        binding.linkLogin.setOnClickListener {
            finish()
        }
    }

    private fun register() {
        resetFields()

        if (!validate()) {
            onSignUpFail()
            return
        }

        val user = User(login = binding.loginTextInput.text.toString(), password = binding.passwordTextInput.text.toString())

        if (!authenticate(user)) {
            onSignUpFail()
            return
        }

        onSignUpSuccess(user)
    }

    private fun authenticate(user: User): Boolean {
        var valid = true

        if (App.database.users().getUserByLogin(user.login) != null) {
            binding.loginTextInputLayout.error = resources.getString(R.string.user_exists)
            valid = false
        }
        return valid
    }

    private fun validate(): Boolean {
        var valid = true

        val login = binding.loginTextInput.text.toString()
        val password = binding.passwordTextInput.text.toString()
        val passwordRepeated = binding.repeatPasswordTextInput.text.toString()

        if (login.isEmpty() || login.length < Constants.MIN_CHARS_LOGIN) {
            binding.loginTextInputLayout.error = resources.getString(R.string.at_least_chars, Constants.MIN_CHARS_LOGIN)
            valid = false
        }

        if (password.isEmpty() || password.length < Constants.MIN_CHARS_PASSWORD) {
            binding.passwordTextInputLayout.error = resources.getString(R.string.at_least_chars, Constants.MIN_CHARS_PASSWORD)
            valid = false
        }

        if (password != passwordRepeated) {
            binding.repeatPasswordTextInputLayout.error = resources.getString(R.string.passwords_must_match)
            binding.passwordTextInputLayout.error = resources.getString(R.string.passwords_must_match)
            valid = false
        }
        return valid
    }

    private fun onSignUpFail() {
        Toast.makeText(this, resources.getString(R.string.check_form), Toast.LENGTH_LONG).show()
        binding.registerButton.isEnabled = true
    }

    private fun onSignUpSuccess(user: User) {
        DatabaseAccess.insert(user)
        addFirstLocAndBox(DatabaseAccess.getUserId(user.login)!!)
        Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun addFirstLocAndBox(userId: Int) {
        val localization = Localization(id = -userId, userId = userId)
        DatabaseAccess.insert(localization)
        val box = Box(id = localization.id, localizationId = localization.id)
        DatabaseAccess.insert(box)
    }

    private fun resetFields() {
        binding.registerButton.isEnabled = false
        binding.loginTextInputLayout.error = null
        binding.passwordTextInputLayout.error = null
        binding.repeatPasswordTextInputLayout.error = null
        binding.registerButton.isEnabled = false
    }

}
