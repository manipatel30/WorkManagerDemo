package com.workmanagerdemo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.workmanagerdemo.App.Companion.context
import com.workmanagerdemo.workers.MyWorker

class MainViewModel : ViewModel() {

    private var mWorkManager: WorkManager = WorkManager.getInstance(context)
    private var mSavedWorkInfo: LiveData<List<WorkInfo>> = mWorkManager.getWorkInfosByTagLiveData(MyWorker.TAG)

    fun initWorker() {
        val data = Data.Builder()

        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString(MyWorker.ARG_EXTRA_PARAM, "From ViewModel")

        //Set Input Data
        val workerTest = OneTimeWorkRequestBuilder<MyWorker>()
            .setInputData(data.build())
            .addTag(MyWorker.TAG)
            .build()

        // Now, enqueue your work
        mWorkManager.enqueue(workerTest)
    }

    internal fun getOutputWorkInfo(): LiveData<List<WorkInfo>> {
        return mSavedWorkInfo
    }
}