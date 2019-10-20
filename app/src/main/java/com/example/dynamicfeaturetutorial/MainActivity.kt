package com.example.dynamicfeaturetutorial

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 101
    lateinit var splitInstallManager : SplitInstallManager
//    private val splitInstallManager = SplitInstallManagerFactory.create(this)
    private var mySessionId = 0
    private lateinit var listener : SplitInstallStateUpdatedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        checkDownloadedFeature()
        trackNonDeferredInstallations()
        registerSplitInstallManager()
        startDownloadingFeature()
        unregisterSplitInstallManager()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // User approved installation
            } else {
                // User declined installation
            }
        }
    }

    private fun checkDownloadedFeature() {
        // Check if Module has been Installed or not
        if (splitInstallManager.installedModules.contains("feature01")) {
            // Module is installed
            Log.d("Tutorial","Module Installed")
        } else {
            Log.d("Tutorial","Module isn't Installed")
        }
    }

    private fun startDownloadingFeature() {
        val request = SplitInstallRequest
            .newBuilder()
            .addModule("feature01")
            .build()

        // Starting Module Installation
        splitInstallManager.startInstall(request)
            .addOnSuccessListener {
                mySessionId = it
            }
            .addOnFailureListener {

            }
    }

    private fun cancelDownloadFeature() {
        splitInstallManager.cancelInstall(mySessionId)
    }

    private fun trackNonDeferredInstallations() {
        // Non-deferred Installations Tracking
        listener = SplitInstallStateUpdatedListener {
            when (it.status()) {
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    // Large module requires user permission (> 10MB)
                    // Developer should handle whether user accept or decline the download
                    // Handling in onActivityResult
                    try {
                        splitInstallManager.startConfirmationDialogForResult(
                            it,
                            this@MainActivity, REQUEST_CODE
                        )
                    } catch (ex: IntentSender.SendIntentException) {
                        // Request failed
                    }

                }

                SplitInstallSessionStatus.DOWNLOADING -> {
                    // The module is being downloaded
                }

                SplitInstallSessionStatus.INSTALLING -> {
                    // The downloaded module is being installed
                }

                SplitInstallSessionStatus.DOWNLOADED -> {
                    // The module download is complete
                }

                SplitInstallSessionStatus.INSTALLED -> {
                    // The module has been installed successfully
                }

                SplitInstallSessionStatus.CANCELED -> {
                    // The user cancelled the download
                }

                SplitInstallSessionStatus.PENDING -> {
                    // The installation is deferred
                }
                SplitInstallSessionStatus.FAILED -> {
                    // The installation failed
                }
            }
        }
    }

    private fun registerSplitInstallManager() {
        splitInstallManager.registerListener(listener)
    }

    private fun unregisterSplitInstallManager() {
        splitInstallManager.unregisterListener(listener)
    }

    private fun currentlyInstalledFeatures() : Set<String> {
        return splitInstallManager.installedModules
    }
}
