package android.example.easybox.activities.ui.main

import android.app.Activity
import android.content.Intent
import android.example.easybox.R
import android.example.easybox.Session
import android.example.easybox.activities.ItemActivity
import android.example.easybox.activities.ListActivity
import android.example.easybox.activities.Scanner
import android.example.easybox.activities.addingcontent.AddingBoxActivity
import android.example.easybox.activities.addingcontent.AddingItemActivity
import android.example.easybox.activities.addingcontent.AddingLocalizationActivity
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Item
import android.example.easybox.data.model.Localization
import android.example.easybox.databinding.FragmentMainBinding
import android.example.easybox.elements.ListPopup
import android.example.easybox.utils.*
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates

class PlaceholderFragment : Fragment() {


    private lateinit var binding: FragmentMainBinding
    private lateinit var array: Array<Any>
    lateinit var adapter: Any
    private lateinit var listView: ListView

    private var LOCALIZATION_ID by Delegates.notNull<Int>()
    private var BOX_ID by Delegates.notNull<Int>()
    private var IS_MAIN_PAGE: Boolean = false
    private var CURRENT_LIST = Constants.LIST_NOT_SET


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMainBinding.inflate(layoutInflater)

        listView = binding.thingsList

        CURRENT_LIST = arguments?.getInt(Constants.SECTION)!!
        IS_MAIN_PAGE = arguments?.getBoolean(Constants.IS_MAIN)!!
        if (arguments?.getInt("BOX_ID_QR") != 0)
            BOX_ID = arguments?.getInt("BOX_ID_QR")!!
        else
            BOX_ID = Session.boxId
        LOCALIZATION_ID = Session.locId


        binding.fab.setOnClickListener {
            val intent = setAddingIntent()
            startActivity(intent)
        }
        setAdapterAndData()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = setItemClickListener(position)
            startActivity(intent)
        }
    }

    private fun setAddingIntent(): Intent {
        return when (CURRENT_LIST) {
            Constants.ITEM_LIST -> Intent(context, AddingItemActivity::class.java)
            Constants.LOCALIZATION_LIST -> Intent(context, AddingLocalizationActivity::class.java)
            else -> Intent(context, AddingBoxActivity::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (arguments?.getInt("BOX_ID_QR") == 0)
            Session.boxId = BOX_ID
        Session.locId = LOCALIZATION_ID
        setAdapterAndData()
    }

    fun updateArrays() {
        array = getArrayAdapter().getItems() as Array<Any>
    }

    fun setAdapterAndData() {
        when (CURRENT_LIST) {
            Constants.ITEM_LIST -> setItemsAdapter()
            Constants.BOX_LIST -> setBoxesAdapter()
            Constants.LOCALIZATION_LIST -> setLocalizationsAdapter()
        }
        registerForContextMenu(listView)
    }

    private fun setLocalizationsAdapter() {
        array = getArray(DatabaseAccess.getUserLocalizations(Session.usrId))
        adapter = LocalizationListAdapter(this.context as Activity, R.layout.list_item, array as Array<Localization?>)
        listView.adapter = adapter as LocalizationListAdapter
    }

    private fun setBoxesAdapter() {
        array = getArray(DatabaseAccess.getLocalizationBoxes(LOCALIZATION_ID))
        adapter = BoxListAdapter(this.context as Activity, R.layout.list_item, array as Array<Box?>)
        listView.adapter = adapter as BoxListAdapter
    }

    private fun setItemsAdapter() {
        array = if (IS_MAIN_PAGE) getArray(DatabaseAccess.getUserItems(Session.usrId))
        else getArray(DatabaseAccess.getBoxItems(Session.boxId))
        adapter = ItemsListAdapter(this.context as Activity, R.layout.list_item, array as Array<Item?>)
        listView.adapter = adapter as ItemsListAdapter
    }

    private fun setItemClickListener(position: Int): Intent {
        var intent = Intent(context, ListActivity::class.java)
        when(CURRENT_LIST) {
            Constants.ITEM_LIST -> {
                Session.itemId = (array[position] as Item).id
                intent = Intent(context, ItemActivity::class.java)
            }
            Constants.BOX_LIST -> {
                Session.boxId = (array[position] as Box).id
                intent.putExtra(Constants.SECTION, Constants.ITEM_LIST)
            }
            Constants.LOCALIZATION_LIST -> {
                Session.locId = (array[position] as Localization).id
                intent.putExtra(Constants.SECTION, Constants.BOX_LIST)
            }
            else -> intent = Intent(context, ListActivity::class.java)
        }
        return intent
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(getMenuLayout(), menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val position = info.position
        if (MenuSettings.isItem(array, position)) {
            when (item.itemId) {
                R.id.delete_item -> MenuSettings.delete(array[position], activity)
                R.id.goto_box -> MenuSettings.goToBox(array[position] as Item, activity)
                R.id.edit_item -> MenuSettings.editItem(array[position] as Item, activity)
                R.id.change_box -> ListPopup.movePopup(array[position], DatabaseAccess.getUserBoxes(Session.usrId), activity!!, null, ::setAdapterAndData)
            }
        } else if (MenuSettings.isLocalization(array, position)) {
            when (item.itemId) {
                R.id.delete_localization -> MenuSettings.delete(array[position], activity)
                R.id.edit_localization -> MenuSettings.editLocalization(array[position] as Localization, activity)
            }
        } else {
            when (item.itemId) {
                R.id.delete_box -> MenuSettings.delete(array[position], activity)
                R.id.change_localization -> ListPopup.movePopup(array[position], DatabaseAccess.getUserLocalizations(Session.usrId), activity!!, null, ::setAdapterAndData)
                R.id.search_box -> Scanner.searchForBox(activity!!, array[position] as Box)
                R.id.edit_box -> MenuSettings.editBox(array[position] as Box, activity)
            }
        }
        onResume()
        return super.onContextItemSelected(item)
    }

    private fun getMenuLayout(): Int {
        return when(CURRENT_LIST) {
            Constants.LOCALIZATION_LIST -> R.menu.localization_menu
            Constants.BOX_LIST -> R.menu.box_menu
            Constants.ITEM_LIST -> if (IS_MAIN_PAGE) R.menu.item_menu_main_act else R.menu.item_menu
            else -> 0
        }
    }

    fun getArrayAdapter(): ItemsListAdapter {
        return this.adapter as ItemsListAdapter
    }

    private inline fun <reified T> getArray(things: List<T>): Array<Any> {
        return things.toTypedArray() as Array<Any>
    }

    companion object {
        fun newInstance(sectionNumber: Int, isMainPage: Boolean = false, boxId: Int = 0): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.SECTION, sectionNumber)
                    putBoolean(Constants.IS_MAIN, isMainPage)
                    putInt("BOX_ID_QR", boxId)
                }
            }
        }
    }
}