package com.garvit.taskmanager.view.model.dataModel

import android.renderscript.RenderScript
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoModel(
    var title:String,
    var description:String,
    var date:Long,
    var time:Long,
    var priority:Int,
    var assign: String,
    var isFinished : Int = 0,
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0L
)