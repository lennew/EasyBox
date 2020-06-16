package android.example.easybox.activities.ui.main

import android.content.Context
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    private var registeredFragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        val fragment = PlaceholderFragment.newInstance(position + 1, true)
        registeredFragments.put(position, fragment)
        return fragment
    }


    fun getRegisteredFragment(position: Int): Fragment? {
        return registeredFragments.get(position)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) "Items"
        else "Localizations"
    }

    override fun getCount(): Int {
        return 2
    }

}