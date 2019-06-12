package me.blog.korn123.easydiary.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_barchart.*
import me.blog.korn123.commons.utils.EasyDiaryUtils
import me.blog.korn123.easydiary.R
import me.blog.korn123.easydiary.chart.IValueFormatterExt
import me.blog.korn123.easydiary.chart.MyAxisValueFormatter
import me.blog.korn123.easydiary.chart.XYMarkerView
import me.blog.korn123.easydiary.helper.EasyDiaryDbHelper
import java.util.*

class BarChartFragmentT2 : Fragment() {
    val mSequences = arrayListOf<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false)

//        barChart.setDrawGridBackground(true)
        // mChart.setDrawYLabels(false);
        //barChart.zoom(3.5F, 0F, 0F, 0F)

        val xAxisFormatter = AxisValueFormatter(context, barChart)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.typeface = mTfLight
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.labelCount = 7
        xAxis.valueFormatter = xAxisFormatter

        val custom = MyAxisValueFormatter(context)

        val leftAxis = barChart.axisLeft
//        leftAxis.typeface = mTfLight
        leftAxis.setLabelCount(8, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false)
//        rightAxis.typeface = mTfLight
        rightAxis.setLabelCount(8, false)
        rightAxis.valueFormatter = custom
        rightAxis.spaceTop = 15f
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.form = Legend.LegendForm.SQUARE
        legend.formSize = 9f
        legend.textSize = 11f
        legend.xEntrySpace = 4f

        val mv = XYMarkerView(context!!, xAxisFormatter)
        mv.chartView = barChart // For bounds control
        barChart.marker = mv // Set the marker to the chart

        setData(6, 20f)
        barChart.animateXY(1000, 2500)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barchart, container, false)
    }

    private fun setData(count: Int, range: Float) {
        val listDiary = EasyDiaryDbHelper.readDiary(null)
        val map = hashMapOf<Int, Int>()
        listDiary.map { diaryDto ->
            val targetColumn = diaryDto.weather
            if (targetColumn != 0) {
                if (map[targetColumn] == null) {
                    map.put(targetColumn, 1)
                } else {
                    map.put(targetColumn, (map[targetColumn] ?: 0) + 1)
                }
            }
        }

        var sortedMap = map.toList().sortedByDescending { (_, value) -> value }.toMap()   
        
        
        val barEntries = ArrayList<BarEntry>()
        var index = 1.0F
        sortedMap.forEach { (key, value) ->
            val drawable: Drawable? = when (EasyDiaryUtils.sequenceToSymbolResourceId(key) > 0) {
                true -> ContextCompat.getDrawable(context!!, EasyDiaryUtils.sequenceToSymbolResourceId(key))
                false -> null
            }
            mSequences.add(key)
            barEntries.add(BarEntry(index++, value.toFloat(), drawable))
        }
        
        val barDataSet: BarDataSet
        barDataSet = BarDataSet(barEntries, "다이어리 심볼기준 통계")
        val iValueFormatter = IValueFormatterExt(context)
        barDataSet.valueFormatter = iValueFormatter
        val colors = intArrayOf(
                Color.rgb(152, 189, 248)/*, Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53), Color.rgb(115, 130, 153)*/)
        barDataSet.setColors(*colors)
        barDataSet.setDrawIcons(true)
        barDataSet.setDrawValues(false)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)

        val barData = BarData(dataSets)
        barData.setValueTextSize(10f)
//        barData.setValueTypeface(mTfLight)
        barData.barWidth = 0.9f
        barChart.zoom((map.size / 6.0F), 0F, 0F, 0F)
        barChart.data = barData
    }

    inner class AxisValueFormatter(private var context: Context?, private val chart: BarLineChartBase<*>) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            val symbolMap = EasyDiaryUtils.getDiarySymbolMap(context!!)
            return symbolMap[mSequences[value.toInt() - 1]] ?: "None"
        }
    }
}