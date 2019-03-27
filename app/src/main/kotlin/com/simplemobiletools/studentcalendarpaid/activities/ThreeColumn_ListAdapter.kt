package com.simplemobiletools.studentcalendarpaid.activities


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


import com.simplemobiletools.studentcalendarpaid.R

import java.util.ArrayList

class ThreeColumn_ListAdapter(context: Context, private val mViewResourceId: Int, private val users: ArrayList<User>) : ArrayAdapter<User>(context, mViewResourceId, users) {

    private val mInflater: LayoutInflater


    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = mInflater.inflate(mViewResourceId, null)

        val user = users[position]

        if (user != null) {
            val firstName = convertView!!.findViewById<View>(R.id.textFirstName) as TextView
            val lastName = convertView.findViewById<View>(R.id.textLastName) as TextView
            val favFood = convertView.findViewById<View>(R.id.textFavFood) as TextView
            if (firstName != null) {
                firstName.text = user.firstName
            }
            if (lastName != null) {
                lastName.text = user.lastName
            }
            if (favFood != null) {
                favFood.text = user.favFood
            }
        }

        return convertView
    }


}
