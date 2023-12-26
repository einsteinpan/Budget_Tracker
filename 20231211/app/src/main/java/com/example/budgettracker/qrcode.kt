package com.example.budgettracker

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class qrcode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode)

        val button_ok = findViewById<View>(R.id.button_ok) as Button
        val closeBtn2 = findViewById<View>(R.id.closeBtn2) as ImageButton

        button_ok.setOnClickListener(btn_ok)
        closeBtn2.setOnClickListener { finish() }
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
}
