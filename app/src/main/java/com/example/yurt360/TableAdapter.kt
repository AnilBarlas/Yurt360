package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(
    private val tables: List<Table>,
    private val onClick: (Table) -> Unit
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvTableName)
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(tables[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_item, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tables[position]
        holder.tvName.text = table.name
    }

    override fun getItemCount(): Int = tables.size
}
