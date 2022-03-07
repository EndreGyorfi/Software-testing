package edu.codespring.ro.apiaapp.android.packages

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.data.model.PackageData

class PackagesAdapter(private val listener: (position: Int) -> Unit) :
    ListAdapter<
            PackageData,
        PackagesAdapter.PackagesViewHolder>(AsyncDifferConfig.Builder(DiffCallback()).build()) {
    inner class PackagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemTitle = view.findViewById<TextView>(R.id.packageCardTitle)
        private val itemLocation = view.findViewById<TextView>(R.id.packageCardLocation)
        private val itemDescription = view.findViewById<TextView>(R.id.packageCardDescription)
        private val itemCategory = view.findViewById<TextView>(R.id.packageCardCategory)
        private val itemValue = view.findViewById<TextView>(R.id.packageCardValue)
        private val itemImage = view.findViewById<ImageView>(R.id.packageCardImage)
        private var id:Long = 0

        fun bindData(packageData: PackageData) {
            id = packageData.id
            itemTitle.text = packageData.name
            itemLocation.text = packageData.townships?.joinToString { "${it.name} ${it.code}" }
            itemDescription.text = packageData.description
            itemCategory.text = packageData.category
            itemValue.text = packageData.value.toString()


            when(packageData.category) {
                PackageCategories.Vin.toString() -> itemImage.setImageResource(R.drawable.wine)
                PackageCategories.Legume.toString() -> itemImage.setImageResource(R.drawable.vegetable)
            }

        }

        fun bindClickListener() {
            itemView.setOnClickListener {
                listener(id.toInt())
            }
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<PackageData>() {
        override fun areItemsTheSame(oldItem: PackageData, newItem: PackageData): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PackageData, newItem: PackageData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.package_item_view, parent, false)

        return PackagesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PackagesViewHolder, position: Int) {
        holder.bindData(getItem(position))
        holder.bindClickListener()
    }
}
