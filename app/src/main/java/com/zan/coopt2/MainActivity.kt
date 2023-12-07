package com.zan.coopt2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.zan.coopt2.Helper.ConnectionStatus
import com.zan.coopt2.Helper.currentConnectivityStatus
import com.zan.coopt2.Helper.observeConnectivityAsFlow
import com.zan.coopt2.Screens.AvailableScreen
import com.zan.coopt2.Screens.UnavailableScreen
//added
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager

/**
 * The main activity for the application. It sets up the Composable UI and checks connectivity status.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            checkConnectivityStatus()
        }
    }
}

/**
 * A Composable function to check the name of the Wi-Fi network connection.
 */

@Composable
fun currentWifiNetworkName(): State<String> {
    val currentContext = LocalContext.current

    return produceState(initialValue = "") {
        val wifiManager = currentContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        val networkName = wifiInfo?.ssid ?: ""
        value = networkName.removeSurrounding("\"") // Removes surrounding quotes from SSID if present
    }
}

/**
 * A Composable function to check the connectivity status and display the appropriate screen.
 */
@Composable
fun checkConnectivityStatus() {
    val connection by connectivityStatus()

    val isConnected = connection === ConnectionStatus.Available

    if (isConnected) {
        val network = currentWifiNetworkName()
        AvailableScreen(network.value)
    } else {
        UnavailableScreen()
    }
}

/**
 * A Composable function that provides the current connectivity status as a State object.
 * @return A State object containing the current connection status.
 */
@Composable
fun connectivityStatus(): State<ConnectionStatus> {
    val currentContext = LocalContext.current

    return produceState(initialValue = currentContext.currentConnectivityStatus) {
        currentContext.observeConnectivityAsFlow().collect { value = it }
    }
}

