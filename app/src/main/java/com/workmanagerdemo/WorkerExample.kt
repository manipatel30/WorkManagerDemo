package com.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Created by Manish Patel on 10/21/2021.
 */
/**
 * EXAMPLE WORKER to do a task in background
 */
class WorkerExample(context: Context, params: WorkerParameters) : Worker(context, params) {


    companion object {
        val TAG = WorkerExample::class.java.simpleName
        val ARG_EXTRA_PARAM = "ARG_EXTRA_PARAM"
        val OUTPUT_DATA_PARAM1 = "OUTPUT_DATA_PARAM1"
        val OUTPUT_DATA_PARAM2 = "OUTPUT_DATA_PARAM2"
    }

    override fun doWork(): Result {

        // Get data
        val param = inputData.getString(ARG_EXTRA_PARAM)

        Thread.sleep(20000)

        //do the work you want done in the background here
        Log.d(TAG, "Worker Test: doWork() called & param = $param")

        val outputData = createOutputData("Hello From WorkerExample", 100)
        return Result.success(outputData)
    }

    private fun createOutputData(firstData: String, secondData: Int): Data {
        return Data.Builder()
            .putString(OUTPUT_DATA_PARAM1, firstData)
            .putInt(OUTPUT_DATA_PARAM2, secondData)
            .build()
    }
}