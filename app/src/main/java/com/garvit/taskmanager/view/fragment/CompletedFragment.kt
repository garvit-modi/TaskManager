package com.garvit.taskmanager.view.fragment

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garvit.taskmanager.BuildConfig
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.FragmentCompletedBinding
import com.garvit.taskmanager.databinding.FragmentDashBinding
import com.garvit.taskmanager.view.activity.AddTaskActivity
import com.garvit.taskmanager.view.adapter.TodoAdapter
import com.garvit.taskmanager.view.model.dataModel.TodoModel
import com.garvit.taskmanager.view.model.db.AppDatabase
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CompletedFragment : Fragment() {

    val list = arrayListOf<TodoModel>()
    lateinit  var adapter: TodoAdapter

    val db by lazy {
        AppDatabase.getDatabase(context!!)
    }

    lateinit var binding: FragmentCompletedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_completed, container, false)
        adapter = TodoAdapter(list, activity!!, context!!)

        binding.todoRecycle.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter =  this@CompletedFragment.adapter
        }
        initSwipe()

        db.todoDao().getFinishedTask().observe(this,  {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })
        binding.DownloadPdf.setOnClickListener {
            createPDf()
        }

        return binding.root
    }

    fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.getItemId(position))
                    }
                }
                //                else if (direction == ItemTouchHelper.RIGHT) {
//                    GlobalScope.launch(Dispatchers.IO) {
//                        db.todoDao().finishTask(adapter.getItemId(position))
//                    }
//                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap

                    if (dX > 0) {

                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_check_white_png)

                        paint.color = Color.parseColor("#388E3C")

                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )


                    }
                    else {
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)

                        paint.color = Color.parseColor("#D32F2F")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX


                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }


        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.todoRecycle)
    }

    fun createPDf() {


        var pdfGenerator = PdfGenerator.getBuilder(BuildConfig.APPLICATION_ID)
            .setContext(activity)
            .fromViewSource()
            .fromView(activity!!.findViewById(R.id.todoRecycle))
//            .fromViewIDSource()
//            .fromViewID(this,R.id.txt1)

            /* "fromLayoutXML()" takes array of layout resources.
			 * You can also invoke "fromLayoutXMLList()" method here which takes list of layout resources instead of array. */
            .setPageSize(PdfGenerator.PageSize.A4) /* It takes default page size like A4,A5,WRAP_CONTENT.*/
            .setFileName("CompletedTask"+System.currentTimeMillis().toString()) /* It is file name */
            .setFolderName("FolderTask") /* It is folder name. If you set the folder name like this pattern (FolderA/FolderB/FolderC), then
			 * FolderA creates first.Then FolderB inside FolderB and also FolderC inside the FolderB and finally
			 * the pdf file named "Test-PDF.pdf" will be store inside the FolderB. */
            .openPDFafterGeneration(true) /* It true then the generated pdf will be shown after generated. */
            .build(object : PdfGeneratorListener() {
                override fun onFailure(failureResponse: FailureResponse) {
                    super.onFailure(failureResponse)
                    /* If pdf is not generated by an error then you will findout the reason behind it
				 * from this FailureResponse. */
                }

                override fun onStartPDFGeneration() {
                    /*When PDF generation begins to start*/
                }

                override fun onFinishPDFGeneration() {
                    /*When PDF generation is finished*/
                }

                override fun showLog(log: String) {
                    super.showLog(log)

                }

                override fun onSuccess(response: SuccessResponse) {
                    super.onSuccess(response)

                }
            })

    }


}