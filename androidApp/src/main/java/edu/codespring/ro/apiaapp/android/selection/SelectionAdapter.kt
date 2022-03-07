package edu.codespring.ro.apiaapp.android.selection

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
import edu.codespring.ro.apiaapp.data.model.SelectionData

class SelectionAdapter(private val listener: (position: Int) -> Unit):
    ListAdapter<
            SelectionData,
            SelectionAdapter.SelectionViewHolder>(AsyncDifferConfig.Builder(DiffCallback()).build()) {

    inner class SelectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemName = view.findViewById<TextView>(R.id.selectionName)
        private val itemTick = view.findViewById<ImageView>(R.id.selectionTick)

        fun bindData(selectionData: SelectionData) {
            // ui logic
            itemName.text = selectionData.name
            if (selectionData.isSelected) {
                itemTick.visibility = View.VISIBLE
            } else {
                itemTick.visibility = View.INVISIBLE
            }
        }

        fun bindClickListener(position: Int) {
            itemView.setOnClickListener {
                listener(position)
            }
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<SelectionData>() {
        override fun areItemsTheSame(oldItem: SelectionData, newItem: SelectionData): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SelectionData, newItem: SelectionData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.selection_item_view, parent, false)

        return SelectionViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        holder.bindData(getItem(position))
        holder.bindClickListener(position)
    }
}
