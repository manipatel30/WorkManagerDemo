package com.workmanagerdemo.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.workmanagerdemo.App.Companion.context
import com.workmanagerdemo.workers.DownloadImageWorker
import com.workmanagerdemo.workers.WorkerExample
import com.workmanagerdemo.workers.NormalWorker
import com.workmanagerdemo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mViewModel: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        setupUI()
    }

    private fun setupUI() {

        // Normal worker
        binding.activityMainButtonNormalWorker.setOnClickListener {
            initWorkerNormal()
        }

        // Worker with constraints (Power & Internet)
        binding.activityMainButtonWorkerUnique.setOnClickListener {
            initWorkerUniqueWithPowerAndConnectivity()
        }

        // Periodic worker
        binding.activityMainButtonWorkerPeriodic.setOnClickListener {
            initPeriodicWorker()
        }

        // Unique worker with params and delay
        binding.activityMainButtonWorkerWithParam.setOnClickListener {
            initWorkerUniqueWithParameters()
        }

        // ViewModel worker
        binding.activityMainButtonWorkerViewModel.setOnClickListener {
            configViewModel()
            initWorkerViewModel()
        }

        // Download Image worker
        binding.activityMainButtonWorkerDownload.setOnClickListener {
            initDownloadWorker()
        }
    }

    private fun configViewModel() {
        // Show work status
        mViewModel.getOutputWorkInfo().observe(this, observer)
    }

    private fun initDownloadWorker() {

        // Create a Constraints object that defines when the task should run
        val downloadConstraints = Constraints.Builder()
            // Device need to charging for the WorkRequest to run.
            .setRequiresCharging(true)
            // Any working network connection is required for this work.
            .setRequiredNetworkType(NetworkType.CONNECTED)
            //.setRequiresBatteryNotLow(true)
            // Many other constraints are available, see the
            // Constraints.Builder reference
            .build()

        // Define the input data for work manager
        val data = Data.Builder()
        data.putString(
            DownloadImageWorker.ARG_EXTRA_PARAM,
            "https://www.freeimageslive.com/galleries/buildings/structures/pics/canal100-0416.jpg"
        )

        // Create an one time work request
        val downloadImageWork = OneTimeWorkRequest
            .Builder(DownloadImageWorker::class.java)
            .setInputData(data.build())
            .setConstraints(downloadConstraints)
            .build()

        // Enqueue the work
        WorkManager.getInstance(context).enqueue((downloadImageWork))

        // Get the work status using live data
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadImageWork.id)
            .observe(this, { workInfo ->

                showWorkerStatus(workInfo.state.name)

                workInfo?.let {
                    when (it.state) {
                        WorkInfo.State.ENQUEUED ->  showWorkerStatus("Download enqueued.")
                        WorkInfo.State.BLOCKED -> showWorkerStatus("Download blocked.")
                        WorkInfo.State.RUNNING -> showWorkerStatus("Download running.")
                    }
                }

                // When work finished
                if (workInfo != null && workInfo.state.isFinished) {

                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        showWorkerStatus("Download successful.")

                        // Get the output data
                        val successOutputData = workInfo.outputData
                        val uriText =
                            successOutputData.getString(DownloadImageWorker.OUTPUT_DATA_PARAM)

                        // If uri is not null then show it
                        uriText?.apply {
                            // If download finished successfully then show the downloaded image in image view
                            binding.imageView.setImageURI(Uri.parse(uriText))
                            showWorkerStatus(uriText)
                        }
                    } else if (workInfo.state == WorkInfo.State.FAILED) {
                        showWorkerStatus("Failed to download.")
                    } else if (workInfo.state == WorkInfo.State.CANCELLED) {
                        showWorkerStatus("Work request cancelled.")
                    }
                }
            })
    }

    private fun initWorkerNormal() {
        val worker = OneTimeWorkRequestBuilder<NormalWorker>().build()
        WorkManager.getInstance(context).enqueue(worker)
    }


    /**
     * In this example this work was called when the device is connected to Internet and
     * is charging. If you click the button and this constraints are not satisfied then the works will not be
     * launched, but when connectivity and charging will be available, the workmanager will be in charge to
     * launch it. This work will not be lost.
     */
    private fun initWorkerUniqueWithPowerAndConnectivity() {
        // optionally, add constraints like power, network availability
        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerTest = OneTimeWorkRequestBuilder<WorkerExample>()
            .setConstraints(constraints).build()

        // Now, enqueue your work
        WorkManager.getInstance(context).enqueue(workerTest)
    }

    /**
     * PERIODIC TASK
     * W/WM-WorkSpec: Interval duration lesser than minimum allowed value; Changed to 900000
     * If we want to cancel a worker we can add a Tag and cancel it by its Tag
     */
    private fun initPeriodicWorker() {
        val mWorkManager = WorkManager.getInstance(context)
        mWorkManager.cancelAllWorkByTag(WorkerExample.TAG)

        val periodicBuilder =
            PeriodicWorkRequest.Builder(WorkerExample::class.java, 1, TimeUnit.MINUTES)
        val myWork = periodicBuilder.addTag(WorkerExample.TAG).build()
        mWorkManager.enqueue(myWork)
    }

    /**
     * To send parameter to a Work:
     *    val data = Data.Builder()     *
     *   //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
     *   data.putString(WorkerExample.ARG_EXTRA_PARAM, "your string param")
     *
     * And in the worker to get the data:
     *   val param =  inputData.getString(ARG_EXTRA_PARAM)
     */
    private fun initWorkerUniqueWithParameters() {
        val data = Data.Builder()

        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString(WorkerExample.ARG_EXTRA_PARAM, "Hello Word!")

        //Set Input Data
        val workerTest = OneTimeWorkRequestBuilder<WorkerExample>()
            .setInputData(data.build())
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Now, enqueue your work
        WorkManager.getInstance(context).enqueue(workerTest)
    }


    private fun initWorkerViewModel() {
        showWorkerStatus("Init")
        mViewModel.initWorker()
    }

    private val observer = Observer<List<WorkInfo>> { state ->
        state?.let {
            if (it == null || it.isEmpty()) {
                showWorkerStatus("Empty")
            } else {
                // We only care about the one output status.
                // Every continuation has only one worker tagged TAG_OUTPUT
                val workInfo = it[0]

                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> {
                        showWorkerStatus("ENQUEUED")
                    }

                    WorkInfo.State.RUNNING -> {
                        showWorkerStatus("RUNNING")
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        val successOutputData = workInfo.outputData
                        val firstValue =
                            successOutputData.getString(WorkerExample.OUTPUT_DATA_PARAM1)
                        val secondValue =
                            successOutputData.getInt(WorkerExample.OUTPUT_DATA_PARAM2, -1)

                        showWorkerStatus("SUCCEEDED: Output $firstValue - $secondValue")
                    }
                }
            }
        }

    }

    private fun showWorkerStatus(message: String) {
        binding.activityMainTvStatus.text = message
    }
}