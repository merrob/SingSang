package com.example.singsang.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.singsang.Data.Vogel
import com.example.singsang.R
import com.example.singsang.Sehen.Companion.globalPosition

class SearchAdapter(context: Context,val layout: Int,val vogel:List<Vogel> ):ArrayAdapter<Vogel>(context,layout,vogel) {

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        Log.d("imageviewDebug","notified")

    }
    override fun getCount(): Int {
        return vogel.size
    }

    override fun getItem(position: Int): Vogel {

        return vogel.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        var retView: View
        var vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {

            retView =  vi.inflate(layout, null)
        } else {

            retView = convertView
        }
        var vogelItem = getItem(position)
        val vogelName = retView.findViewById<TextView>(R.id.tvSearch)

        vogelName.text = vogelItem.word
        return retView
    }
}