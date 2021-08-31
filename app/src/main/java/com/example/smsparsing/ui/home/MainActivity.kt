package com.example.smsparsing.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.smsparsing.api.model.sms_model.SmsModel
import com.example.smsparsing.databinding.ActivityMainBinding
import com.example.smsparsing.ui.msg_details.MsgDetailsActivity
import com.example.smsparsing.utils.ViewState
import com.example.smsparsing.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var checkList: MutableList<String> = mutableListOf()
    private var totalMatchedDataFound = 0
    private var idListOfSMS: MutableList<String> = mutableListOf()
    private var messageData: MutableList<SmsModel> = mutableListOf()
    private var msgDataFound = false
    private var bundle = bundleOf()


    private var count = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()

        checkList.clear()
        checkList.addAll(listOf("Nagad", "combank", "bKash"))

    }

    private fun initClickListener() {

        binding?.readSmsBtn?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), 111)
            } else {
                receiveMsg()
            }
        }

        binding?.showList?.setOnClickListener {
            toast("${idListOfSMS}")
            Timber.d("MessageDataList $idListOfSMS")
            Timber.d("MessageData $messageData")
            toast("Data found total: ${totalMatchedDataFound}")
        }

        binding?.showDetails?.setOnClickListener {
            //Timber.d("requestBody 1 $messageData")
            if (!bundle.isEmpty) {
                val intent = Intent(this, MsgDetailsActivity::class.java)
                intent.putExtra("bundle", bundle)
                startActivity(intent)
            } else {
                toast("No data Available")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            receiveMsg()
        }
    }

    private fun receiveMsg() {

        binding?.progressBar?.isVisible = true

        lifecycleScope.launch(Dispatchers.IO) {
            val cursor = contentResolver.query(
                Uri.parse("content://sms/inbox"),
                null, null,null, null)

            cursor?.moveToFirst()
            var msgData = ""
            idListOfSMS.clear()
            messageData.clear()
            totalMatchedDataFound = 0


            withContext(Dispatchers.Main) {

                binding?.progressBar?.isVisible = false

                if (cursor?.moveToFirst() == true) { // must check the result to prevent exception
                    do {
                        count -= 1
                        for (idx in 0 until cursor!!.columnCount) {
                            msgData += " \n" + "$idx  " + cursor!!.getColumnName(idx) + ":" + cursor!!.getString(idx)
                        }
                        msgData += "\n"
                        val tempId = cursor?.getString(0)
                        var tempData = cursor?.getString(12)
                        val tempSenderId = cursor?.getString(0)
                        var tempSender = cursor?.getString(2)
                        tempData = tempData?.lowercase(Locale.getDefault())
                        tempSender = tempSender?.lowercase(Locale.getDefault())

                        /*if (tempData.contains("emergency")) {
                            idListOfSMS.add(tempId)
                        }*/

                        for (i in checkList) {
                            if (tempSender.contains(i.lowercase(Locale.getDefault()))) {
                                totalMatchedDataFound += 1
                                idListOfSMS.add(tempSenderId)
                                messageData.add(SmsModel(
                                    cursor?.getString(2),
                                    cursor?.getString(12),
                                    cursor?.getString(13))
                                )
                                break
                            }
                        }

                        // use msgData
                    } while (cursor!!.moveToNext() && count != 0)
                    msgDataFound = true
                } else {
                    if (cursor?.moveToFirst() == false) {
                        msgDataFound = false
                    }
                    // empty box, no SMS
                }

                binding?.displaySms?.text = msgData
                dataValidation()
            }
        }
        //binding?.progressBar?.isVisible = false
    }

    private fun dataValidation() {
        if (msgDataFound) {
            binding?.showDetails?.isVisible = true
            bundle = bundleOf(
                "msgData" to messageData
            )
            toast("Data found total: ${totalMatchedDataFound}")
        } else {
            binding?.showDetails?.isVisible = false
            toast("No data found")
        }
    }
}