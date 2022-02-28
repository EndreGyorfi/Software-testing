package edu.codespring.ro.apiaapp.android.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.progressview.ProgressView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData

class PlannedPackagesAdapter(private val listener: (position: Int, action: ActionEnum) -> Unit) :
    ListAdapter<
            PlannedPackageData,
        PlannedPackagesAdapter.PackagesViewHolder>(AsyncDifferConfig.Builder(DiffCallback()).build()) {
    inner class PackagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemTitle = view.findViewById<TextView>(R.id.packageCardTitle)
        private val itemLocation = view.findViewById<TextView>(R.id.packageCardLocation)
        private val itemCategory = view.findViewById<TextView>(R.id.packageCardCategory)
        private val itemValue = view.findViewById<TextView>(R.id.packageCardValue)
        private val deleteButton = view.findViewById<ImageButton>(R.id.imageButton)
        private var packageId: Long = 0
        private var id: Long = 0
        private val addFieldLayout = view.findViewById<ConstraintLayout>(R.id.addFieldLayout)
        private var addedFields = view.findViewById<TextView>(R.id.addedFields)
        private var progressView = view.findViewById<ProgressView>(R.id.fieldsProgress)
        private val addToActivePackages = view.findViewById<Button>(R.id.addToActivePackagesButton)

        fun bindData(plannedPackageData: PlannedPackageData) {
            id = plannedPackageData.id
            packageId = plannedPackageData.packageData.id
            itemTitle.text = plannedPackageData.packageData.name
            itemLocation.text = plannedPackageData.packageData.townships?.joinToString { "${it.name} ${it.code}" }
            itemCategory.text = plannedPackageData.packageData.category
            itemValue.text = plannedPackageData.packageData.value.toString()
            addedFields.text = plannedPackageData.addedFieldsString
            progressView.labelText = plannedPackageData.addedFieldsSize.toString()
            progressView.progress = plannedPackageData.addedFieldsSize!!
        }

        fun bindClickListener() {
            itemView.setOnClickListener{
                listener(packageId.toInt(), ActionEnum.DETAIL)
            }
            deleteButton.setOnClickListener {
                listener(id.toInt(), ActionEnum.DELETE)
            }
            addToActivePackages.setOnClickListener {
                listener(id.toInt(), ActionEnum.ADD_TO_ACTIVE)
            }
            addFieldLayout.setOnClickListener {
                listener(id.toInt(), ActionEnum.ADD_FIELD)
            }
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<PlannedPackageData>() {
        override fun areItemsTheSame(oldItem: PlannedPackageData, newItem: PlannedPackageData): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PlannedPackageData, newItem: PlannedPackageData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.planned_package_item_view, parent, false)
        return PackagesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PackagesViewHolder, position: Int) {
        holder.bindData(getItem(position))
        holder.bindClickListener()
    }
}
