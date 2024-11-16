package com.shubham.alarmassignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shubham.alarmassignment.communicator.IAlarmCallbackProvider
import com.shubham.alarmassignment.data.AlarmModel
import com.shubham.alarmassignment.databinding.ItemAlarmBinding

class AlarmAdapter(
    private val list: List<AlarmModel>,
    private val listener: IAlarmCallbackProvider
) : RecyclerView.Adapter<AlarmAdapter.MyViewBinderClass>() {


    inner class MyViewBinderClass(val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmAdapter.MyViewBinderClass {
        return MyViewBinderClass(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlarmAdapter.MyViewBinderClass, position: Int) {
        val data = list[position]
        holder.binding.apply {
            tvTime.text = data.alarmTime
            sbAlarmSwitch.isChecked = data.isActive
            ivDeleteButton.setOnClickListener { listener.deleteBtnClicked(data , position) }
            sbAlarmSwitch.setOnClickListener { listener.switchStateIsChanges(position) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}