package com.elanciers.bringszo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.R

import java.util.ArrayList


class SpinAdapter(internal var context: Context, internal var resource: Int, internal var items: ArrayList<SpinnerPojo>) : BaseAdapter() {
    internal var mInflater: LayoutInflater

    init {

        mInflater = LayoutInflater.from(context)
    }


    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder = ViewHolder()
        var alertView: LinearLayout? = null
        if (convertView == null) {
            convertView = mInflater.inflate(resource, alertView, true)
            convertView!!.tag = holder
            alertView = convertView as LinearLayout?
        } else {
            alertView = convertView as LinearLayout?
        }
        holder.text = convertView.findViewById<View>(R.id.spinner_text) as TextView

        holder.text!!.text = items[position].name

        return alertView as View
    }


    private class ViewHolder {
        var text: TextView? = null
        var accountno: TextView? = null
        var accountid: TextView? = null

    }
}
