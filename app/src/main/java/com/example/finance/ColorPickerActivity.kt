package com.example.finance

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.finance.databinding.ActivityColorPickerBinding

class ColorPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColorPickerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Активировать ActionBar - меню
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Присвоить по кмолчанию белый цвет
        binding.btnColorPreview.setBackgroundColor(Color.parseColor("#FFFFFFFF"))

        binding.strColor.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.colorR.progress = Integer.parseInt(p0!!.substring(0..1), 16)
                binding.colorG.progress = Integer.parseInt(p0.substring(2..3), 16)
                binding.colorB.progress = Integer.parseInt(p0.substring(4..5), 16)
            }
        })

        //binding.colorR.max = 255
        binding.colorR.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val colorStr = getColorString()
                binding.strColor.setText(colorStr.replace("#","").uppercase())
                binding.btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))          }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        //binding.colorG.max = 255
        binding.colorG.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val colorStr = getColorString()
                binding.strColor.setText(colorStr.replace("#","").uppercase())
                binding.btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        //binding.colorB.max = 255
        binding.colorB.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val colorStr = getColorString()
                binding.strColor.setText(colorStr.replace("#","").uppercase())
                binding.btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.colorCancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.colorOkBtn.setOnClickListener {
            // Получить код цвета
            val color : String = getColorString()
            // Отправить цвет в activity для редактирования категорий
            val intent  = Intent()
            intent.putExtra(MyConstants.COLOR, color)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getColorString(): String {
        binding.apply {
            var r = Integer.toHexString(((255 * colorR.progress) / colorR.max))
            if (r.length == 1) r = "0" + r
            var g = Integer.toHexString(((255 * colorG.progress) / colorG.max))
            if (g.length == 1) g = "0" + g
            var b = Integer.toHexString(((255 * colorB.progress) / colorB.max))
            if(b.length==1) b = "0"+b
            return "#${r}${g}${b}"
        }
    }
}