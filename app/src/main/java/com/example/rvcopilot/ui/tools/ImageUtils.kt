package com.example.rvcopilot.ui.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

fun uriToBase64(context: Context, uri: Uri): String {
    println("uri to Base 64 conversion function")
    return try {
        //val inputStream = context.contentResolver.openInputStream(uri)
        //val bytes = inputStream?.readBytes()
        val bytes = resizeAndCompressImage(context, uri)

        //inputStream?.close()

        if (bytes == null) {
            println("Failed to resize or read image")
            return ""
        }

        val base64Encoded = Base64.encodeToString(bytes, Base64.DEFAULT)
        val encodeSize = base64Encoded.toByteArray().size
        println("Encoded Size: $encodeSize bytes")
        println("Original resized byte size: ${bytes.size} bytes")

        val maxSizeAllow = 1048487  //Firestore max allowed bytes stored into database
        val sizeWithMargin = 0.8 * maxSizeAllow
        // interpolation of values could cause a difference in max allowed
        // and what was saved or trying to save and rejected
        println("Firestore max allowed bytes: 1_048_487")
        println("Max allowed bytes with margin: %.2f".format(sizeWithMargin))

        if (encodeSize < sizeWithMargin) { // $ causes string interpolation
            println("Image size saved to Firestore: ${bytes.size} bytes")
            return base64Encoded

        } else {
            println("Image too large to save in Firestore (interpolated size): ${bytes?.size ?: 0} bytes")
            println("default image will be saved instead")
            return "" // try-catch function returns an empty string, it must return a string
        }
    } catch (e: Exception) {
        println("Error converting URI to Base64: ${e.localizedMessage}")
        return "" // try-catch function returns an empty string, it must return a string
    }
}

fun resizeAndCompressImage(
    context: Context,
    uri: Uri,
    maxWidth: Int = 800,
    maxHeight: Int = 800
): ByteArray? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val ratio = minOf(
            maxWidth.toFloat() / originalBitmap.width,
            maxHeight.toFloat() / originalBitmap.height
        )

        val newWidth = (originalBitmap.width * ratio).toInt()
        val newHeight = (originalBitmap.height * ratio).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)  // adjust quality if needed

        println("Resized to ${newWidth}x$newHeight, compressed byte size: ${outputStream.size()} bytes")
        outputStream.toByteArray()
    } catch (e: Exception) {
        println("Error resizing image: ${e.localizedMessage}")
        null
    }
}