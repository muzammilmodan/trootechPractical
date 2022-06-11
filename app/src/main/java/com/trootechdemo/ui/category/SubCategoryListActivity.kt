package com.trootechdemo.ui.category

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trootechdemo.R
import com.trootechdemo.databinding.ActivitySubCategoryListBinding
import com.trootechdemo.listeners.RecyclerViewClickListener
import com.trootechdemo.model.SubCategoryResponseData
import com.trootechdemo.restapi.api.ApiCallback
import com.trootechdemo.stickyheaderexample.StickyHeaderItemDecoration
import com.trootechdemo.stickyheaderexample.SubCategoryAdapter
import com.trootechdemo.ui.common.BaseActivity
import com.trootechdemo.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.header_view.*
import kotlinx.android.synthetic.main.header_view.view.*

@AndroidEntryPoint
class SubCategoryListActivity : BaseActivity() {
    val mainViewModel by viewModels<CategoryViewModel>()

    lateinit var binding: ActivitySubCategoryListBinding
    lateinit var mContext: Context

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var alStickyList: ArrayList<SubCategoryResponseData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_category_list)
        mContext = this

        var selectCategoryKey = intent.getStringExtra("api_key")

        init()

        if (isOnline()) {
            callGetSubCategoryListApi(selectCategoryKey!!)
        }
    }

    fun init() {
        with(binding) {
            incldHeader.rlHeaderMain.ivBack.setOnClickListener { finish() }
            intent.getStringExtra("cat_name").also { tvTitle.text = it } //Title name set category name
        }
    }

    private fun callGetSubCategoryListApi(selectCategoryKey: String) {
        progress.show()
        mainViewModel.fetchSubCategoryListResponse(selectCategoryKey)
        mainViewModel.responseGetSubCategoryMain.observe(this) { response ->
            when (response) {
                is ApiCallback.OnSuccess -> {
                    progress.hide()

                    val sorted = response.data?.data!!.sortedBy { it.categoria?.nombremenu }
                    setSubCategoryView(sorted)

                }

                is ApiCallback.OnError -> {
                    progress.hide()
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiCallback.OnLoading -> {
                    progress.hide()
                }
            }
        }
    }

    private fun setSubCategoryView(alSorted: List<SubCategoryResponseData>) {

        var mainCategory: String = ""
        alStickyList = ArrayList()

        for (i in alSorted.indices) {
            when {
                mainCategory.isEmpty() -> { //First time main header not  available so add first header
                    mainCategory = alSorted[i].categoria.nombremenu //Main Category store
                    var categoryObject = alSorted[i]
                    categoryObject.header = true
                    alStickyList.add(categoryObject)

                    /**
                     * Header Wise sub child view data store.
                     **/
                    var subCategoryObject = alSorted[i]
                    subCategoryObject.header = false
                    alStickyList.add(subCategoryObject) //Required means same main category values add also sub category.
                }
                alSorted[i].categoria.nombremenu != mainCategory -> {
                    mainCategory = alSorted[i].categoria.nombremenu

                    var categoryObject = alSorted[i]
                    categoryObject.header = true
                    alStickyList.add(categoryObject)

                }
                alSorted[i].categoria.nombremenu == mainCategory -> {
                    mainCategory = alSorted[i].categoria.nombremenu
                    var categoryObject = alSorted[i]
                    categoryObject.header = false
                    alStickyList.add(categoryObject)
                }
            }
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = SubCategoryAdapter(alStickyList, object : RecyclerViewClickListener {
            override fun onClicked(position: Int) {
                showPercentageDialog(position)
            }
        })
        binding.rcVwSubCategoryASCL.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }//adapter values set
        binding.rcVwSubCategoryASCL.addItemDecoration(
            StickyHeaderItemDecoration(
                binding.rcVwSubCategoryASCL,
                viewAdapter as SubCategoryAdapter
            )
        )//StickyHeader add

    }

    private fun showPercentageDialog(position: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_percentage_layout)
        val tvCategoryTitleCPL = dialog.findViewById(R.id.tvCategoryTitleCPL) as TextView
        val tvConfirmCPL = dialog.findViewById(R.id.tvConfirmCPL) as TextView
        val tvCancelCPL = dialog.findViewById(R.id.tvCancelCPL) as TextView

        val tvPercentageTitleCPL = dialog.findViewById(R.id.tvPercentageTitleCPL) as TextView
        val tvPercentagePriceCPL = dialog.findViewById(R.id.tvPercentagePriceCPL) as TextView

        val ivAddValuesCPL = dialog.findViewById(R.id.ivAddValuesCPL) as ImageView
        val ivMinusValuesCPL = dialog.findViewById(R.id.ivMinusValuesCPL) as ImageView
        val edtCountCPL = dialog.findViewById(R.id.tvCountCPL) as TextView

        //Get Local data base thru first old sub category data...
        var alLocalList = SessionManager.getSubCategoryStatsData(mContext)
        if (alLocalList.isNotEmpty())
            for (mainPos in alStickyList.indices) {
                for (locPos in alLocalList.indices) {
                    if (alStickyList[mainPos].idmenu == alLocalList[locPos].idmenu) {
                        if (alLocalList[locPos].precioSugeridoValues == 0) alLocalList[locPos].precioSugeridoValues = 1
                        alStickyList[mainPos].precioSugeridoValues =
                            alLocalList[locPos].precioSugeridoValues
                    } else {
                        if (alStickyList[position].precioSugeridoValues == 0) alStickyList[position].precioSugeridoValues =
                            1
                    }
                }
            }
        else {
            if (alStickyList[position].precioSugeridoValues == 0) alStickyList[position].precioSugeridoValues =
                1
        }

        //With scope function thru set data.. object return subcategory.
        with(alStickyList[position]) {
            tvCategoryTitleCPL.text = categoria.nombremenu
            edtCountCPL.setText("${precioSugeridoValues}")
            tvPercentageTitleCPL.text = nombre
            tvPercentagePriceCPL.text =
                "${alStickyList[position].precioSugeridoValues * alStickyList[position].precioSugerido.toFloat()}"
        }

//If user confirm so add data in local preference other wise not store...
        tvConfirmCPL.setOnClickListener {
            var lastCount = edtCountCPL.text.toString()
            alStickyList[position].precioSugeridoValues = lastCount.toInt()
            SessionManager.setSubCategoryStatsData(mContext, alStickyList)
            dialog.dismiss()
        }
        tvCancelCPL.setOnClickListener { dialog.dismiss() }

        ivAddValuesCPL.setOnClickListener {
            var countGet = edtCountCPL.text.toString()
            var valuesAdd = countGet.toInt() + 1
            edtCountCPL.setText("${valuesAdd}")
            tvPercentagePriceCPL.text =
                "${valuesAdd * alStickyList[position].precioSugerido.toFloat()}"
        }
        ivMinusValuesCPL.setOnClickListener {
            var countGet = edtCountCPL.text.toString()
            if (countGet.toInt() > 1) {
                var countGet = edtCountCPL.text.toString()
                var valuesAdd = countGet.toInt() - 1
                edtCountCPL.setText("${valuesAdd}")
                tvPercentagePriceCPL.text =
                    "${valuesAdd * alStickyList[position].precioSugerido.toFloat()}"
            } else {
                Toast.makeText(
                    this,
                    "Value must be minimum of 1 number.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        dialog.show()

    }
}