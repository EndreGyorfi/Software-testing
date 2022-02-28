package edu.codespring.ro.apiaapp.android.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.data.model.Field

class FieldsAdapter(private val listener: (position: Int, action: ActionEnum, name: String, size: Float) -> Unit) :
    ListAdapter<
            Field,
            FieldsAdapter.FieldsViewHolder>(AsyncDifferConfig.Builder(DiffCallback()).build()) {
    inner class FieldsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemName = view.findViewById<TextView>(R.id.fieldName)
        private val itemSize = view.findViewById<TextView>(R.id.fieldSize)
        private val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)
        private val editButton = view.findViewById<ImageButton>(R.id.editButton)
        private var id: Int = 0
        private var size: Float = 0F
        private var defaultUnit = itemSize.text

        fun bindData(fieldData: Field) {
            id = fieldData.id
            size = fieldData.size
            itemName.text = fieldData.name
            itemSize.text = "${fieldData.size} ${defaultUnit}"
        }

        fun bindClickListener() {
            editButton.setOnClickListener {
                listener(id, ActionEnum.EDIT, itemName.text.toString(), size)
            }
            deleteButton.setOnClickListener {
                listener(id, ActionEnum.DELETE, itemName.text.toString(), size)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Field>() {
        override fun areItemsTheSame(oldItem: Field, newItem: Field): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Field, newItem: Field): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldsViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.field_item_view, parent, false)

        return FieldsViewHolder(layout)
    }

    override fun onBindViewHolder(holder: FieldsViewHolder, position: Int) {
        holder.bindData(getItem(position))
        holder.bindClickListener()
    }
}
