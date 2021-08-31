package com.example.smsparsing.ui.msg_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smsparsing.R
import com.example.smsparsing.api.model.sms_model.SmsModel
import com.example.smsparsing.databinding.ActivityMainBinding
import com.example.smsparsing.databinding.ActivityMsgDetailsBinding
import timber.log.Timber

class MsgDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMsgDetailsBinding
    private var messageData: MutableList<SmsModel> = mutableListOf()

    private var dataAdapter = MsgDetailsAdapter()

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