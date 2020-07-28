package com.htrh.studio.rotarybutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rb_ex.isEnabled = false
        rb_ex.setOnSeekBarChangeListener(object : RotaryButton.OnCircleSeekBarChangeListener{
            override fun onProgressChange(progress: Int) {
                Log.d(TAG, "progress: $progress")
            }

            override fun onStartTrackingTouch(rotaryButton: RotaryButton?) {
                Log.d(TAG, "start tracking")
            }

            override fun onStopTrackingTouch(rotaryButton: RotaryButton?) {
                Log.d(TAG, "stop tracking")
            }
        })
        rb_ex.setOnClickListener {
            if (!rb_ex.isEnabled) {
                Toast.makeText(this, "isEnable: ${rb_ex.isEnabled}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}