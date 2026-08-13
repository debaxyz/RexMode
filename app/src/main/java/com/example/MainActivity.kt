package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.RexViewModel
import com.example.ui.screens.ControlDeckScreen
import com.example.ui.screens.FastApiServerDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PythonCodeViewerDialog
import com.example.ui.theme.RexModeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RexViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RexModeTheme {
                val activeLicense by viewModel.activeLicense.collectAsState()
                val showServerConfigDialog by viewModel.showServerConfigDialog.collectAsState()
                val showPythonCodeDialog by viewModel.showPythonCodeDialog.collectAsState()
                val serverUrlInput by viewModel.serverUrlInput.collectAsState()
                val isFastApiConnected by viewModel.isFastApiConnected.collectAsState()
                val fastApiLatencyMs by viewModel.fastApiLatencyMs.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (activeLicense != null) {
                        ControlDeckScreen(viewModel = viewModel, activeLicense = activeLicense!!)
                    } else {
                        LoginScreen(viewModel = viewModel)
                    }

                    if (showServerConfigDialog) {
                        FastApiServerDialog(
                            currentUrl = serverUrlInput,
                            isConnected = isFastApiConnected,
                            latencyMs = fastApiLatencyMs,
                            onDismiss = { viewModel.setServerConfigDialogVisible(false) },
                            onSaveUrl = { viewModel.updateServerUrl(it) },
                            onTestPing = { viewModel.checkFastApiHealth() },
                            onViewPythonCode = {
                                viewModel.setServerConfigDialogVisible(false)
                                viewModel.setPythonCodeDialogVisible(true)
                            }
                        )
                    }

                    if (showPythonCodeDialog) {
                        PythonCodeViewerDialog(
                            onDismiss = { viewModel.setPythonCodeDialogVisible(false) }
                        )
                    }
                }
            }
        }
    }
}
