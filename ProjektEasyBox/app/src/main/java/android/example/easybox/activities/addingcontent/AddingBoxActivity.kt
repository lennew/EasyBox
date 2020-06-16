package android.example.easybox.activities.addingcontent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.example.easybox.R
import android.example.easybox.Session
import android.example.easybox.data.model.Box
import android.example.easybox.databinding.ActivityAddingBoxBinding
import android.example.easybox.utils.Constants
import android.example.easybox.utils.DatabaseAccess
import android.example.easybox.utils.StorageManipulation
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class AddingBoxActivity : AppCompatActivity(), AddInterface<Box, ActivityAddingBoxBinding> {


    private lateinit var binding: ActivityAddingBoxBinding

    private var name: String = Constants.STRING_NOT_SET
    private var comment: String = Constants.STRING_NOT_SET
    private var qrCode: String = Constants.STRING_NOT_SET + "B"
    private var mainImagePath: String = Constants.STRING_NOT_SET
    private var bitmap: Bitmap? = null

    private var EDIT_MODE: Boolean? = false
    private lateinit var currentBox: Box


    private val PHOTO_RESULT = 0
    private val QRCODE_RESULT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EDIT_MODE = intent?.extras?.getBoolean("EDIT_MODE")

        if (EDIT_MODE != null && EDIT_MODE!!) {
            currentBox = DatabaseAccess.getBoxById(intent?.extras?.getInt("BOX_ID")!!)!!
            bindInformation()
        }


        binding.submitButton.setOnClickListener {
            submit()
        }

        binding.mainImage.setOnClickListener {
            addImage(this, PHOTO_RESULT)
        }

        binding.qrPhoto.setOnClickListener {
            scanCode(QRCODE_RESULT, Constants.SCANNER_SCAN_MODE, this)
        }
    }

    private fun bindInformation() {
        binding.nameInput.setText(currentBox.name)
        binding.commentInput.setText(currentBox.comment)
        binding.qrText.text = currentBox.code
        binding.qrPhoto.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_rotate, applicationContext.theme))
        if (currentBox.photo != "") {
            binding.mainImage.setImageBitmap(StorageManipulation.loadImageFromStorage(currentBox.photo))
        }
    }

    override fun submit() {
        resetForm()

        name = binding.nameInput.text.toString()
        comment = binding.commentInput.text?.toString().orEmpty()

        if (!validate()) {
            onFail()
            return
        }

        if (bitmap != null) {
            mainImagePath = StorageManipulation.saveToInternalStorage(bitmap!!, applicationContext, name + Calendar.getInstance().time)!!
        }

        val box: Box
        if (EDIT_MODE != null && EDIT_MODE!!) {
            box = currentBox
            box.comment = comment
            box.name = name
            box.code = qrCode
            box.photo = mainImagePath
            DatabaseAccess.update(box)
        } else {
            box = Box(localizationId = Session.locId, name = name, comment = comment, code = qrCode, photo = mainImagePath)
            DatabaseAccess.insert(box)
        }

        onSuccess()
    }

    override fun validate(): Boolean {
        var valid = true

        if (name.isBlank()) {
            binding.nameTextInputLayout.error = "name cannot be empty"
            valid = false
        }

        if (qrCode.isBlank()) {
            binding.errorMessage.text = resources.getString(R.string.qr_code_needed)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PHOTO_RESULT -> {
                bitmap = getPhoto(data)
                if (bitmap != null)
                    binding.mainImage.setImageBitmap(bitmap)
            }
            QRCODE_RESULT -> {
                if (resultCode == Activity.RESULT_OK) handleQrCodeScan(data)
                else Toast.makeText(this, "Scanning canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleQrCodeScan(data: Intent?) {
        val qr = data?.getStringExtra(Constants.BAR_CODE_RESULT)!!
        if (!checkIfQrExists(qr)) {
            binding.qrText.text = qr
            binding.qrPhoto.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_rotate, applicationContext.theme))
            qrCode = qr
            Toast.makeText(this, "QR code scanned!", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "QR code already exists", Toast.LENGTH_SHORT).show()
    }

    private fun checkIfQrExists(qr: String): Boolean {
        if (DatabaseAccess.getUserBoxes(Session.usrId).find { box -> box.code == qr } == null) {
            return false
        }
        return true
    }
}
