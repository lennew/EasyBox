package android.example.easybox.utils

import android.content.Context
import android.example.easybox.App
import android.example.easybox.Session
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Item
import android.example.easybox.data.model.Localization
import android.example.easybox.data.model.User
import android.widget.Toast

class DatabaseAccess {
    companion object {
        fun getUserId(login: String): Int? {
            return App.database.users().getUserByLogin(login)?.id
        }

        fun getItemById(id: Int): Item? {
            return App.database.items().getItemById(id)
        }

        fun getUserById(id: Int): User? {
            return App.database.users().getUserById(id)
        }

        fun getBoxById(id: Int): Box? {
            return App.database.boxes().getBoxById(id)
        }

        fun getBoxByQrCode(qrCode: String): Box? {
            return App.database.boxes().getBoxByQrCode(qrCode)
        }

        fun getBoxItems(id: Int): List<Item> {
            return App.database.boxes().getBoxItems(id)!!
        }

        fun getLocalizationById(id:Int): Localization? {
            return App.database.localizations().getLocalizationById(id);
        }

        fun getLocalizationBoxes(id: Int): List<Box> {
            return App.database.localizations().getLocalizationBoxes(id)!!
        }

        fun getUserItems(id: Int): List<Item?> {
            return App.database.users().getAllUserItems(id)!!
        }

        fun getUserLocalizations(id: Int): List<Localization> {
            return App.database.users().getUserLocalizations(id)!!
        }

        fun getUserBoxes(id: Int): List<Box> {
            return App.database.users().getAllUserBoxes(id)
        }

        fun <T> insert(thing: T) {
            when (thing) {
                is Item -> App.database.items().insert(thing)
                is Box -> App.database.boxes().insert(thing)
                is Localization -> App.database.localizations().insert(thing)
                is User -> App.database.users().insert(thing)
            }
        }

        fun <T> update(thing: T) {
            when (thing) {
                is Item -> App.database.items().update(thing)
                is Box -> {
                    App.database.boxes().update(thing)
                }
                is Localization -> App.database.localizations().update(thing)
                is User -> App.database.users().update(thing)
            }
        }

        fun <T> delete (thing: T, context: Context?) {
            when (thing) {
                is Item -> App.database.items().delete(thing)
                is Box -> {
                    if (thing.id == -Session.usrId) {
                        Toast.makeText(context, "You cannot delete this box!", Toast.LENGTH_LONG).show()
                        return
                    }
                    val items = getBoxItems((thing).id)
                    items.map {
                        it.boxId = -Session.usrId
                        update(it)
                    }
                    App.database.boxes().delete(thing)
                }
                is Localization -> {
                    if (thing.id == -Session.usrId) {
                        Toast.makeText(context, "You cannot delete this localization!", Toast.LENGTH_LONG).show()
                        return
                    }
                    val boxes = getLocalizationBoxes(thing.id)
                    boxes.map {
                        it.localizationId = -Session.usrId
                        update(it)
                    }
                    App.database.localizations().delete(thing)
                }
            }
        }
    }
}