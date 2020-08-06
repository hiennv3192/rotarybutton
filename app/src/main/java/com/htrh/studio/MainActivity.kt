package com.htrh.studio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.htrh.studio.customview.R
import com.htrh.studio.rotatybutton.RotaryButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rotary_button_3.run {
            setButtonBgPadding(130f)
            setButtonFgPadding(150f)
            setProgressPadding(50f)
            setButtonStartDegrees(215)
            setMaxRotateDegrees(290)
            setProgress(150)
            setProgressMax(300)
            setProgressStartDegrees(215)
            setOnClickListener {
                if (!it.isEnabled) {
                    Toast.makeText(this@MainActivity, "Not enable", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            setOnSeekBarChangeListener(object : RotaryButton.OnCircleSeekBarChangeListener {
                override fun onProgressChange(progress: Int) {
                    tv_rotary_button_progress_value.text = progress.toString()
                }

                override fun onStartTrackingTouch(rotaryButton: RotaryButton?) {
                    Toast.makeText(this@MainActivity, "Start", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onStopTrackingTouch(rotaryButton: RotaryButton?) {
                    Toast.makeText(this@MainActivity, "Stop", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}