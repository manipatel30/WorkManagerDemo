package com.workmanagerdemo.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    override fun doWork(): Result {

        // Get data
        val param = inputData.getString(ARG_EXTRA_PARAM)

        Thread.sleep(10000)

        //do the work you want done in the background here
        Log.e(TAG, "MyWorker: doWork() called & param = $param")

        val outputData = createOutputData("Hello From MyWorker", 100)
        return Result.success(outputData)
    }

    private fun createOutputData(firstData: String, secondData: Int): Data {
        return Data.Builder()
            .putString(OUTPUT_DATA_PARAM1, firstData)
            .putInt(OUTPUT_DATA_PARAM2, secondData)
            .build()
    }

    companion object {
        val TAG = MyWorker::class.java.simpleName
        val ARG_EXTRA_PARAM = "ARG_EXTRA_PARAM"
        val OUTPUT_DATA_PARAM1 = "OUTPUT_DATA_PARAM1"
        val OUTPUT_DATA_PARAM2 = "OUTPUT_DATA_PARAM2"
    }
}