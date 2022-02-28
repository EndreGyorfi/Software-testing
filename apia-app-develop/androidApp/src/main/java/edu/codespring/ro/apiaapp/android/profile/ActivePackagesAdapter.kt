package edu.codespring.ro.apiaapp.android.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.data.model.ActivePackageData

class ActivePackagesAdapter(private val listener: (position: Int, action: ActionEnum) -> Unit) :
    ListAdapter<
            ActivePackageData,
            ActivePackagesAdapter.PackagesViewHolder>(AsyncDifferConfig.Builder(DiffCallback()).build()) {
    inner class PackagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemTitle = view.findViewById<TextView>(R.id.packageCardTitle)
        private val itemLocation = view.findViewById<TextView>(R.id.packageCardLocation)
        private val itemCategory = view.findViewById<TextView>(R.id.packageCardCategory)
        private val itemValue = view.findViewById<TextView>(R.id.packageCardValue)
        private var id:Long = 0
        private var packageId: Long = 0

        fun bindData(activePackageData: ActivePackageData) {
            id = activePackageData.id
            packageId = activePackageData.packageData.id
            itemTitle.text = activePackageData.packageData.name
            itemLocation.text = activePackageData.packageData.townships?.joinToString { "${it.name} ${it.code}" }
            itemCategory.text = activePackageData.packageData.category
            itemValue.text = activePackageData.packageData.value.toString()
        }

        fun bindClickListener() {
            itemView.setOnClickListener{
                listener(packageId.toInt(), ActionEnum.DETAIL)
            }
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<ActivePackageData>() {
        override fun areItemsTheSame(oldItem: ActivePackageData, newItem: ActivePackageData): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ActivePackageData, newItem: ActivePackageData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.active_package_item_view, parent, false)
        return PackagesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PackagesViewHolder, position: Int) {
        holder.bindData(getItem(position))
        holder.bindClickListener()
    }
}
