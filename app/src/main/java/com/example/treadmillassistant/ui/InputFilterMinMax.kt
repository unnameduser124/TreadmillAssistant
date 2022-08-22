package com.example.treadmillassistant.ui

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private var min: Double, private var max: Double) : InputFilter {

    override fun filter(source:CharSequence, start:Int, end:Int, dest: Spanned, dstart:Int, dend:Int): CharSequence? {
        if(source=="-"){
            return null
        }
        try
        {
            val before = dest.toString()
            val input = StringBuilder(before).insert(dstart, source.toString()).toString().toDouble()
            if (isInRange(min, max, input)){
                return null
            }
        }
        catch (nfe:NumberFormatException) {

        }
        catch(outOfBoundsException: IndexOutOfBoundsException){
            println(outOfBoundsException.message)
        }
        return ""
    }
    private fun isInRange(a: Double, b: Double, c:Double):Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}