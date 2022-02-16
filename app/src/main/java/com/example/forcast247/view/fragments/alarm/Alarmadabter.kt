package com.example.forcast247_app.weather.view.fragments.alarm

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forcast247.AlarmEntity
import com.example.forcast247.databinding.AlertItemBinding
import kotlinx.android.synthetic.main.alert_item.view.*
import java.util.*

class Alarmadabter(val context: Context) : RecyclerView.Adapter<Alarmadabter.ViewHolder>(){
        private var alarmList: MutableList<AlarmEntity>
        lateinit var sp:SharedPreferences
        init {

            alarmList= ArrayList<AlarmEntity>()
            sp= PreferenceManager.getDefaultSharedPreferences(context)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return alarmList.size
        }

        fun fetchData(alertList: MutableList<AlarmEntity>, context: Context) {
            this.alarmList = alertList
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(alarmList[position])

        }



        fun getItemByVH(viewHolder: RecyclerView.ViewHolder): AlarmEntity {
            return alarmList.get(viewHolder.adapterPosition)
        }

        fun removeAlarmItem(viewHolder: RecyclerView.ViewHolder) {
            alarmList.removeAt(viewHolder.adapterPosition)
            notifyItemRemoved(viewHolder.adapterPosition)

        }

        class ViewHolder(itemView: AlertItemBinding) : RecyclerView.ViewHolder(itemView.root) {

            fun bind(alarmEntity: AlarmEntity){
                itemView.timeFrom.text =alarmEntity.TimeFrom
                itemView.timeTo.text =alarmEntity.TimeTo
                itemView.tv_status.text= alarmEntity.main


            }


        }
    }