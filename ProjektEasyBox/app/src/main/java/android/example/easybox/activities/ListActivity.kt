package android.example.easybox.activities

import android.example.easybox.R
import android.example.easybox.Session
import android.example.easybox.activities.ui.main.PlaceholderFragment
import android.example.easybox.data.model.Box
import android.example.easybox.databinding.ListActivityBinding
import android.example.easybox.utils.Constants
import android.example.easybox.utils.DatabaseAccess
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import kotlin.properties.Delegates
import android.example.easybox.elements.ListPopup


class ListActivity : AppCompatActivity() {

    private lateinit var binding: ListActivityBinding

    private var currentList by Delegates.notNull<Int>()
    private var box: Box? = null
    private lateinit var fragment: PlaceholderFragment
    private var boxId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentList = intent.getIntExtra(Constants.SECTION, Constants.ITEM_LIST)
        boxId = intent.getIntExtra("BOX_ID_QR", Session.boxId)

        if (currentList == Constants.ITEM_LIST) {
            box = DatabaseAccess.getBoxById(boxId)
            var title = "Box"
            if (box?.name != "") title += ": " + box?.name;
            setTitle(title)
        } else if (currentList == Constants.BOX_LIST) {
            title = "Localization: " + DatabaseAccess.getLocalizationById(Session.locId)!!.name
        }

        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragment = PlaceholderFragment.newInstance(currentList)
        fragmentTransaction.add(R.id.container, fragment)
        fragmentTransaction.commit()

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentList == Constants.ITEM_LIST) {
            menuInflater.inflate(R.menu.box_menu, menu)
            return true
        }
        return false
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.change_localization -> ListPopup.movePopup(box, DatabaseAccess.getUserLocalizations(Session.usrId), this, null, fragment::setAdapterAndData)
            R.id.delete_box -> {
                DatabaseAccess.delete(box, this)
                finish()
            }
            R.id.search_box -> Scanner.searchForBox(this, box!!)
        }
        return true
    }


}
