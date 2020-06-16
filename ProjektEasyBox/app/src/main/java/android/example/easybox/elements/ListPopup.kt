package android.example.easybox.elements

import android.app.Activity
import android.app.AlertDialog
import android.example.easybox.Session
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Item
import android.example.easybox.data.model.Localization
import android.example.easybox.utils.DatabaseAccess
import android.widget.TextView
import android.widget.Toast
import kotlin.reflect.KFunction0


class ListPopup {
    companion object {
        fun<T, E> movePopup(thing: T, wrappings: List<E>, activity: Activity, textView: TextView?, setAdapterAndData: KFunction0<Unit>?) {
            if (thing is Box && thing.id == -Session.usrId) {
                Toast.makeText(activity, "You cannot move this box!", Toast.LENGTH_LONG).show()
                return
            }
            val names = getWrappingsNames(wrappings)
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Move to...")
            builder.setItems(names) { dialog, which ->
                changeWrapping(thing, wrappings, which, textView, setAdapterAndData)
                showToast(thing, wrappings[which], activity)
            }
            builder.show()
        }


        private fun<T, E> showToast(thing: T, wrapping: E, activity: Activity) {
            var thingName = ""
            var wrappingName = ""
            if (thing is Item && wrapping is Box) {
                thingName = thing.name
                wrappingName = wrapping.name
            }
            else if (thing is Box && wrapping is Localization) {
                thingName = thing.name
                wrappingName = wrapping.name
            }
            Toast.makeText(activity, "$thingName moved to $wrappingName", Toast.LENGTH_LONG).show()
        }

        private fun<T, E> changeWrapping(thing: T, wrappings: List<E>, picked: Int, textView: TextView?, setAdapterAndData: KFunction0<Unit>?) {
            if (thing is Item) {
                val pickedBox = wrappings[picked] as Box
                thing.boxId = pickedBox.id
                if (textView != null)
                    textView.text = pickedBox.name
            } else if (thing is Box) {
                val pickedLocalization = wrappings[picked] as Localization
                thing.localizationId = pickedLocalization.id
                if (textView != null)
                    textView.text = pickedLocalization.name
            }
            DatabaseAccess.update(thing)
            if (setAdapterAndData != null)
                setAdapterAndData()
        }

        private fun <E> getWrappingsNames(wrappings: List<E>): Array<String> {
            return if (wrappings[0] is Box) (wrappings.map { (it as Box).name }).toTypedArray()
            else (wrappings.map { (it as Localization).name }).toTypedArray()
        }
    }
}