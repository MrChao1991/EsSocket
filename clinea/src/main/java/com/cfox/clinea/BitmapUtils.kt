package com.cfox.clinea

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object BitmapUtils  {


    fun getBitmapForRes(resources : Resources, res: Int) : Bitmap {
        return BitmapFactory.decodeResource(resources, res)
    }


    fun bitmap2Bytes(bitmap: Bitmap) : ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 , byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()

    }


    fun bytes2Bitmap(byteArray: ByteArray) : Bitmap ? {
        if (byteArray.isNotEmpty()) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        return null
    }
}