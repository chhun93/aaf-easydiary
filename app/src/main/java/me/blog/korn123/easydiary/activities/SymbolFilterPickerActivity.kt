package me.blog.korn123.easydiary.activities

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_symbol_filter_picker.*
import me.blog.korn123.easydiary.R
import me.blog.korn123.easydiary.adapters.SymbolFilterAdapter
import me.blog.korn123.easydiary.adapters.SymbolPagerAdapter
import me.blog.korn123.easydiary.extensions.addCategory
import me.blog.korn123.easydiary.extensions.config
import me.blog.korn123.easydiary.extensions.showAlertDialog
import java.util.*

/**
 * Created by CHO HANJOONG on 2020-11-23.
 */

class SymbolFilterPickerActivity : EasyDiaryActivity() {

    /***************************************************************************************************
     *   global properties
     *
     ***************************************************************************************************/
    private lateinit var mSymbolFilterAdapter: SymbolFilterAdapter
    private var mSymbolFilterList: ArrayList<SymbolFilterAdapter.SymbolFilter> = arrayListOf()


    /***************************************************************************************************
     *   override functions
     *
     ***************************************************************************************************/
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_symbol_filter_picker)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = "Symbol Picker"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_cross)
        }

        mSymbolFilterAdapter = SymbolFilterAdapter(
            this,
            mSymbolFilterList,
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val filteredList =  config.selectedSymbols.split(",")
                        .filter { sequence ->  sequence.toInt() != mSymbolFilterList[position].sequence}
                if (filteredList.isNotEmpty()) {
                    config.selectedSymbols = filteredList.joinToString(",")
                    updateSymbolFilter()
                } else {
                    showAlertDialog(getString(R.string.symbol_filter_picker_remove_guide_message), null)
                }
            }
        )

        recyclerView?.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@SymbolFilterPickerActivity, 4)
            addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.card_layout_padding)))
            adapter = mSymbolFilterAdapter
        }
        updateSymbolFilter()


        val itemList = arrayListOf<Array<String>>()
        val categoryList = arrayListOf<String>()
        addCategory(itemList, categoryList, "weather_item_array", getString(R.string.category_weather))
        addCategory(itemList, categoryList, "emotion_item_array", getString(R.string.category_emotion))
        addCategory(itemList, categoryList, "daily_item_array", getString(R.string.category_daily))
        addCategory(itemList, categoryList, "tasks_item_array", getString(R.string.category_tasks))
        addCategory(itemList, categoryList, "food_item_array", getString(R.string.category_food))
        addCategory(itemList, categoryList, "leisure_item_array", getString(R.string.category_leisure))
        addCategory(itemList, categoryList, "landscape_item_array", getString(R.string.category_landscape))
        addCategory(itemList, categoryList, "symbol_item_array", getString(R.string.category_symbol))
        addCategory(itemList, categoryList, "flag_item_array", getString(R.string.category_flag))

        val symbolPagerAdapter = SymbolPagerAdapter(this, itemList, categoryList) { symbolSequence ->
            if (symbolSequence > 0) {
                config.selectedSymbols.split(",").find { it.toInt() == symbolSequence }?.let {
                    showAlertDialog("The selected symbol already exists in the filter list.", null)
                } ?: run {
                    config.selectedSymbols = config.selectedSymbols + "," + symbolSequence
                    updateSymbolFilter(true)
                }
            }
        }
        viewpager.adapter = symbolPagerAdapter
        sliding_tabs.setViewPager(viewpager)
    }

    private fun updateSymbolFilter(scrollToBottom: Boolean = false) {
        mSymbolFilterList.clear()
        config.selectedSymbols.split(",").map { sequence ->
            mSymbolFilterList.add(SymbolFilterAdapter.SymbolFilter(sequence.toInt()))
        }
        mSymbolFilterAdapter.notifyDataSetChanged()
        if (scrollToBottom) recyclerView.smoothScrollToPosition(mSymbolFilterList.size.minus(1))
    }

    /***************************************************************************************************
     *   etc functions
     *
     ***************************************************************************************************/
    class SpacesItemDecoration(private val space: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
//            val position = parent.getChildAdapterPosition(view)
//            when (position % 4) {
//                3  -> {
//                    outRect.right = 0
//                }
//                else -> outRect.right = 50
//            }
//            when (position < 4) {
//                true -> outRect.top = 0
//                false -> outRect.top = 50
//            }
            outRect.top = space
            outRect.bottom = space
            outRect.left = space
            outRect.right = space
        }
    }
}


