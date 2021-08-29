package com.example.smsparsing.ui.msg_details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smsparsing.R
import com.example.smsparsing.api.model.sms_model.SmsModel
import com.example.smsparsing.databinding.ItemViewMsgDetailsBinding
import java.util.*

class MsgDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<SmsModel> = mutableListOf()
    private val checkList: MutableList<String> = mutableListOf()
    private val creditList: MutableList<String> = mutableListOf()
    private val debitList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemViewMsgDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val model = dataList[position]
            val binding = holder.binding

            checkList.clear()
            checkList.addAll(listOf("tk", "bdt"))

            creditList.clear()
            creditList.addAll(listOf("received", "credited", "deposit"))

            debitList.clear()
            debitList.addAll(listOf("payment", "debited", "purchase", "deducted"))

            val amountCheck = """(\d+(,\d+)*(.\d+)*)""".toRegex()
            if (((model.body)?.contains(amountCheck) == true) ) {
                for (i in checkList) {
                    if (((model.body)?.lowercase(Locale.getDefault())?.contains(i) == true)) {
                        binding?.msgAddress?.text = model.address
                        binding?.serviceCenter?.text = model.serviceCenter
                        binding?.msgBody?.text = model.body
                        binding?.msgBody?.setTextColor(ContextCompat.getColor( binding?.msgBody?.context, R.color.black_80))
                        break
                    } else {
                        binding?.msgAddress?.text = model.address
                        binding?.serviceCenter?.text = model.serviceCenter
                        binding?.msgBody?.text = model.body
                        binding?.msgBody?.setTextColor(Color.BLUE)
                    }
                }
            } else {
                binding?.msgAddress?.text = model.address
                binding?.serviceCenter?.text = model.serviceCenter
                binding?.msgBody?.text = model.body
                binding?.msgBody?.setTextColor(Color.BLUE)
                //dataList.removeAt(position)
            }

        }
    }

    private inner class ViewHolder(val binding: ItemViewMsgDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            /*binding.nextQuestion.setOnClickListener {
                if (onOptionsSelected != null) {
                    nextQuestion?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }

            }*/
        }

    }

    fun loadInitData(list: MutableList<SmsModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }
}