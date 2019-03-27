package com.simplemobiletools.studentcalendarpaid.activities

import androidx.room.*


@Dao
interface TaskDAO {

    @Query("SELECT * FROM " + TodoListDBContract.TodoListItem.TABLE_NAME)
    fun retrieveTaskList(): List<Task>

    @Insert
    fun addNewTask(task: Task): Long

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

}
