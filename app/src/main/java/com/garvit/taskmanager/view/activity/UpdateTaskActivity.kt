package com.garvit.taskmanager.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.ActivityUpdateTaskBinding
import com.garvit.taskmanager.view.model.dataModel.TodoModel
import com.garvit.taskmanager.view.model.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UpdateTaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityUpdateTaskBinding
    lateinit var myCalendar: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalDate = 0L
    var finalTime = 0L
    var priority = 0L
    private val labels = arrayListOf("Low", "Medium", "High")
    var uId: Long = 0L

    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_task)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.red)
        }
        GlobalScope.launch(Dispatchers.IO) {
           binding.data =  db.todoDao().getOneTask(uId)
        }
        uId = intent.getLongExtra("uID", 0L)
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


        binding.spinnerPriority.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                priority = position.toLong()

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
                if (binding.titleInpLay.editText?.text.toString().trim().isEmpty()
                    && binding.taskInpLay.editText?.text.toString().trim().isEmpty()
                    && binding.AssignTo.text.toString().trim().isEmpty()
                ) {
                    Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
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
                return@withContext db.todoDao().updateTask(
                        uId,
                        title,
                        description,
                        finalDate,
                        finalTime,
                        priority,
                        assign
                )
            }
           startActivity(Intent(applicationContext , HomeActivity::class.java))
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
            myCalendar.get(Calendar.MINUTE), false
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