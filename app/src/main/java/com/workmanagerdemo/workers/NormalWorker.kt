package com.workmanagerdemo.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NormalWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        Thread.sleep(5000)
        //do the work you want done in the background here
        Log.e(TAG, "NormalWorker doWork() called")
        return Result.success()
    }

    companion object {
        val TAG = MyWorker::class.java.simpleName
    }
}