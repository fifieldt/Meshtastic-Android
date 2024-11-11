package com.geeksville.mesh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PermScanWifi
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.geeksville.mesh.R
import com.geeksville.mesh.android.Logging
import com.geeksville.mesh.database.entity.NodeEntity
import com.geeksville.mesh.model.MetricsViewModel
import com.geeksville.mesh.model.RadioConfigViewModel
import com.geeksville.mesh.ui.components.DeviceMetricsScreen
import com.geeksville.mesh.ui.components.EnvironmentMetricsScreen
import com.geeksville.mesh.ui.components.NodeMapScreen
import com.geeksville.mesh.ui.components.PositionLogScreen
import com.geeksville.mesh.ui.components.SignalMetricsScreen
import com.geeksville.mesh.ui.components.TracerouteLogScreen
import com.geeksville.mesh.ui.components.config.AmbientLightingConfigScreen
import com.geeksville.mesh.ui.components.config.AudioConfigScreen
import com.geeksville.mesh.ui.components.config.BluetoothConfigScreen
import com.geeksville.mesh.ui.components.config.CannedMessageConfigScreen
import com.geeksville.mesh.ui.components.config.ChannelConfigScreen
import com.geeksville.mesh.ui.components.config.DetectionSensorConfigScreen
import com.geeksville.mesh.ui.components.config.DeviceConfigScreen
import com.geeksville.mesh.ui.components.config.DisplayConfigScreen
import com.geeksville.mesh.ui.components.config.ExternalNotificationConfigScreen
import com.geeksville.mesh.ui.components.config.LoRaConfigScreen
import com.geeksville.mesh.ui.components.config.MQTTConfigScreen
import com.geeksville.mesh.ui.components.config.NeighborInfoConfigScreen
import com.geeksville.mesh.ui.components.config.NetworkConfigScreen
import com.geeksville.mesh.ui.components.config.PaxcounterConfigScreen
import com.geeksville.mesh.ui.components.config.PositionConfigScreen
import com.geeksville.mesh.ui.components.config.PowerConfigScreen
import com.geeksville.mesh.ui.components.config.RangeTestConfigScreen
import com.geeksville.mesh.ui.components.config.RemoteHardwareConfigScreen
import com.geeksville.mesh.ui.components.config.SecurityConfigScreen
import com.geeksville.mesh.ui.components.config.SerialConfigScreen
import com.geeksville.mesh.ui.components.config.StoreForwardConfigScreen
import com.geeksville.mesh.ui.components.config.TelemetryConfigScreen
import com.geeksville.mesh.ui.components.config.UserConfigScreen
import com.google.accompanist.themeadapter.appcompat.AppCompatTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

internal fun FragmentManager.navigateToNavGraph(
    destNum: Int? = null,
    startDestination: String = "RadioConfig",
) {
    val radioConfigFragment = NavGraphFragment().apply {
        arguments = bundleOf("destNum" to destNum, "startDestination" to startDestination)
    }
    beginTransaction()
        .replace(R.id.mainActivityLayout, radioConfigFragment)
        .addToBackStack(null)
        .commit()
}

@AndroidEntryPoint
class NavGraphFragment : ScreenFragment("NavGraph"), Logging {

