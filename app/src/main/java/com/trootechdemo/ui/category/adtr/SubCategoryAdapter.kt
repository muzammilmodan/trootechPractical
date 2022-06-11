package com.trootechdemo.stickyheaderexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.trootechdemo.R
import com.trootechdemo.listeners.RecyclerViewClickListener
import com.trootechdemo.model.SubCategoryResponseData
import kotlinx.android.synthetic.main.row_sticky_header.view.*
import kotlinx.android.synthetic.main.row_sub_category_item.view.*

const val TYPE_HEADER = 0
const val TYPE_ITEM = 1

class SubCategoryAdapter(val alSubCategory: List<SubCategoryResponseData>, var btnListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyHeaderItemDecoration.StickyHeaderInterface {

    companion object {
        var mClickListener: RecyclerViewClickListener? = null
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return alSubCategory[itemPosition].header
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        ((header as ConstraintLayout).getChildAt(0) as TextView).text =
            alSubCategory[headerPosition].categoria.nombremenu
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.row_sticky_header
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        var position = itemPosition
        do {
            if (this.isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            ViewHolderHeader(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_sticky_header, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_sub_category_item, parent, false)
            )
        }
    }

    override fun getItemCount() = alSubCategory.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mClickListener = btnListener
        if (holder is ViewHolder) {
            holder.tvSubCategoryName.text = alSubCategory[position].nombre //Sub category list

            holder.ivAddValuesRSCI.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener?.onClicked(position)
                    notifyDataSetChanged()
                }
            }
        } else if (holder is ViewHolderHeader) {
            holder.headerView.text = alSubCategory[position].categoria.nombremenu //Main category Header list data
        }



    }


    override fun getItemViewType(position: Int): Int {
        return if (alSubCategory[position].header) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSubCategoryName = view.tvSubCategoryName as TextView
        val ivAddValuesRSCI=view.ivAddValuesRSCI as ImageView
    }

    class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
        val headerView = view.tvHeaderView as TextView
    }

}