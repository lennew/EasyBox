package android.example.easybox.activities.addingcontent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.example.easybox.activities.Scanner
import android.example.easybox.utils.Constants
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


interface AddInterface<T, E> {
    fun submit()
    fun validate(): Boolean
    fun onFail()
    fun onSuccess()
    fun resetForm()


    fun addImage(activity: Activity, requestCode: Int) {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(takePicture, requestCode)
    }

    fun getPhoto(data: Intent?): Bitmap? {
        if (data == null) {
            return null
        }
        return data.extras.get("data") as Bitmap
    }

    fun scanCode(resultCode: Int, scannerMode: Int, context: Activity) {
        val intent = Intent(context, Scanner::class.java)
        intent.putExtra(Constants.SCANNER_INTENT_MODE, Constants.SCANNER_SCAN_MODE)
        context.startActivityForResult(intent, resultCode)
    }
}