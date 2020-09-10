package com.elanciers.bringszo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elanciers.booking.DataClass.SpinnerPojo
import com.elanciers.bringszo.Common.Appconstands.rupees
import com.elanciers.bringszo.R


class OptionsAdapter(internal var context: Context, internal var resource: Int, internal var items: ArrayList<SpinnerPojo>) :
        ArrayAdapter<SpinnerPojo>(context, resource, items) {

    internal var inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        var itemView = itemView
        val holder = ViewHolder()
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.optioslist_adapter, null)
            itemView!!.tag = holder
        }

        holder.opt = itemView.findViewById(R.id.opt) as RadioButton
        holder.optpr = itemView.findViewById(R.id.optpr) as TextView
        /*if(items[position] == position){
            holder.opt!!.setChecked(true);
        }
        else{
            holder.opt!!.setChecked(false)
        }*/


        holder.opt!!.setText(items[position].name)
        holder.optpr!!.setText(rupees+items[position].price)


        return itemView
    }

    internal inner class ViewHolder {
        var opt : RadioButton? = null
        var optpr : TextView? = null

    }

    /*private fun createRadioButton() {
        val rb = arrayOfNulls<RadioButton>(5)
        val rg = RadioGroup(this) //create the RadioGroup
        rg.orientation = RadioGroup.HORIZONTAL//or RadioGroup.VERTICAL
        for (i in 0..4) {
            rb[i] = RadioButton(this)
            rb[i].setText(" " + ContactsActivity.phonetype.get(i)
                    + "    " + ContactsActivity.phone.get(i))
            rb[i].setId(i + 100)
            rg.addView(rb[i])
        }
        ll.addView(rg)//you add the whole RadioGroup to the layout

    }*/
}