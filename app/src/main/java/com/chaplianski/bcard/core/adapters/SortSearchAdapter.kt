package com.chaplianski.bcard.core.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaplianski.bcard.R
import com.google.android.material.chip.Chip

class SortSearchAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val helpUnitList = mutableListOf<Int>(0,1)

    interface onClickViewListener{
        fun onSortAdding()
        fun onSortName()
        fun onSortSurname()
        fun onSortTown()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            SORT -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_sort_information, parent, false)
                SortViewHolder(v)
            }
            SEARCH -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_search_information, parent, false)
                SearchViewHolder(v)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            SORT -> {
                (holder as SortViewHolder).onBind(helpUnitList[position])
            }
            SEARCH -> {
                (holder as SearchViewHolder).onBind(helpUnitList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return helpUnitList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> SORT
            1 -> SEARCH
            else -> SORT
        }
    }

    class SortViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val sortAdding = itemView.findViewById<Chip>(R.id.bt_layout_sorting_adding)
        val sortName = itemView.findViewById<Chip>(R.id.bt_layout_sorting_name)
        val sortSurname = itemView.findViewById<Chip>(R.id.bt_layout_sorting_surname)
        val sortTown = itemView.findViewById<Chip>(R.id.bt_layout_sorting_town)

        fun onBind(position: Int){}
    }

    class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val searchText = itemView.findViewById<EditText>(R.id.et_layout_search_text)
        val searchButton = itemView.findViewById<Chip>(R.id.iv_layout_search_search)
        val searchResult = itemView.findViewById<TextView>(R.id.tv_layout_search_result)
        val searchUp = itemView.findViewById<ImageView>(R.id.iv_layout_search_top)
        val searchDown = itemView.findViewById<ImageView>(R.id.iv_layout_search_down)

        fun onBind(position: Int){}
    }

    companion object{

        const val SORT = 0
        const val SEARCH = 1
    }
}