package com.katiekilroy.myapplication.unused

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.katiekilroy.myapplication.databinding.FragmentAddEdgeBinding
import com.katiekilroy.myapplication.placeholder.BeaconContent

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val values: List<BeaconContent.BeaconItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentAddEdgeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.nameView.text = item.name
        holder.weightView.text = item.weight
        holder.directionView.text = item.direction
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAddEdgeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val nameView: TextView = binding.content
        val weightView: TextView = binding.content
        val directionView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'" + weightView.text + "'" + directionView.text + "'"
        }
    }

}