package com.abhishek.outx.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abhishek.outx.R
import com.abhishek.outx.databinding.ActivityMainBinding
import com.abhishek.outx.model.ResultTimeFeed
import com.abhishek.outx.model.TimeFeedModel
import com.abhishek.outx.ui.fragments.CustomDialogFragment
import com.abhishek.outx.utils.Constants
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var switchStatus: Boolean = false

    private var inTimeHours = 0
    private var inTimeMin = 0
    private var effectiveHours = 0
    private var effectiveMin = 0
    private var grossHours = 0
    private var grossMin = 0

    private lateinit var customDialogFragment: CustomDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() {
        /**ClickListeners*/
        binding.cvInTime.setOnClickListener(this)
        binding.cvEffectiveHours.setOnClickListener(this)
        binding.cvGrossHours.setOnClickListener(this)
        binding.btnCalculate.setOnClickListener(this)
        binding.iconMenu.setOnClickListener(this)
        binding.switchPartialDay.setOnClickListener(this)
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view, Gravity.END)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.side_menu, popup.menu)

        //set menu item click listener here
        popup.setOnMenuItemClickListener {
            if (it.itemId==R.id.contactMenu) {
                openUrl(Constants.url)
            }
            true
        }
        popup.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.side_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.contactMenu -> {
                openUrl(Constants.url)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.cvInTime -> {
                callTimePicker("Select In Time", binding.cvInTime)
            }

            binding.cvEffectiveHours -> {
                callTimePicker("Select Effective Time", binding.cvEffectiveHours)
            }

            binding.cvGrossHours -> {
                callTimePicker("Select Gross Time", binding.cvGrossHours)
            }

            binding.btnCalculate -> {
                if (validation() == 0) {
                    calculateResult()
                } else {
                    Toast.makeText(this, "Give Proper Details", Toast.LENGTH_SHORT).show()
                }
            }

            binding.iconMenu -> {
                showPopupMenu(binding.iconMenu)
            }

            binding.switchPartialDay -> {
                switchStatus = binding.switchPartialDay.isChecked
                Toast.makeText(this, "$switchStatus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateResult() {
        val outTime = outTimeCalculation()
        val result = breakTimeCalculation(outTime)

        customDialogFragment = CustomDialogFragment()
        val args = Bundle()
        args.putParcelable(Constants.passingDataKey, result)
        customDialogFragment.arguments = args
        customDialogFragment.show(supportFragmentManager, "dialog")

    }

    private fun breakTimeCalculation(outTime: Int): ResultTimeFeed {
        val breakInMin =
            convertAllInMin(grossHours, grossMin) - convertAllInMin(effectiveHours, effectiveMin)
        val actualOutTime = outTime + breakInMin

        val breakHours = breakInMin / 60
        val breakMin = breakInMin % 60


        val outTimeHours = actualOutTime / 60
        val outTimeMin = actualOutTime % 60

        val resultBreak = checkDigitOfTime(breakHours, breakMin)
        val resultOutTime = checkDigitOfTime(outTimeHours, outTimeMin)

        return ResultTimeFeed(
            breakTaken = "${resultBreak.hour}:${resultBreak.min}",
            outTime = "${resultOutTime.hour}:${resultOutTime.min}"
        )
    }

    private fun outTimeCalculation(): Int {
        val outTimeHours = if (switchStatus) {
            inTimeHours + 6
        } else {
            inTimeHours + 8
        }
        val data = checkDigitOfTime(outTimeHours, inTimeMin)
        return convertAllInMin(data.hour.toInt(), data.min.toInt())
    }

    private fun convertAllInMin(hour: Int, min: Int): Int {
        return (hour * 60) + min
    }

    private fun validation(): Int {
        var error = 0

        if (inTimeHours.toString().isEmpty() && inTimeMin.toString().isEmpty()) {
            error++
        } else {
            error = 0
        }
        if (effectiveHours.toString().isEmpty() && effectiveMin.toString().isEmpty()) {
            error++
        } else {
            error = 0
        }
        if (grossHours.toString().isEmpty() && grossMin.toString().isEmpty()) {
            error++
        } else {
            error = 0
        }
        return error
    }

    @SuppressLint("SetTextI18n")
    private fun callTimePicker(title: String, v: View) {
        val picker =
            MaterialTimePicker.Builder()
                .setTitleText(title)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()
        picker.show(supportFragmentManager, "tag")
        picker.addOnPositiveButtonClickListener {
            // call back code
            when (v) {
                binding.cvInTime -> {
                    inTimeHours = picker.hour
                    inTimeMin = picker.minute
                    val data = checkDigitOfTime(inTimeHours, inTimeMin)
                    binding.tvInTime.text = "${data.hour}:${data.min}"
                }

                binding.cvEffectiveHours -> {
                    effectiveHours = picker.hour
                    effectiveMin = picker.minute
                    val data = checkDigitOfTime(effectiveHours, effectiveMin)
                    binding.tvEffectiveHours.text = "${data.hour}:${data.min}"
                }

                binding.cvGrossHours -> {
                    grossHours = picker.hour
                    grossMin = picker.minute
                    val data = checkDigitOfTime(grossHours, grossMin)
                    binding.tvGrossHours.text = "${data.hour}:${data.min}"
                }
            }
        }
        picker.addOnNegativeButtonClickListener {
            // call back code
        }
    }

    private fun checkDigitOfTime(hour: Int, min: Int): TimeFeedModel {
        return TimeFeedModel(
            hour = if (hour.toString().length == 1) {
                "0$hour"
            } else {
                hour.toString()
            },
            min = if (min.toString().length == 1) {
                "0$min"
            } else {
                min.toString()
            }
        )
    }
}