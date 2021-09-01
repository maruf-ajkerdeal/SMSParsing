package com.example.smsparsing.ui.msg_details

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smsparsing.R
import com.example.smsparsing.api.model.sms_model.SmsModel
import com.example.smsparsing.databinding.ActivityMainBinding
import com.example.smsparsing.databinding.ActivityMsgDetailsBinding
import com.example.smsparsing.utils.toast
import timber.log.Timber
import java.util.*

class MsgDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMsgDetailsBinding
    private var messageData: MutableList<SmsModel> = mutableListOf()

    private var dataAdapter = MsgDetailsAdapter()

    private val dataList: MutableList<SmsModel> = mutableListOf()
    private val checkList: MutableList<String> = mutableListOf()
    private val creditList: MutableList<String> = mutableListOf()
    private val debitList: MutableList<String> = mutableListOf()

    private var totalMessageCount = 0
    private var totalCreditMessageCount = 0
    private var totalDebitMessageCount = 0
    private var totalCreditAmount = 0.00
    private var totalDebitAmount = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMsgDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")
        messageData = bundle?.getParcelableArrayList<Parcelable>("msgData") as MutableList<SmsModel>

        dataAdapter = MsgDetailsAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MsgDetailsActivity)
            adapter = dataAdapter
        }

        //Timber.d("requestBody 2 $messageData")

        loadData()
        initClickListener()

        filterMessageData(messageData)



    }

    private fun filterMessageData(msgModel: MutableList<SmsModel>) {

        totalCreditAmount = 0.00

        checkList.clear()
        checkList.addAll(listOf("tk", "bdt"))

        creditList.clear()
        creditList.addAll(listOf("received", "credited", "deposit"))

        debitList.clear()
        debitList.addAll(listOf("payment", "debited", "purchase", "deducted"))

        val amountCheck = """(\d+(,\d+)*(.\d+)*)""".toRegex()
        for (model in msgModel) {
            if (((model.body)?.contains(amountCheck) == true) ) {
                for (i in checkList) {
                    if (((model.body)?.lowercase(Locale.getDefault())?.contains(i) == true)) {

                        totalCreditMessageCount += 1

                        val amountCheckTest = """((\d+,\d+)*(\d+\.\d+))""".toRegex()
                        //val tempData : MatchResult? = amountCheckTest.find(model.body.toString())
                        //Timber.d("requestBody single ${tempData?.value}")

                        val tempData : Sequence<MatchResult> = amountCheckTest.findAll(model.body.toString())
                        var tempAmount = 0.00
                        tempData.forEach()
                        {
                            Timber.d("requestBody ${it.value}")
                            if (it.value.contains(",")) {
                                var tempString = it.value
                                Timber.d("requestBody string $tempString")
                                tempString = tempString.replace(",", "")
                                Timber.d("requestBody string $tempString")
                                tempAmount = tempString.toDouble()
                            }
                            //matchResult -> Timber.d("requestBody ${matchResult.value}")
                        }

                        totalCreditAmount += tempAmount

                        break
                    } else {
                        /*binding.msgAddress.text = model.address
                        binding.serviceCenter.text = model.serviceCenter
                        binding.msgBody.text = model.body
                        binding.msgBody.setTextColor(Color.BLUE)*/
                    }
                }
            } else {
                /*binding.msgAddress.text = model.address
                binding.serviceCenter.text = model.serviceCenter
                binding.msgBody.text = model.body
                binding.msgBody.setTextColor(Color.BLUE)*/
                //dataList.removeAt(position)
            }
        }

    }

    private fun initClickListener() {
        binding?.showList?.setOnClickListener {
            Timber.d("requestBody $totalCreditMessageCount, $totalCreditAmount")
            toast("Credit: $totalCreditMessageCount, $totalCreditAmount")

        }
    }

    private fun loadData() {
        if (messageData.size != 0) {
            dataAdapter.clearData()
            dataAdapter.loadInitData(messageData)
        } else {
            binding?.emptyView?.isVisible = true
        }
    }
}