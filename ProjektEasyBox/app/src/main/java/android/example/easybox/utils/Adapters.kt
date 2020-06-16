package android.example.easybox.utils

import android.annotation.SuppressLint
import android.content.Context
import android.example.easybox.Session
import android.example.easybox.data.model.Box
import android.example.easybox.data.model.Item
import android.example.easybox.data.model.Localization
import android.example.easybox.databinding.ListItemBinding
import android.example.easybox.databinding.LocalizationItemBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes

class ItemsListAdapter(context: Context, @LayoutRes private val layoutResource: Int, private var items: Array<Item?>) :
        ArrayAdapter<Item?>(context, layoutResource, items) {

    private lateinit var binding: ListItemBinding

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = ListItemBinding.inflate(LayoutInflater.from(context))
        val rowView: View = binding.root
        binding.image.setImageBitmap(StorageManipulation.loadImageFromStorage(items[position]?.mainImage!!))
        binding.name.text = items[position]?.name
        val box = DatabaseAccess.getBoxById(items[position]?.boxId!!)
        binding.box.text = box?.name
        binding.localization.text = DatabaseAccess.getLocalizationById(box?.localizationId!!)?.name
        return rowView
    }

    fun getItems(): Array<Item?> {
        return this.items
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var filteredItems = DatabaseAccess.getUserItems(Session.usrId)
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                if (constraint != null) {
                    filteredItems = filteredItems.filter { item ->
                        item?.name!!
                                .toLowerCase()
                                .contains(constraint.toString().toLowerCase())
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                items = (results!!.values as ArrayList<Item?>).toTypedArray()
                notifyDataSetChanged()
            }
        }
    }
}

class LocalizationListAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val localizations: Array<Localization?>) :
        ArrayAdapter<Localization?>(context, layoutResource, localizations) {

    private lateinit var binding: LocalizationItemBinding

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = LocalizationItemBinding.inflate(LayoutInflater.from(context))
        val rowView: View = binding.root
        val name = binding.name
        name.text = localizations[position]?.name
        return rowView
    }
}

class BoxListAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val boxes: Array<Box?>) :
        ArrayAdapter<Box?>(context, layoutResource, boxes) {

    private lateinit var binding: LocalizationItemBinding

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = LocalizationItemBinding.inflate(LayoutInflater.from(context))
        val rowView: View = binding.root
        binding.name.text = boxes[position]?.name
        if (boxes[position]?.photo != null)
            binding.image.setImageBitmap(StorageManipulation.loadImageFromStorage(boxes[position]?.photo!!))
        binding.localization.text = DatabaseAccess.getLocalizationById(boxes[position]?.localizationId!!)?.name
        return rowView
    }

}