package com.workmanagerdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager

/**
 * Created by Manish Patel on 5/23/2019.
 */
class WorkerViewModel : ViewModel() {

    private var mWorkManager: WorkManager = WorkManager.getInstance()
    private var mSavedWorkInfo: LiveData<List<WorkInfo>>

    init {
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(WorkerExample.TAG)
    }

    fun initWorker() {
        val data = Data.Builder()

        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString(WorkerExample.ARG_EXTRA_PARAM, "From ViewModel")

        //Set Input Data
        val workerTest = OneTimeWorkRequestBuilder<WorkerExample>()
            .setInputData(data.build())
            .addTag(WorkerExample.TAG)
            .build()

        // Now, enqueue your work
        mWorkManager.enqueue(workerTest)
    }

    internal fun getOutputWorkInfo(): LiveData<List<WorkInfo>> {
        return mSavedWorkInfo
    }
}