package com.workmanagerdemo.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.workmanagerdemo.extensions.saveToInternalStorage
import com.workmanagerdemo.extensions.stringToURL
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection

/**
 * Created by Manish Patel on 10/21/2021.
 */
class DownloadImageWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val imageUrl = inputData.getString(ARG_EXTRA_PARAM)
        Log.e(WorkerExample.TAG, "Start downloading image from imageUrl= $imageUrl")
        val url = stringToURL(imageUrl)
        var connection: HttpURLConnection? = null
        try {
            connection = url?.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)

            // Return the downloaded bitmap
            val bmp: Bitmap? = BitmapFactory.decodeStream(bufferedInputStream)
            val uri: Uri? = bmp?.saveToInternalStorage(applicationContext)

            Log.e(TAG, "success")
            // Return the success with output data
            return Result.success(createOutputData(uri))

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, e.toString())

        } finally {
            // Disconnect the http url connection
            connection?.disconnect()
        }
        Log.e(TAG, "failed")
        return Result.failure(createOutputData(null))
    }

    companion object {
        val TAG = DownloadImageWorker::class.java.simpleName
        val ARG_EXTRA_PARAM = "imageUrl"
        val OUTPUT_DATA_PARAM = "imageURI"
    }

    private fun createOutputData(uri: Uri?): Data {
        return Data.Builder()
            .putString(OUTPUT_DATA_PARAM, uri.toString())
            .build()
    }
}