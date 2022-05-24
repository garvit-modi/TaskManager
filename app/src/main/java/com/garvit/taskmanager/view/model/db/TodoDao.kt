package com.garvit.taskmanager.view.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.garvit.taskmanager.view.model.dataModel.TodoModel

@Dao
interface TodoDao {

    @Insert()
    suspend fun insertTask(todoModel: TodoModel): Long

    @Query("Select * from TodoModel where isFinished == 0")
    fun getTask(): LiveData<List<TodoModel>>

    @Query("Select * from TodoModel where isFinished == 1")
    fun getFinishedTask(): LiveData<List<TodoModel>>

    @Query("Select * from TodoModel where id == :uid")
    fun getOneTask(uid: Long): TodoModel

    @Query("Select * from TodoModel")
    fun getAllTask(): LiveData<List<TodoModel>>


    @Query("Select Count(id) from TodoModel where date <= :date and isFinished == 0 ")
    fun getByDate(date: Long): LiveData<Int>

 @Query("Select date from TodoModel where isFinished == 0 ")
    fun getAllDate(): LiveData<List<Long>>

    @Query("Update TodoModel Set isFinished = 1 where id=:uid")
    fun finishTask(uid: Long)

    @Query("Update TodoModel Set title = :title , description = :description, date  = :date, time = :time, priority=:priority, assign=:assign, isFinished = 0  where id=:uid")
    fun updateTask(
        uid: Long,
        title: String,
        description: String,
        date: Long,
        time: Long,
        priority: Long,
        assign: String,
    )

    @Query("Delete from TodoModel where id=:uid")
    fun deleteTask(uid: Long)
}