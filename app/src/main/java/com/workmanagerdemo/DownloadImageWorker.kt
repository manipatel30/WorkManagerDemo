package com.workmanagerdemo

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
 * Created by Manish Patel on 5/23/2019.
 */
class DownloadImageWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        // Get data
        val imageUrl = inputData.getString(ARG_EXTRA_PARAM)
        val url = stringToURL(imageUrl)

        // IMPORTANT - Put internet permission on manifest file
        var connection: HttpURLConnection? = null

        try {
            // Initialize a new http url connection
            connection = url?.openConnection() as HttpURLConnection

            // Connect the http url connection
            connection.connect()

            // Get the input stream from http url connection
            val inputStream = connection.getInputStream()

            // Initialize a new BufferedInputStream from InputStream
            val bufferedInputStream = BufferedInputStream(inputStream)

            // Convert BufferedInputStream to Bitmap object

            // Return the downloaded bitmap
            val bmp: Bitmap? = BitmapFactory.decodeStream(bufferedInputStream)
            val uri: Uri? = bmp?.saveToInternalStorage(applicationContext)

            Log.d("download", "success")
            // Return the success with output data
            return Result.success(createOutputData(uri))

        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("download", e.toString())

        } finally {
            // Disconnect the http url connection
            connection?.disconnect()
        }
        Log.d("download", "failed")
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