package android.example.easybox

import kotlin.properties.Delegates

class Session {
    companion object {
        var usrId by Delegates.notNull<Int>()
        var locId by Delegates.notNull<Int>()
        var boxId by Delegates.notNull<Int>()
        var itemId by Delegates.notNull<Int>()
    }
}