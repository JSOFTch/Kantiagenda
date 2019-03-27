package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.simplemobiletools.studentcalendarpaid.R

class GradeListAdapter(val context: Context, private val gradeList: ArrayList<Grade>): BaseAdapter() {

    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var view = convertView
        var viewHolder: ViewHolder?
        if (view == null) {
            view =  inflater.inflate(R.layout.item_task, parent, false)
            viewHolder = ViewHolder()

            viewHolder.taskDescriptionTextView = view.findViewById(R.id.task_item_description)
            viewHolder.deadlineTextView = view.findViewById(R.id.task_item_deadline)
            //viewHolder.note = view.findViewById(R.id.note)
            viewHolder.statusTextView = view.findViewById(R.id.task_item_status)

            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder?
        }

        val taskDescriptionTextView = viewHolder?.taskDescriptionTextView
        val deadlineTextView = viewHolder?.deadlineTextView
        val note = viewHolder?.note
        val statusTextView = viewHolder?.statusTextView

        val grade = getItem(position) as Grade


        taskDescriptionTextView?.text = grade.gradeDetails
        note?.text = grade.gradeValue
        taskDescriptionTextView?.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.black, null))
        note?.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.black, null))


        deadlineTextView?.text = grade.gradeDeadline
        if (null != grade.completed && true == grade.completed) {
            statusTextView?.text = (context.getString(R.string.complete))
            statusTextView?.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.holo_green_light, null))

        } else {
            statusTextView?.text = (context.getString(R.string.incomplete))
            statusTextView?.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.holo_red_light, null))
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return gradeList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return gradeList.size
    }

    private class ViewHolder {
        var taskDescriptionTextView: TextView? = null
        var deadlineTextView: TextView? = null
        var note: TextView? = null
        var statusTextView: TextView? = null
    }
}
