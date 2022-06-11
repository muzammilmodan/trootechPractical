package com.trootechdemo.ui.category.adtr

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trootechdemo.databinding.RowMainCategoryBinding
import com.trootechdemo.listeners.RecyclerViewClickListener
import com.trootechdemo.model.Franquicia

class CategoryAdapter(
    var mContext: Context,
    var alLanguage: ArrayList<Franquicia>,
    var btnListener: RecyclerViewClickListener
) : RecyclerView.Adapter<CategoryAdapter.LanguageViewHolder>() {

    companion object {
        var mClickListener: RecyclerViewClickListener? = null
    }

    class LanguageViewHolder(val binding: RowMainCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun getInstance(parent: ViewGroup): LanguageViewHolder = LanguageViewHolder(
                RowMainCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        fun bind(item: Franquicia, mContext: Context) {
            binding.tvMainCategory.text = item.negocio

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder =
        LanguageViewHolder.getInstance(parent)

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(alLanguage[position], mContext)

        mClickListener = btnListener


        holder.binding.cnstMain.setOnClickListener {
            if (mClickListener != null) {
                mClickListener?.onClicked(position)
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int = alLanguage.size

}