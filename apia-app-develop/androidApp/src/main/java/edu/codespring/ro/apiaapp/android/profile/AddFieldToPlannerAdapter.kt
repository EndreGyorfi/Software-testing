package edu.codespring.ro.apiaapp.android.profile

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.data.model.Field

class AddFieldToPlannerAdapter(private val listener: (fieldId: Int) -> Unit) :
    ListAdapter<
            Field,
            AddFieldToPlannerAdapter.FieldsViewHolder>(AsyncDifferConfig.Builder(DiffCallback()).build()) {
    inner class FieldsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.fieldName)
        private val itemSize = view.findViewById<TextView>(R.id.size)
        private val itemSizeType = view.findViewById<TextView>(R.id.fielSizeType)
        private val selectionTick = view.findViewById<ImageView>(R.id.selectionTick)

        private var fieldId: Int = 0
        private var defaultUnit:String = "he"

        fun bindData(fieldData: Field) {
            fieldId = fieldData.id
            itemName.text = fieldData.name
            itemSize.text = fieldData.size.toString()
            itemSizeType.text = defaultUnit
            if(fieldData.checked == true){
                selectionTick.visibility = View.VISIBLE
            }else{
                selectionTick.visibility = View.INVISIBLE
            }
        }

        fun bindClickListener() {
            itemView.setOnClickListener {
                listener(fieldId)
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
            .inflate(R.layout.add_field_item_view, parent, false)

        return FieldsViewHolder(layout)
    }

    override fun onBindViewHolder(holder: FieldsViewHolder, position: Int) {
        holder.bindData(getItem(position))
        holder.bindClickListener()
    }
}
