package com.garvit.taskmanager.view.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.TodoInfoBinding
import com.garvit.taskmanager.databinding.UpdateTodoBinding
import com.garvit.taskmanager.view.activity.UpdateTaskActivity
import com.garvit.taskmanager.view.model.dataModel.TodoModel
import kotlinx.android.synthetic.main.adapter_item_todo.view.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list: List<TodoModel>, val activity: Activity, val context: Context) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_todo, parent, false)
        )

    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val colors = context.resources.getIntArray(R.array.random_color)
        val randomColor = colors[Random().nextInt(colors.size)]
        holder.itemView.viewColorTag.setBackgroundColor(randomColor)
        holder.itemView.txtShowTitle.text = list[position].title
        holder.itemView.textAssign.text = list[position].assign
        val myformatTime = "h:mm a"
        val sdfTime = SimpleDateFormat(myformatTime)
        holder.itemView.txtShowTime.text = sdfTime.format(Date(list[position].time))
        val myformatDate = "EEE, d MMM yyyy"
        val sdfDate = SimpleDateFormat(myformatDate)
        holder.itemView.txtShowDate.text = sdfDate.format(Date(list[position].date))
        when (list[position].priority) {
            0 -> {
                holder.itemView.cardPriority.setCardBackgroundColor(
                    context.getResources().getColor(R.color.low)
                )
            }
            1 -> {
                holder.itemView.cardPriority.setCardBackgroundColor(
                    context.getResources().getColor(R.color.medium)
                )
            }
            2 -> {
                holder.itemView.cardPriority.setCardBackgroundColor(
                    context.getResources().getColor(R.color.high)
                )
            }
        }

        holder.itemView.linear.setOnClickListener {
            showDialogInfo(list[position])
        }
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }


    private fun showDialogInfo(todo: TodoModel) {
        val dialog = Dialog(context)
        var binding: TodoInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.todo_info,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.todo_info)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val myformatTime = "h:mm a"
        val sdfTime = SimpleDateFormat(myformatTime)
        dialog.findViewById<TextView>(R.id.txtShowTime).text = sdfTime.format(Date(todo.time))
        val myformatDate = "EEE, d MMM yyyy"
        val sdfDate = SimpleDateFormat(myformatDate)
        dialog.findViewById<TextView>(R.id.txtShowDate).text = sdfDate.format(Date(todo.date))
        dialog.findViewById<TextView>(R.id.Title).text = todo.title
        dialog.findViewById<TextView>(R.id.Desc).text = todo.description
        dialog.findViewById<TextView>(R.id.Assigned).text = todo.assign
        val yesBtn = dialog.findViewById<CardView>(R.id.btn_submit)
        val noBtn = dialog.findViewById<TextView>(R.id.btn_cancel)
        yesBtn.setOnClickListener {
           context.startActivity(Intent(context , UpdateTaskActivity::class.java).putExtra("uID", todo.id))
            activity.finish()
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }



    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}


