package android.example.easybox.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.easybox.Session
import android.example.easybox.activities.ListActivity
import android.example.easybox.activities.addingcontent.AddingBoxActivity
import android.example.easybox.activities.addingcontent.AddingItemActivity
import android.example.easybox.activities.addingcontent.AddingLocalizationActivity
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Item
import android.example.easybox.data.model.Localization
import android.widget.Toast


class MenuSettings {
    companion object {
        fun editItem(item: Item, context: Context?) {
            val intent = Intent(context, AddingItemActivity::class.java)
            intent.putExtra(Constants.EDIT_MODE, true)
            intent.putExtra(Constants.ITEM_ID, item.id)
            context?.startActivity(intent)
        }

        fun editBox(box: Box, activity: Activity?) {
            if (box.id == -Session.usrId) {
                Toast.makeText(activity, "You cannot edit this box!", Toast.LENGTH_LONG).show()
                return
            }
            val intent = Intent(activity, AddingBoxActivity::class.java)
            intent.putExtra(Constants.EDIT_MODE, true)
            intent.putExtra(Constants.BOX_ID, box.id)
            activity?.startActivity(intent)
        }

        fun editLocalization(localization: Localization, activity: Activity?) {
            if (localization.id == -Session.usrId) {
                Toast.makeText(activity, "You cannot edit this localization!", Toast.LENGTH_LONG).show()
                return
            }
            val intent = Intent(activity, AddingLocalizationActivity::class.java)
            intent.putExtra(Constants.EDIT_MODE, true)
            intent.putExtra(Constants.LOC_ID, localization.id)
            activity?.startActivity(intent)
        }

        fun goToBox(item: Item, context: Context?) {
            Session.boxId = item.boxId
            val intent = Intent(context, ListActivity::class.java)
            intent.putExtra(Constants.SECTION, Constants.ITEM_LIST)
            context?.startActivity(intent)
        }

        fun <T> delete(thing: T, activity: Context?) {
            DatabaseAccess.delete(thing, activity)
        }

        fun <T> isItem(array: Array<T>, position: Int): Boolean {
            return position < array.size && array[position] is Item
        }

        fun <T> isLocalization(array: Array<T>, position: Int): Boolean {
            return position < array.size && array[position] is Item
        }
    }
}