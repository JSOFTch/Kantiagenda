package com.simplemobiletools.studentcalendarpaid.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.EditText
import com.simplemobiletools.studentcalendarpaid.R




class NewGradeDialogFragment: DialogFragment() {  // 1

    // 2
    interface NewGradeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, gradeDetails: String, gradeValue: String)
        fun onDialogNegativeClick(dialog: DialogFragment) }

    var newGradeDialogListener: NewGradeDialogListener? = null  // 3

    // 4
    companion object {
        fun newInstance(title: Int, value1: Int, selected: String?): NewGradeDialogFragment { // 1

            val newGradeDialogFragment = NewGradeDialogFragment()
            val args = Bundle()
            args.putInt("dialog_title", title)
            args.putInt("value", value1)
            args.putString("selected_item", selected) // 2
            newGradeDialogFragment.arguments = args
            return newGradeDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments.getInt("dialog_title")
        val value1 = arguments.getInt("value")
        val selectedText = arguments.getString("selected_item") // 1
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(value1)
        builder.setTitle(title)

        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_new_grade, null)

        val grade = dialogView.findViewById<EditText>(R.id.task)
        val value = dialogView.findViewById<EditText>(R.id.value)

        grade.setText(selectedText)  // 2
        value.setText(selectedText)


        builder.setView(dialogView)
                .setPositiveButton(R.string.save) { dialog, id->

                    newGradeDialogListener?.onDialogPositiveClick(this, grade.text.toString(), value.text.toString());
                }
                .setNegativeButton(android.R.string.cancel, { dialog, id ->

                    newGradeDialogListener?.onDialogNegativeClick(this)
                })

        return builder.create()
    }


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            newGradeDialogListener = activity as NewGradeDialogListener  // 9
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement NewGradeDialogListener")
        }

    }
}
