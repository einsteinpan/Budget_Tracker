package com.example.budgettracker

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class qrcode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode)

        val button_ok = findViewById<View>(R.id.button_ok) as Button
        val closeBtn2 = findViewById<View>(R.id.closeBtn2) as ImageButton
        val spinnerOptions = findViewById<Spinner>(R.id.spinner_options)
        val image_view = findViewById<ImageView>(R.id.image_view)

        val options = arrayOf("常用載具", "用戶一", "用戶二") // 替换成您的常用选项
        //val options = arrayOf("Option 1", "Option 2", "Option 3", "New Option 4", "Another New Option") // 添加或修改选项


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = adapter

        button_ok.setOnClickListener(btn_ok)
        closeBtn2.setOnClickListener { finish() }

        spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = options[position]
                generateQRCode(selectedOption)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // 如果没有选项被选择
            }
        }
    }

    private val btn_ok = View.OnClickListener { v -> genCode(v) }

    fun genCode(view: View?) {
        val input = findViewById<View>(R.id.input) as EditText
        val image_view = findViewById<View>(R.id.image_view) as ImageView
        val encoder = BarcodeEncoder()
        try {
            val bit: Bitmap = encoder.encodeBitmap(
                input.text.toString(), BarcodeFormat.QR_CODE,
                250, 250
            )
            image_view.setImageBitmap(bit)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateQRCode(data: String) {
        val image_view = findViewById<ImageView>(R.id.image_view)
        val encoder = BarcodeEncoder()
        try {
            val bit: Bitmap = encoder.encodeBitmap(
                data, BarcodeFormat.QR_CODE,
                250, 250
            )
            image_view.setImageBitmap(bit)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}