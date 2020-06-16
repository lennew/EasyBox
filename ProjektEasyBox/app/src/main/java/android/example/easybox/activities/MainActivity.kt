package android.example.easybox.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.example.easybox.R
import android.example.easybox.activities.ui.main.PlaceholderFragment
import android.example.easybox.activities.ui.main.SectionsPagerAdapter
import android.example.easybox.databinding.ActivityMainBinding
import android.example.easybox.utils.Constants
import android.example.easybox.utils.DatabaseAccess
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private var searchView: SearchView? = null
    private lateinit var itemFragment: PlaceholderFragment
    private var cameraPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(view_pager)
        checkPermission()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_acitivty_menu, menu)

        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        setSearchLogic()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStop() {
        super.onStop()
        searchView?.setQuery("", true)
    }


    private fun setSearchLogic() {
        itemFragment = sectionsPagerAdapter.getRegisteredFragment(0) as PlaceholderFragment

        searchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(cs: String): Boolean {
                itemFragment.getArrayAdapter().filter.filter(cs)
                itemFragment.updateArrays()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                itemFragment.getArrayAdapter().filter.filter(query)
                itemFragment.updateArrays()
                searchView?.clearFocus()
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.search_box_qrcode -> {
                val intent = Intent(this, Scanner::class.java)
                intent.putExtra(Constants.SCANNER_INTENT_MODE, Constants.SCANNER_SCAN_MODE)
                this.startActivityForResult(intent, 0)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val box = DatabaseAccess.getBoxByQrCode(data!!.getStringExtra(Constants.BAR_CODE_RESULT))
            if (box != null) {
                Toast.makeText(this, "Box found!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra(Constants.SECTION, Constants.ITEM_LIST)
                intent.putExtra("BOX_ID_QR", box.id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Box with this QR code doesn't exists!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Scanning canceled!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    1)
        else
            this.cameraPermission = true
    }
}