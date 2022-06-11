package com.trootechdemo.ui.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.trootechdemo.R
import com.trootechdemo.databinding.ActivityCategoryListBinding
import com.trootechdemo.restapi.api.ApiCallback
import com.trootechdemo.ui.common.BaseActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.trootechdemo.listeners.RecyclerViewClickListener
import com.trootechdemo.model.Categoria
import com.trootechdemo.model.Franquicia
import com.trootechdemo.model.SubCategoryResponse
import com.trootechdemo.ui.category.CategoryViewModel
import com.trootechdemo.ui.category.adtr.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.header_view.view.*

/**
 * As per Hilt required set Entry point, with-out entry point Hilt not working this screen and also application crash.
 **/
@AndroidEntryPoint
class CategoryListActivity : BaseActivity() {

    //View model object create
    val mainViewModel by viewModels<CategoryViewModel>()

    lateinit var binding: ActivityCategoryListBinding
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_list)
        mContext = this


        init()
        if (isOnline()) { //First check internet connection available or not
            callGetCategoryListApi()
        }
    }

    private fun init(){
        with(binding){
            incldHeader.rlHeaderMain.ivBack.visibility= View.GONE
            incldHeader.rlHeaderMain.tvTitle.text="Category-"
        }
    }

    lateinit var  alFranquicias: ArrayList<Franquicia>
    private fun callGetCategoryListApi() {
        progress.show()
        mainViewModel.fetchCategoryListResponse()
        /**
         * Using Observer thru response handle..
         * **/
        mainViewModel.responseGetCategoryMain.observe(this) { response ->
            when (response) {
                is ApiCallback.OnSuccess -> {
                    progress.hide()
                    alFranquicias= ArrayList()
                    alFranquicias.addAll(response.data?.franquicias!!)
                    setCategoryView()
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

    private fun setCategoryView() {
        var adapterPeople = CategoryAdapter(mContext, alFranquicias, object :
            RecyclerViewClickListener {
            override fun onClicked(position: Int) {
                var intent= Intent(mContext,SubCategoryListActivity::class.java)
                intent.putExtra("api_key",alFranquicias[position].APIKEY)
                intent.putExtra("cat_name",alFranquicias[position].negocio)
                startActivity(intent)
            }
        })
        val linearLayoutManager = LinearLayoutManager(mContext)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcVwCategoryACL.layoutManager = linearLayoutManager
        adapterPeople.also { binding.rcVwCategoryACL.adapter = it }
        adapterPeople.notifyDataSetChanged()
    }


}