    private val model: RadioConfigViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val destNum = arguments?.getInt("destNum")
        val startDestination: Any = when (arguments?.getString("startDestination")) {
            "NodeDetails" -> Route.NodeDetail(destNum!!)
            else -> Route.RadioConfig(destNum)
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorAdvancedBackground))
            setContent {
                val node by model.destNode.collectAsStateWithLifecycle()

                AppCompatTheme {
                    val navController: NavHostController = rememberNavController()
                    Scaffold(
                        topBar = {
                            MeshAppBar(
                                currentScreen = node?.user?.longName
                                    ?: stringResource(R.string.unknown_username),
                                canNavigateBack = true,
                                navigateUp = {
                                    if (navController.previousBackStackEntry != null) {
                                        navController.navigateUp()
                                    } else {
                                        parentFragmentManager.popBackStack()
                                    }
                                },
                            )
                        }
                    ) { innerPadding ->
                        NavGraph(
                            node = node,
                            viewModel = model,
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}

enum class AdminRoute(@StringRes val title: Int) {
    REBOOT(R.string.reboot),
    SHUTDOWN(R.string.shutdown),
    FACTORY_RESET(R.string.factory_reset),
    NODEDB_RESET(R.string.nodedb_reset),
}

sealed interface Route {
    @Serializable
    data class RadioConfig(val destNum: Int? = null) : Route
    @Serializable data object User : Route
    @Serializable data object Channels : Route
    @Serializable data object Device : Route
    @Serializable data object Position : Route
    @Serializable data object Power : Route
    @Serializable data object Network : Route
    @Serializable data object Display : Route
    @Serializable data object LoRa : Route
    @Serializable data object Bluetooth : Route
    @Serializable data object Security : Route

    @Serializable data object MQTT : Route
    @Serializable data object Serial : Route
    @Serializable data object ExtNotification : Route
    @Serializable data object StoreForward : Route
    @Serializable data object RangeTest : Route
    @Serializable data object Telemetry : Route
    @Serializable data object CannedMessage : Route
    @Serializable data object Audio : Route
    @Serializable data object RemoteHardware : Route
    @Serializable data object NeighborInfo : Route
    @Serializable data object AmbientLighting : Route
    @Serializable data object DetectionSensor : Route
    @Serializable data object Paxcounter : Route

    @Serializable
    data class NodeDetail(val destNum: Int) : Route
    @Serializable data object DeviceMetrics : Route
    @Serializable data object NodeMap : Route
    @Serializable data object PositionLog : Route
    @Serializable data object EnvironmentMetrics : Route
    @Serializable data object SignalMetrics : Route
    @Serializable data object TracerouteLog : Route
}

// Config (type = AdminProtos.AdminMessage.ConfigType)
enum class ConfigRoute(val title: String, val route: Route, val icon: ImageVector?, val type: Int = 0) {
    USER("User", Route.User, Icons.Default.Person, 0),
    CHANNELS("Channels", Route.Channels, Icons.AutoMirrored.Default.List, 0),
    DEVICE("Device", Route.Device, Icons.Default.Router, 0),
    POSITION("Position", Route.Position, Icons.Default.LocationOn, 1),
    POWER("Power", Route.Power, Icons.Default.Power, 2),
    NETWORK("Network", Route.Network, Icons.Default.Wifi, 3),
    DISPLAY("Display", Route.Display, Icons.Default.DisplaySettings, 4),
    LORA("LoRa", Route.LoRa, Icons.Default.CellTower, 5),
    BLUETOOTH("Bluetooth", Route.Bluetooth, Icons.Default.Bluetooth, 6),
    SECURITY("Security", Route.Security, Icons.Default.Security, type = 7),
}

// ModuleConfig (type = AdminProtos.AdminMessage.ModuleConfigType)
enum class ModuleRoute(val title: String, val route: Route, val icon: ImageVector?, val type: Int = 0) {
    MQTT("MQTT", Route.MQTT, Icons.Default.Cloud, 0),
    SERIAL("Serial", Route.Serial, Icons.Default.Usb, 1),
    EXT_NOTIFICATION("External Notification", Route.ExtNotification, Icons.Default.Notifications, 2),
    STORE_FORWARD("Store & Forward", Route.StoreForward, Icons.AutoMirrored.Default.Forward, 3),
    RANGE_TEST("Range Test", Route.RangeTest, Icons.Default.Speed, 4),
    TELEMETRY("Telemetry", Route.Telemetry, Icons.Default.DataUsage, 5),
    CANNED_MESSAGE("Canned Message", Route.CannedMessage, Icons.AutoMirrored.Default.Message, 6),
    AUDIO("Audio", Route.Audio, Icons.AutoMirrored.Default.VolumeUp, 7),
    REMOTE_HARDWARE("Remote Hardware", Route.RemoteHardware, Icons.Default.SettingsRemote, 8),
    NEIGHBOR_INFO("Neighbor Info", Route.NeighborInfo, Icons.Default.People, 9),
    AMBIENT_LIGHTING("Ambient Lighting", Route.AmbientLighting, Icons.Default.LightMode, 10),
    DETECTION_SENSOR("Detection Sensor", Route.DetectionSensor, Icons.Default.Sensors, 11),
    PAXCOUNTER("Paxcounter", Route.Paxcounter, Icons.Default.PermScanWifi, 12),
}

/**
 * Generic sealed class defines each possible state of a response.
 */
sealed class ResponseState<out T> {
    data object Empty : ResponseState<Nothing>()
    data class Loading(var total: Int = 1, var completed: Int = 0) : ResponseState<Nothing>()
    data class Success<T>(val result: T) : ResponseState<T>()
    data class Error(val error: String) : ResponseState<Nothing>()

    fun isWaiting() = this !is Empty
}

@Composable
private fun MeshAppBar(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(currentScreen) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_back),
                    )
                }
            }
        }
    )
}

