package android.example.easybox.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.*


class StorageManipulation {
    companion object {
        fun saveToInternalStorage(bitmapImage: Bitmap, applicationContext: Context, name: String): String? {
            val cw = ContextWrapper(applicationContext)
            val directory: File = cw.getDir("images", Context.MODE_PRIVATE)
            val mypath = File(directory, "$name.jpg")
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(mypath)
                val bitmap = rotateIfNeeded(bitmapImage, mypath.absolutePath)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return mypath.absolutePath
        }

        fun loadImageFromStorage(path: String) : Bitmap? {
            try {
                val f = File(path)
                return BitmapFactory.decodeStream(FileInputStream(f))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }
        private fun rotateIfNeeded(bitmap: Bitmap, photoPath: String): Bitmap {
            val ei = ExifInterface(photoPath)
            val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)

            val rotatedBitmap: Bitmap?
            rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90.toFloat())
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180.toFloat())
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270.toFloat())
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
            return rotatedBitmap!!
        }

        private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                    matrix, true)
        }
    }
}