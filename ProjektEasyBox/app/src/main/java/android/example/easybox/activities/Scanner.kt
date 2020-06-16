package android.example.easybox.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.easybox.data.model.Box
import android.example.easybox.databinding.ActivityScannerBinding
import android.example.easybox.utils.Constants
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class Scanner : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView
    private lateinit var binding: ActivityScannerBinding
    private val CAMERA_PERMISSION_RC = 1
    private var cameraPermission: Boolean = false
    private var SCANNER_MODE: Int = Constants.SCANNER_SCAN_MODE
    private lateinit var SEARCHED_BOX_QRCODE: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        checkPermission()
        setContentView(binding.root)
        scannerView = binding.scanner
        SCANNER_MODE = intent.extras.getInt(Constants.SCANNER_INTENT_MODE)
        SEARCHED_BOX_QRCODE = if (SCANNER_MODE == Constants.SCANNER_SEARCH_MODE ) intent.extras.getString(Constants.BOX_QRCODE_INTENT)!!
        else ""
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_RC)
        else
            this.cameraPermission = true
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        if (SCANNER_MODE == Constants.SCANNER_SCAN_MODE) {
            val resultIntent = Intent()
            resultIntent.putExtra("BAR_CODE", result!!.text)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else if (SCANNER_MODE == Constants.SCANNER_SEARCH_MODE) {
            val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            if (result!!.text == SEARCHED_BOX_QRCODE) {
                Toast.makeText(this,"You found the box", Toast.LENGTH_LONG).show()
                toneGen1.startTone(45, 150)
                setResult(Activity.RESULT_OK)
                finish()
            }
            else {
                Toast.makeText(this,"It's not the box you are looking for", Toast.LENGTH_SHORT).show()
                toneGen1.startTone(96, 150)
                this.onResume()
            }
        }
    }

    companion object {
        fun searchForBox(context: Activity, box: Box) {
            val intent = Intent(context, Scanner::class.java)
            intent.putExtra(Constants.SCANNER_INTENT_MODE, Constants.SCANNER_SEARCH_MODE)
            intent.putExtra(Constants.BOX_QRCODE_INTENT, box.code)
            context.startActivity(intent)
        }
    }
}
