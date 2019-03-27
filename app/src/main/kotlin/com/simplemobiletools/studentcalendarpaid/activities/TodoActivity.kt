package com.simplemobiletools.studentcalendarpaid.activities

import android.app.DialogFragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.material.snackbar.Snackbar
import com.simplemobiletools.studentcalendarpaid.R
import com.simplemobiletools.studentcalendarpaid.activities.TodoListDBContract.DATABASE_NAME
import kotlinx.android.synthetic.main.activity_todo.*
import java.util.*




import android.os.AsyncTask
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class TodoActivity : AppCompatActivity(), NewTaskDialogFragment.NewTaskDialogListener, NavigationView.OnNavigationItemSelectedListener {
    var prefs: SharedPreferences? = null

    private var listView: ListView? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private lateinit var userName: TextView
    private var userImage: ImageView? = null
    private var userEmail: TextView? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    private var listAdapter: TaskListAdapter? = null

    private var todoListItems = ArrayList<Task>()

    private var showMenuItems = false

    private var selectedItem = -1

    private var database: AppDatabase? = null

    private var dbHelper: TodoListDBHelper = TodoListDBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        prefs = getSharedPreferences("com.simplemobiletools.studentcalendarpro", MODE_PRIVATE);






        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = "My Todo's"
        setSupportActionBar(toolbar)



        mAuth = FirebaseAuth.getInstance()
        val user = mAuth!!.currentUser


        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
/*
                val personName = user!!.displayName
                val personPhotoUrl = Objects.requireNonNull<Uri>(user.photoUrl).toString()
                val email = user.email

                userEmail = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userEmail)
                userName = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userName)
                userImage = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userImage)




                userName.text = personName
                userEmail!!.text = email

                Glide.with(applicationContext).load(personPhotoUrl)
                        .thumbnail(0.3f)
                        .apply(RequestOptions.circleCropTransform())

                        .into(userImage!!)*/
            }

            if (firebaseAuth.currentUser == null) {
               // Toast.makeText(this@TodoActivity, "You're logged out", Toast.LENGTH_SHORT).show()

            }
        }



        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)
        navigationView!!.bringToFront()

        navigationView!!.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()



        listView = this.findViewById(R.id.list_view)

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(object : Migration(TodoListDBContract.DATABASE_VERSION - 1, TodoListDBContract.DATABASE_VERSION) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                    }
                }).build()

        populateListView()

        fab.setOnClickListener { showNewTaskUI() }

        listView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> showUpdateTaskUI(position) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.to_do_list_menu, menu)
        val editItem = menu.findItem(R.id.edit_item)
        val deleteItem = menu.findItem(R.id.delete_item)
        val completeItem = menu.findItem(R.id.mark_as_done_item)
        val reminderItem = menu.findItem(R.id.reminder_item)

        if (showMenuItems) {
            editItem.isVisible = true
            deleteItem.isVisible = true
            completeItem.isVisible = true

            reminderItem.isVisible = true
        }

        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@TodoActivity, MainActivity::class.java)
                startActivity(h)
            }
            R.id.nav_grades -> {
                val i = Intent(this@TodoActivity, AddGradeActivity::class.java)
                startActivity(i)
            }


            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@TodoActivity, ViewListContents::class.java)
                startActivity(j)
            }
           /* com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@TodoActivity, AccountActivity::class.java)
                startActivity(l)

            }*/

            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {
                val k = Intent(this@TodoActivity, InfoActivity::class.java)
                startActivity(k)


            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@TodoActivity, TodoActivity::class.java)
                startActivity(m)

            }


        }


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (-1 != selectedItem) {
            if (R.id.edit_item == item?.itemId) {

                val updateFragment = NewTaskDialogFragment.newInstance(R.string.update_task_dialog_title, todoListItems[selectedItem].taskDetails)

                updateFragment.show(fragmentManager, "updatetask")

            } else if (R.id.delete_item == item?.itemId) {
                val selectedTask = todoListItems[selectedItem]
                DeleteTaskAsyncTask(database, selectedTask).execute()
//                dbHelper.deleteTask(selectedTask)

                todoListItems.removeAt(selectedItem)
                listAdapter?.notifyDataSetChanged()
                selectedItem = -1
                Snackbar.make(fab, "Task deleted successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show()

            } else if (R.id.mark_as_done_item == item?.itemId) {
                todoListItems[selectedItem].completed = true

                UpdateTaskAsyncTask(database, todoListItems[selectedItem]).execute()
                listAdapter?.notifyDataSetChanged()
                selectedItem = -1

                Snackbar.make(fab, "Task has been marked as completed", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }

            else if (R.id.reminder_item == item?.itemId) {
                TimePickerFragment.newInstance("Time picker argument")
                        .show(fragmentManager, "TodoActivtiy")
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun showNewTaskUI() {

        val newFragment = NewTaskDialogFragment.newInstance(R.string.add_new_task_dialog_title, null)
        newFragment.show(fragmentManager, "newtask")
    }

    private fun showUpdateTaskUI(selected: Int) {

        selectedItem = selected
        showMenuItems = true
        invalidateOptionsMenu()
    }

    private fun populateListView() {
        todoListItems = RetrieveTasksAsyncTask(database).execute().get() as ArrayList<Task>
        listAdapter = TaskListAdapter(this, todoListItems)
        listView?.adapter = listAdapter

    }

    override fun onDialogPositiveClick(dialog: DialogFragment, taskDetails: String) {

        if ("newtask" == dialog.tag) {

            var addNewTask = Task(taskDetails, "")

            addNewTask.taskId = AddTaskAsyncTask(database, addNewTask).execute().get()
            todoListItems.add(addNewTask)
            listAdapter?.notifyDataSetChanged()


            Snackbar.make(fab, "Task Added Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show()

        } else if ("updatetask" == dialog.tag) {
            todoListItems[selectedItem].taskDetails = taskDetails
//            dbHelper.updateTask(todoListItems[selectedItem])
            UpdateTaskAsyncTask(database, todoListItems[selectedItem]).execute()

            listAdapter?.notifyDataSetChanged()

            selectedItem = -1

            Snackbar.make(fab, "Task Updated Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
    }

    override fun onDestroy() {
//        dbHelper.close()
        super.onDestroy()
    }


    private class RetrieveTasksAsyncTask(private val database: AppDatabase?) : AsyncTask<Void, Void, List<Task>>() {

        override fun doInBackground(vararg params: Void): List<Task>? {
            return database?.taskDao()?.retrieveTaskList()
        }
    }

    private class AddTaskAsyncTask(private val database: AppDatabase?, private val newTask: Task) : AsyncTask<Void, Void, Long>() {

        override fun doInBackground(vararg params: Void): Long? {
            return database?.taskDao()?.addNewTask(newTask)
        }
    }

    private class UpdateTaskAsyncTask(private val database: AppDatabase?, private val selectedTask: Task) : AsyncTask<Void, Void, Unit>() {

        override fun doInBackground(vararg params: Void): Unit? {
            return database?.taskDao()?.updateTask(selectedTask)
        }
    }

    private class DeleteTaskAsyncTask(private val database: AppDatabase?, private val selectedTask: Task) : AsyncTask<Void, Void, Unit>() {

        override fun doInBackground(vararg params: Void): Unit? {
            return database?.taskDao()?.deleteTask(selectedTask)
        }
    }
    override fun onResume() {
        super.onResume()

        if (prefs!!.getBoolean("firstrun", true)) {
            val h = Intent(this@TodoActivity, OnBoardingActivity::class.java)
            startActivity(h)
            prefs!!.edit().putBoolean("firstrun", false).apply()
        }
    }

    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener)

    }

}
