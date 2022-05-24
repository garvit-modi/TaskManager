package com.garvit.taskmanager.view.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.FragmentChartBinding
import com.garvit.taskmanager.view.model.db.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*


class ChartFragment : Fragment() {
    var today = 0
    var tommorow = 0
    var nextMonth = 0
    var nextWeak = 0
    val dateList = arrayListOf<Long>()
    val db by lazy {
        AppDatabase.getDatabase(context!!)
    }

    var barDataSet1: BarDataSet? = null
    var barDataSet2: BarDataSet? = null
    var barDataSet3: BarDataSet? = null
    var barDataSet4: BarDataSet? = null
    lateinit var barEntries: ArrayList<BarEntry>
    var days = arrayOf("Task")

    lateinit var binding: FragmentChartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_chart, container, false)
        db.todoDao().getAllDate().observe(this, {
            dateList.addAll(it)
            dateForGraph(dateList)
        })
        return binding.root
    }

    private fun initializeBarChart() {
        val barChart = binding.idBarChart
        // creating a new bar data set.
        // creating a new bar data set.
        barDataSet1 = BarDataSet(getBarEntriesOne(), "Today")
        barDataSet1!!.color = activity!!.getResources().getColor(R.color.purple_200)
        barDataSet2 = BarDataSet(getBarEntriesTwo(), "Tomorrow")
        barDataSet2!!.setColor(Color.BLUE)
        barDataSet3 = BarDataSet(getBarEntriesThree(), "Next Week")
        barDataSet3!!.setColor(Color.RED)
        barDataSet4 = BarDataSet(getBarEntriesFour(), "Upcoming")
        barDataSet4!!.setColor(Color.BLACK)

        // below line is to add bar data set to our bar data.

        // below line is to add bar data set to our bar data.
        val data = BarData(barDataSet1, barDataSet2,barDataSet3,barDataSet4)

        // after adding data to our bar data we
        // are setting that data to our bar chart.

        // after adding data to our bar data we
        // are setting that data to our bar chart.
        barChart.data = data

        // below line is to remove description
        // label of our bar chart.

        // below line is to remove description
        // label of our bar chart.
        barChart.description.isEnabled = false

        // below line is to get x axis
        // of our bar chart.

        // below line is to get x axis
        // of our bar chart.
        val xAxis = barChart.xAxis

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.
        xAxis.valueFormatter = IndexAxisValueFormatter(days)

        // below line is to set center axis
        // labels to our bar chart.

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true)

        // below line is to set position
        // to our x-axis to bottom.

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // below line is to set granularity
        // to our x axis labels.

        // below line is to set granularity
        // to our x axis labels.
        xAxis.granularity = 1f

        // below line is to enable
        // granularity to our x axis.

        // below line is to enable
        // granularity to our x axis.
        xAxis.isGranularityEnabled = true

        // below line is to make our
        // bar chart as draggable.

        // below line is to make our
        // bar chart as draggable.
        barChart.isDragEnabled = true

        // below line is to make visible
        // range for our bar chart.

        // below line is to make visible
        // range for our bar chart.
        barChart.setVisibleXRangeMaximum(3f)

        // below line is to add bar
        // space to our chart.

        // below line is to add bar
        // space to our chart.
        val barSpace = 0.1f

        // below line is use to add group
        // spacing to our bar chart.

        // below line is use to add group
        // spacing to our bar chart.
        val groupSpace = 0.5f

        // we are setting width of
        // bar in below line.

        // we are setting width of
        // bar in below line.
        data.barWidth = 0.15f

        // below line is to set minimum
        // axis to our chart.

        // below line is to set minimum
        // axis to our chart.
        barChart.xAxis.axisMinimum = 0f

        // below line is to
        // animate our chart.

        // below line is to
        // animate our chart.
        barChart.animate()

        // below line is to group bars
        // and add spacing to it.

        // below line is to group bars
        // and add spacing to it.
        barChart.groupBars(0f, groupSpace, barSpace)

        // below line is to invalidate
        // our bar chart.

        // below line is to invalidate
        // our bar chart.
        barChart.invalidate()


    }

    private fun getBarEntriesOne(): ArrayList<BarEntry> {
        barEntries = ArrayList()
        barEntries.add(BarEntry(1f, today.toFloat()))


        return barEntries
    }

    private fun getBarEntriesTwo(): ArrayList<BarEntry> {
        barEntries = ArrayList()
        barEntries.add(BarEntry(1f, tommorow.toFloat()))


        return barEntries
    }
    private fun getBarEntriesThree(): ArrayList<BarEntry> {
        barEntries = ArrayList()
        barEntries.add(BarEntry(1f, nextWeak.toFloat()))


        return barEntries
    }
    private fun getBarEntriesFour(): ArrayList<BarEntry> {
        barEntries = ArrayList()
        barEntries.add(BarEntry(1f, nextMonth.toFloat()))


        return barEntries
    }


    fun dateForGraph(list: ArrayList<Long>) {
        val myformat = "yyyyMMdd"
        val sdf = SimpleDateFormat(myformat)
        val todayCal = Calendar.getInstance()
        val tommCal = Calendar.getInstance()
        val nextMonthCal = Calendar.getInstance()
        tommCal.add(Calendar.DATE, 1)
        nextMonthCal.add(Calendar.MONTH, 1)
        for (date in list) {

            if (sdf.format(todayCal.time) >= sdf.format(date))
                today++
            else if (sdf.format(tommCal.time) >= sdf.format(date))
                tommorow++
            else if (sdf.format(nextMonthCal.time) >= sdf.format(date))
                nextWeak++
            else
                nextMonth++
        }
        initializeBarChart()
    }



}