@Suppress("LongMethod")
@Composable
fun NavGraph(
    node: NodeEntity?,
    viewModel: RadioConfigViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    startDestination: Any,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Route.NodeDetail> {
            NodeDetailScreen(
                node = node,
            ) { navController.navigate(route = it) }
        }
        composable<Route.DeviceMetrics> {
            val parentEntry = remember { navController.getBackStackEntry<Route.NodeDetail>() }
            DeviceMetricsScreen(hiltViewModel<MetricsViewModel>(parentEntry))
        }
        composable<Route.NodeMap> {
            val parentEntry = remember { navController.getBackStackEntry<Route.NodeDetail>() }
            NodeMapScreen(hiltViewModel<MetricsViewModel>(parentEntry))
        }
        composable<Route.PositionLog> {
            val parentEntry = remember { navController.getBackStackEntry<Route.NodeDetail>() }
            PositionLogScreen(hiltViewModel<MetricsViewModel>(parentEntry))
        }
        composable<Route.EnvironmentMetrics> {
            val parentEntry = remember { navController.getBackStackEntry<Route.NodeDetail>() }
            EnvironmentMetricsScreen(hiltViewModel<MetricsViewModel>(parentEntry))
        }
        composable<Route.SignalMetrics> {
            val parentEntry = remember { navController.getBackStackEntry<Route.NodeDetail>() }
            SignalMetricsScreen(hiltViewModel<MetricsViewModel>(parentEntry))
        }
        composable<Route.TracerouteLog> {
            val parentEntry = remember { navController.getBackStackEntry<Route.NodeDetail>() }
            TracerouteLogScreen(hiltViewModel<MetricsViewModel>(parentEntry))
        }
        composable<Route.RadioConfig> {
            RadioConfigScreen(
                node = node,
                viewModel = viewModel,
            ) { navController.navigate(route = it) }
        }
        composable<Route.User> {
            UserConfigScreen(viewModel)
        }
        composable<Route.Channels> {
            ChannelConfigScreen(viewModel)
        }
        composable<Route.Device> {
            DeviceConfigScreen(viewModel)
        }
        composable<Route.Position> {
            PositionConfigScreen(viewModel)
        }
        composable<Route.Power> {
            PowerConfigScreen(viewModel)
        }
        composable<Route.Network> {
            NetworkConfigScreen(viewModel)
        }
        composable<Route.Display> {
            DisplayConfigScreen(viewModel)
        }
        composable<Route.LoRa> {
            LoRaConfigScreen(viewModel)
        }
        composable<Route.Bluetooth> {
            BluetoothConfigScreen(viewModel)
        }
        composable<Route.Security> {
            SecurityConfigScreen(viewModel)
        }
        composable<Route.MQTT> {
            MQTTConfigScreen(viewModel)
        }
        composable<Route.Serial> {
            SerialConfigScreen(viewModel)
        }
        composable<Route.ExtNotification> {
            ExternalNotificationConfigScreen(viewModel)
        }
        composable<Route.StoreForward> {
            StoreForwardConfigScreen(viewModel)
        }
        composable<Route.RangeTest> {
            RangeTestConfigScreen(viewModel)
        }
        composable<Route.Telemetry> {
            TelemetryConfigScreen(viewModel)
        }
        composable<Route.CannedMessage> {
            CannedMessageConfigScreen(viewModel)
        }
        composable<Route.Audio> {
            AudioConfigScreen(viewModel)
        }
        composable<Route.RemoteHardware> {
            RemoteHardwareConfigScreen(viewModel)
        }
        composable<Route.NeighborInfo> {
            NeighborInfoConfigScreen(viewModel)
        }
        composable<Route.AmbientLighting> {
            AmbientLightingConfigScreen(viewModel)
        }
        composable<Route.DetectionSensor> {
            DetectionSensorConfigScreen(viewModel)
        }
        composable<Route.Paxcounter> {
            PaxcounterConfigScreen(viewModel)
        }
    }
}