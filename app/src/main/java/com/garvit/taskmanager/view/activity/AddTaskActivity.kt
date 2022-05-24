package com.garvit.taskmanager.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.ActivityAddTaskBinding
import com.garvit.taskmanager.view.model.dataModel.TodoModel
import com.garvit.taskmanager.view.model.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class AddTaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityAddTaskBinding
    lateinit var myCalendar: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalDate = 0L
    var finalTime = 0L
    var priority = 0
    private val labels = arrayListOf("Low", "Medium", "High")
    var flagDate = false

    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.red)
        }
        binding.dateEdt.setOnClickListener(this)
        binding.timeEdt.setOnClickListener(this)
        binding.saveBtn.setOnClickListener(this)
        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)
//        labels.sort()

        binding.spinnerPriority.adapter = adapter


        binding.spinnerPriority.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                //first,  we have to retrieve the item position as a string
                // then, we can change string value into integer
                val item_position = position.toString()
                priority = Integer.valueOf(item_position)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dateEdt -> {
                setListener()
            }
            R.id.timeEdt -> {
                setTimeListener()
            }
            R.id.saveBtn -> {
                if (binding.titleInpLay.editText?.text.toString().trim()
                        .isEmpty() && binding.taskInpLay.editText?.text.toString().trim()
                        .isEmpty() && binding.AssignTo.text.toString().trim().isEmpty()
                ) {
                    Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!flagDate) {
                    Toast.makeText(this, "please select date ", Toast.LENGTH_SHORT).show()
                    return
                }
                val myformatDate = "EEE, d MMM yyyy"
                val sdfDate = SimpleDateFormat(myformatDate)
                val currentDate = sdfDate.format(Date(System.currentTimeMillis()))
                val date = sdfDate.format(Date(finalDate))
                if (currentDate == date &&(myCalendar.time.time <= System.currentTimeMillis()) ) {
                    Toast.makeText(this, "please select valid date and time", Toast.LENGTH_SHORT).show()
                    return
                }
                saveTodo()
            }
        }

    }

    private fun saveTodo() {
        val title = binding.titleInpLay.editText?.text.toString().trim()
        val description = binding.taskInpLay.editText?.text.toString().trim()
        val assign = binding.AssignTo.text.toString().trim()

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        finalDate,
                        finalTime,
                        priority,
                        assign
                    )
                )
            }
            finish()
        }

    }

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener =
            TimePickerDialog.OnTimeSetListener() { _: TimePicker, hourOfDay: Int, min: Int ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, min)
                updateTime()
            }

        val timePickerDialog = TimePickerDialog(
            this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE), true
        )
        timePickerDialog.show()
    }

    private fun updateTime() {
        //Mon, 5 Jan 2020
        val myformat = "h:mm a"
        val sdf = SimpleDateFormat(myformat)
        finalTime = myCalendar.time.time
        binding.timeEdt.setText(sdf.format(myCalendar.time))

    }

    private fun setListener() {
        myCalendar = Calendar.getInstance()
        flagDate = true
        dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()

            }

        val datePickerDialog = DatePickerDialog(
            this, dateSetListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        //Mon, 5 Jan 2020
        val myformat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myformat)
        finalDate = myCalendar.time.time
        binding.dateEdt.setText(sdf.format(myCalendar.time))

        binding.timeInptLay.visibility = View.VISIBLE

    }

}