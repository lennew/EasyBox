package android.example.easybox.activities.addingcontent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.example.easybox.Session
import android.example.easybox.data.model.Item
import android.example.easybox.databinding.ActivityAddingItemBinding
import android.example.easybox.utils.Constants
import android.example.easybox.utils.DatabaseAccess
import android.example.easybox.utils.StorageManipulation
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class AddingItemActivity : AppCompatActivity(), AddInterface<Item, ActivityAddingItemBinding> {


    lateinit var binding: ActivityAddingItemBinding

    private var name: String = Constants.STRING_NOT_SET
    private var comment: String = Constants.STRING_NOT_SET
    private var keywords: String = Constants.STRING_NOT_SET
    private var ean: String = Constants.STRING_NOT_SET

    private var bitmap: MutableList<Bitmap?> = mutableListOf(null)
    private var EDIT_MODE: Boolean? = false
    private lateinit var currentItem: Item

    private var MAIN_PHOTO_REQ_CODE = 0
    private var ADDITIONAL_PHOTO_REQ_CODE = 1
    private var EAN_SCAN = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EDIT_MODE = intent?.extras?.getBoolean("EDIT_MODE")

        if (EDIT_MODE != null && EDIT_MODE!!) {
            currentItem = DatabaseAccess.getItemById(intent?.extras?.getInt("ITEM_ID")!!)!!
            bindInformation()
        }

        binding.submitButton.setOnClickListener {
            submit()
        }

        binding.mainImage.setOnClickListener {
            addImage(this, MAIN_PHOTO_REQ_CODE)
        }

        binding.addPhoto.setOnClickListener {
            addImage(this, ADDITIONAL_PHOTO_REQ_CODE)
        }

        binding.addEan.setOnClickListener {
            scanCode(EAN_SCAN, Constants.SCANNER_SCAN_MODE, this)
        }
    }

    private fun bindInformation() {
        binding.mainImage.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.mainImage))
        binding.nameInput.setText(currentItem.name)
        binding.commentInput.setText(currentItem.comment)
        binding.keywordsInput.setText(currentItem.keywords)
        if (currentItem.ean != "") {
            binding.eanText.text = "Code scanned!"
            binding.addEan.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_rotate, applicationContext.theme))
        }
        if (currentItem.additionalImage1 != "") {
            binding.additionalPhotosContainer.visibility = View.VISIBLE
            binding.additionalPhoto1.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.additionalImage1))
            binding.additionalPhoto1.visibility = View.VISIBLE
        }
        if (currentItem.additionalImage2 != "") {
            binding.additionalPhoto2.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.additionalImage2))
            binding.additionalPhoto2.visibility = View.VISIBLE
        }
        if (currentItem.additionalImage3 != "") {
            binding.additionalPhoto3.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.additionalImage3))
            binding.additionalPhoto3.visibility = View.VISIBLE
            binding.addMorePhotosContainer.visibility = View.GONE
        }

        name = currentItem.name
        comment = currentItem.comment
        keywords = currentItem.keywords
        ean = currentItem.ean

        bitmap[0] = StorageManipulation.loadImageFromStorage(currentItem.mainImage)
        if (currentItem.additionalImage1 != "") bitmap.add(StorageManipulation.loadImageFromStorage(currentItem.additionalImage1))
        if (currentItem.additionalImage2 != "") bitmap.add(StorageManipulation.loadImageFromStorage(currentItem.additionalImage2))
        if (currentItem.additionalImage3 != "") bitmap.add(StorageManipulation.loadImageFromStorage(currentItem.additionalImage3))
    }

    override fun submit() {
        resetForm()

        name = binding.nameInput.text.toString()
        comment = binding.commentInput.text.toString()
        keywords = binding.keywordsInput.text.toString()

        if (!validate()) {
            onFail()
            return
        }

        val photosPaths: MutableList<String> = mutableListOf()
        if (bitmap[0] != null) {
            photosPaths.add(StorageManipulation.saveToInternalStorage(bitmap[0]!!, applicationContext, name + Calendar.getInstance().time + (0..100).random())!!)
        }

        for (i in 1..3) {
            if (bitmap.size > i && bitmap[i] != null) {
                photosPaths.add(StorageManipulation.saveToInternalStorage(bitmap[i]!!, applicationContext, name + Calendar.getInstance().time + (0..100).random())!!)
            } else photosPaths.add("")
        }
        val item: Item
        if (EDIT_MODE != null && EDIT_MODE!!) {
            item = currentItem
            item.name = name
            item.ean = ean
            item.keywords = keywords
            item.mainImage = photosPaths[0]
            item.additionalImage1 = photosPaths[1]
            item.additionalImage2 = photosPaths[2]
            item.additionalImage3 = photosPaths[3]
            item.comment = comment
            DatabaseAccess.update(item)
        }
        else {
            item = Item(boxId = Session.boxId, name = name, ean = ean,
                    keywords = keywords, mainImage = photosPaths[0], additionalImage1 = photosPaths[1],
                    additionalImage2 = photosPaths[2], additionalImage3 = photosPaths[3], comment = comment)
            DatabaseAccess.insert(item)
        }
        onSuccess()
    }


    override fun validate(): Boolean {
        var valid = true

        if (name.isBlank()) {
            binding.nameTextInputLayout.error = "name cannot be empty"
            valid = false
        }

        if (bitmap[0] == null) {
            binding.errorPhoto.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

    override fun onFail() {
        Toast.makeText(this, "Check form!", Toast.LENGTH_LONG).show()
        binding.submitButton.isEnabled = true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MAIN_PHOTO_REQ_CODE -> {
                bitmap[0] = getPhoto(data)
                if (bitmap[0] != null) {
                    binding.mainImage.setImageBitmap(bitmap[0])
                }
            }
            ADDITIONAL_PHOTO_REQ_CODE -> if (getPhoto(data) != null) handleAdditionalPhoto(data)
            EAN_SCAN -> {
                if (resultCode == Activity.RESULT_OK) handleEanScan(data)
                else Toast.makeText(this, "Scanning canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleAdditionalPhoto(data: Intent?) {
        binding.additionalPhotosContainer.visibility = View.VISIBLE
        var imageView: ImageView? = null
        bitmap.add(getPhoto(data))
        when (bitmap.size) {
            2 -> imageView = binding.additionalPhoto1
            3 -> imageView = binding.additionalPhoto2
            4 -> {
                imageView = binding.additionalPhoto3
                binding.addMorePhotosContainer.visibility = View.GONE
            }
        }
        imageView?.setImageBitmap(bitmap[bitmap.size - 1])
        imageView?.visibility = View.VISIBLE
    }

    private fun handleEanScan(data: Intent?) {
        if (data?.getStringExtra(Constants.BAR_CODE_RESULT) != null) {
            ean = data.getStringExtra(Constants.BAR_CODE_RESULT)
            binding.eanText.text = "Code scanned!"
            binding.addEan.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_rotate, applicationContext.theme))
            Toast.makeText(this, "Code scanned!", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "Problem with code!", Toast.LENGTH_SHORT).show()
    }

    override fun resetForm() {
        binding.submitButton.isEnabled = false
        binding.nameTextInputLayout.error = null
        binding.errorPhoto.visibility = View.INVISIBLE
    }

    override fun onSuccess() {
        finish()
    }

}
