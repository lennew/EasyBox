package android.example.easybox.activities.addingcontent

import android.example.easybox.Session
import android.example.easybox.data.model.Localization
import android.example.easybox.databinding.ActivityAddingLocalizationBinding
import android.example.easybox.utils.Constants
import android.example.easybox.utils.DatabaseAccess
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class AddingLocalizationActivity : AppCompatActivity(), AddInterface<Localization, ActivityAddingLocalizationBinding> {

    private lateinit var binding: ActivityAddingLocalizationBinding

    private var name: String = Constants.STRING_NOT_SET
    private var EDIT_MODE: Boolean? = false
    private lateinit var currentLocalization: Localization


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingLocalizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EDIT_MODE = intent?.extras?.getBoolean("EDIT_MODE")

        if (EDIT_MODE != null && EDIT_MODE!!) {
            currentLocalization = DatabaseAccess.getLocalizationById(intent?.extras?.getInt("LOCALIZATION_ID")!!)!!
            binding.nameInput.setText(currentLocalization.name)
        }

        binding.submitButton.setOnClickListener {
            submit()
        }

    }

    override fun submit() {
        resetForm()
        name = binding.nameInput.text.toString()

        if (!validate()) {
            onFail()
            return
        }

        val localization: Localization
        if(EDIT_MODE != null && EDIT_MODE!!) {
            localization = currentLocalization
            localization.name = name
            DatabaseAccess.update(localization)
        } else {
            localization = Localization(userId = Session.usrId, name = name)
            DatabaseAccess.insert(localization)
        }
        onSuccess()
    }

    override fun validate(): Boolean {
        var valid = true

        if (name.isBlank()) {
            binding.nameTextInputLayout.error = "name cannot be empty"
            valid = false
        }

        return valid
    }

    override fun onFail() {
        Toast.makeText(this, "Check form!", Toast.LENGTH_LONG).show()
        binding.submitButton.isEnabled = true
    }

    override fun onSuccess() {
        finish()
    }

    override fun resetForm() {
        binding.submitButton.isEnabled = false
        binding.nameTextInputLayout.error = null
    }
}
