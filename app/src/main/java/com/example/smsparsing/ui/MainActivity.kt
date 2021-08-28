package com.example.smsparsing.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.smsparsing.api.model.sms_model.SmsModel
import com.example.smsparsing.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var checkList: MutableList<String> = mutableListOf()
    private var totalMatchedDataFound = 0
    private var idListOfSMS: MutableList<String> = mutableListOf()
    private var messageData: MutableList<SmsModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkList.clear()
        checkList.addAll(listOf("Nagad", "combank", "bKash"))

        binding?.readSmsBtn?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), 111)
            } else {
                receiveMsg()
            }
        }

        binding?.showList?.setOnClickListener {
            Toast.makeText(this, "${idListOfSMS}", Toast.LENGTH_SHORT).show()
            Timber.d("MessageDataList $idListOfSMS")
            Timber.d("MessageData $messageData")
            Toast.makeText(this, "Data found total: ${totalMatchedDataFound}", Toast.LENGTH_SHORT).show()
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
        val cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null, null,null, null)

        cursor?.moveToFirst()
        var msgData = ""
        idListOfSMS.clear()
        messageData.clear()
        var count = 100
        if (cursor?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                count -= 1
                for (idx in 0 until cursor!!.columnCount) {
                    msgData += " \n" + "$idx  " + cursor!!.getColumnName(idx) + ":" + cursor!!.getString(idx)
                }
                //Toast.makeText(this, "${cursor?.getString(12)}", Toast.LENGTH_SHORT).show()
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
                    //Toast.makeText(this, "checking with $i", Toast.LENGTH_SHORT).show()
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
        } else {
            if (cursor?.moveToFirst() == false) {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            }
            // empty box, no SMS
        }

        //Toast.makeText(this, "${idListOfSMS}", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Data found total: ${totalMatchedDataFound}", Toast.LENGTH_SHORT).show()

        //cursor?.moveToFirst()
        //binding?.displaySms?.text = cursor?.getString(12)
        binding?.displaySms?.text = msgData

    }
}