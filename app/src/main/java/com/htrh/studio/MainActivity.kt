package com.htrh.studio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.htrh.studio.rotarybutton.R
import com.htrh.studio.rotarybutton.RotaryButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var click: Boolean = true

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "${rb_ex.isEnabled}")
        rb_ex.setOnSeekBarChangeListener(object :
            RotaryButton.OnCircleSeekBarChangeListener {
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
    }
}