package android.example.easybox.activities

import android.example.easybox.R
import android.example.easybox.Session
import android.example.easybox.data.model.Item
import android.example.easybox.databinding.ActivityItemBinding
import android.example.easybox.elements.ListPopup
import android.example.easybox.utils.DatabaseAccess
import android.example.easybox.utils.MenuSettings
import android.example.easybox.utils.StorageManipulation
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

class ItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemBinding

    private lateinit var currentItem: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentItem = DatabaseAccess.getItemById(Session.itemId)!!
    }

    override fun onResume() {
        super.onResume()
        bindData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_item -> {
                MenuSettings.delete(currentItem, this)
                finish()
            }
            R.id.change_box -> ListPopup.movePopup(currentItem, DatabaseAccess.getUserBoxes(Session.usrId), this, binding.box, null)
            R.id.edit_item -> MenuSettings.editItem(currentItem, this)
        }
        return true
    }

    private fun bindData() {
        val box = DatabaseAccess.getBoxById(currentItem.boxId)!!
        val localization = DatabaseAccess.getLocalizationById(box.localizationId)!!
        binding.itemImage.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.mainImage))
        binding.additionalPhoto1.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.additionalImage1))
        binding.additionalPhoto2.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.additionalImage2))
        binding.additionalPhoto3.setImageBitmap(StorageManipulation.loadImageFromStorage(currentItem.additionalImage3))
        if (currentItem.additionalImage1 == "" && currentItem.additionalImage2 == "" && currentItem.additionalImage3 == "")
            binding.additionalPhotosContainer.visibility = View.GONE
        binding.itemName.text = currentItem.name
        binding.localization.text = localization.name
        binding.box.text = box.name
        binding.comment.text = box.comment
    }
}
