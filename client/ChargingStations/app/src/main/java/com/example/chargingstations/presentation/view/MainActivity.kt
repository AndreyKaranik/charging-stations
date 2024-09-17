package com.example.chargingstations.presentation.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.chargingstations.presentation.theme.ChargingStationsTheme
import com.example.chargingstations.presentation.viewmodel.MainActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import com.example.chargingstations.MyCameraListener
import com.example.chargingstations.R
import com.example.chargingstations.createBitmapFromVector
import com.example.chargingstations.isNetworkAvailable
import com.example.chargingstations.moveTo
import com.example.chargingstations.presentation.view.component.BasicIconButton
import com.example.chargingstations.presentation.view.component.BasicIconButtonWithProgress
import com.example.chargingstations.presentation.view.component.ChargingStationDetailsView
import com.example.chargingstations.presentation.view.component.ChargingStationListView
import com.example.chargingstations.presentation.view.component.ChargingStationNotFoundDialog
import com.example.chargingstations.presentation.view.component.ConnectionProblemDialog
import com.example.chargingstations.presentation.view.component.GPSDialog
import com.example.chargingstations.presentation.view.component.IncorrectQRCodeDialog
import com.example.chargingstations.presentation.view.component.NoInternetConnectionDialog

class MainActivity : ComponentActivity() {
    private lateinit var mapView: MapView
    private lateinit var cameraListener: MyCameraListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var cameraCallback: Map.CameraCallback

    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var qrScannerActivityResultLauncher: ActivityResultLauncher<Intent>


    private val TAG: String = "MainActivity"
    private val placemarkMapObjectList = mutableListOf<PlacemarkMapObject>()
    private val tapListeners = mutableListOf<MapObjectTapListener>()


    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("83258943-5e6f-4d87-b448-45553225d7e4")
        MapKitFactory.initialize(this)
        mapView = MapView(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        cameraListener =
            MyCameraListener(this, placemarkMapObjectList, mapView.map.cameraPosition.zoom)
        mapView.map.addCameraListener(cameraListener)

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!mainActivityViewModel.chargingStationsFetched.value) {
                    mainActivityViewModel.hideNoInternetConnectionDialog()
                    mainActivityViewModel.fetchChargingStations()
                }
            }

            override fun onLost(network: Network) {

            }

            override fun onCapabilitiesChanged(
                network: Network, networkCapabilities: NetworkCapabilities
            ) {

            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {

            }
        })

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            Log.d(TAG, "locationPermissionRequest")
            if (permissions.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                ) && permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            ) {
                val manager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mainActivityViewModel.showGPSDialog()
                } else {
                    move()
                }
            } else {
                Log.w(TAG, "permission problem")
            }
        }

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            move()
        }

        qrScannerActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val code = it.data?.getStringExtra("code")
                if (code != null) {
                    val regex = "ChargingStationId=([0-9])+".toRegex()
                    if (regex.containsMatchIn(code)) {
                        val pair = code.split('=')
                        val chargingStation =
                            mainActivityViewModel.getChargingStationById(pair[1].toInt())
                        if (chargingStation != null) {
                            moveTo(
                                mainActivityViewModel,
                                mapView,
                                cameraCallback,
                                chargingStation.id,
                                Point(chargingStation.latitude, chargingStation.longitude)
                            )
                        } else {
                            mainActivityViewModel.showChargingStationNotFoundDialogIsShown()
                        }
                    } else {
                        mainActivityViewModel.showIncorrectQRCodeDialogIsShown()
                    }
                }
            }
        }

        cameraCallback = Map.CameraCallback {
            // Handle camera move finished ...
        }

        mapView.map.move(
            CameraPosition(Point(55.751244, 37.618423), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            cameraCallback
        )

        setContent {
            ChargingStationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val chargingStationsFetched by mainActivityViewModel.chargingStationsFetched.collectAsState()
                    val chargingStationsFetching by mainActivityViewModel.chargingStationsFetching.collectAsState()
                    val gpsProgressIndicatorIsShown by mainActivityViewModel.gpsProgressIndicatorIsShown.collectAsState()
                    val gpsDialogIsShown by mainActivityViewModel.gpsDialogIsShown.collectAsState()
                    val noInternetConnectionDialogIsShown by mainActivityViewModel.noInternetConnectionDialogIsShown.collectAsState()
                    val incorrectQRCodeDialogIsShown by mainActivityViewModel.incorrectQRCodeDialogIsShown.collectAsState()
                    val chargingStationNotFoundDialogIsShown by mainActivityViewModel.chargingStationNotFoundDialogIsShown.collectAsState()
                    val connectionProblemDialogIsShown by mainActivityViewModel.connectionProblemDialogIsShown.collectAsState()

                    val chargingStationDetailsSheetIsShown by mainActivityViewModel.chargingStationDetailsSheetIsShown.collectAsState()
                    var searchSheetIsShown by remember { mutableStateOf(false) }
                    val accountSheetIsShown by mainActivityViewModel.accountSheetIsShown.collectAsState()

                    if (chargingStationsFetched && placemarkMapObjectList.isEmpty()) {
                        addMarkers()
                    }

                    when {
                        gpsDialogIsShown -> {
                            GPSDialog(
                                onDismissRequest = {
                                    mainActivityViewModel.hideGPSDialog()
                                }, onConfirmRequest = {
                                    mainActivityViewModel.hideGPSDialog()
                                    activityResultLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                }
                            )
                        }

                        incorrectQRCodeDialogIsShown -> {
                            IncorrectQRCodeDialog(
                                onDismissRequest = {
                                    mainActivityViewModel.hideIncorrectQRCodeDialogIsShown()
                                }, onConfirmRequest = {
                                    mainActivityViewModel.hideIncorrectQRCodeDialogIsShown()
                                }
                            )
                        }

                        noInternetConnectionDialogIsShown -> {
                            NoInternetConnectionDialog()
                        }

                        chargingStationNotFoundDialogIsShown -> {
                            ChargingStationNotFoundDialog(
                                onDismissRequest = {
                                    mainActivityViewModel.hideChargingStationNotFoundDialogIsShown()
                                }, onConfirmRequest = {
                                    mainActivityViewModel.hideChargingStationNotFoundDialogIsShown()
                                }
                            )
                        }

                        connectionProblemDialogIsShown -> {
                            ConnectionProblemDialog(
                                onDismissRequest = {},
                                onConfirmRequest = {
                                    mainActivityViewModel.hideConnectionProblemDialog()
                                    mainActivityViewModel.fetchChargingStations()
                                }
                            )
                        }

                        else -> {

                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!chargingStationsFetched) {
                            if (!noInternetConnectionDialogIsShown && !connectionProblemDialogIsShown) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                            if (!chargingStationsFetching) {
                                if (!isNetworkAvailable(this@MainActivity)) {
                                    mainActivityViewModel.showNoInternetConnectionDialog()
                                } else {
                                    mainActivityViewModel.showConnectionProblemDialog()
                                }
                            }
                        } else {
                            ChargingStationsMap()
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                BasicIconButton(
                                    onClick = {
                                        val sharedPref =
                                            getSharedPreferences("myPrefs", MODE_PRIVATE)
                                        val auth = sharedPref.getBoolean("auth", false)
                                        if (!auth) {
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    AuthenticationActivity::class.java
                                                )
                                            )
                                        } else {
                                            mainActivityViewModel.showAccountSheet()
                                        }
                                    },
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_account_circle_24)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                                    BasicIconButton(
                                        onClick = {
                                            mapView.map.apply {
                                                move(
                                                    CameraPosition(
                                                        cameraPosition.target,
                                                        cameraPosition.zoom + 1,
                                                        cameraPosition.azimuth,
                                                        cameraPosition.tilt
                                                    ),
                                                    Animation(Animation.Type.SMOOTH, 0.25f),
                                                    cameraCallback
                                                )
                                            }
                                        },
                                        imageVector = ImageVector.vectorResource(R.drawable.baseline_add_24)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    BasicIconButton(
                                        onClick = {
                                            mapView.map.apply {
                                                move(
                                                    CameraPosition(
                                                        cameraPosition.target,
                                                        cameraPosition.zoom - 1,
                                                        cameraPosition.azimuth,
                                                        cameraPosition.tilt
                                                    ),
                                                    Animation(Animation.Type.SMOOTH, 0.25f),
                                                    cameraCallback
                                                )
                                            }
                                        },
                                        imageVector = ImageVector.vectorResource(R.drawable.baseline_remove_24)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(0.dp, 64.dp)
                                ) {
                                    BasicIconButton(
                                        onClick = {
                                            searchSheetIsShown = true
                                        }, imageVector = Icons.Default.Search
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    BasicIconButton(
                                        onClick = {
                                            qrScannerActivityResultLauncher.launch(
                                                Intent(
                                                    this@MainActivity, QRScannerActivity::class.java
                                                )
                                            )
                                        },
                                        imageVector = ImageVector.vectorResource(R.drawable.baseline_qr_code_2_24)
                                    )
                                    Spacer(modifier = Modifier.size(32.dp))
                                    BasicIconButtonWithProgress(
                                        onClick = {
                                            requestPermissions()
                                        },
                                        imageVector = ImageVector.vectorResource(R.drawable.round_near_me_24),
                                        gpsProgressIndicatorIsShown
                                    )
                                }
                            }
                            if (chargingStationDetailsSheetIsShown) {
                                searchSheetIsShown = false
                                ChargingStationDetailsSheet(onDismissRequest = {
                                    mainActivityViewModel.hideChargingStationDetailsSheet()
                                })
                            }
                            if (searchSheetIsShown) {
                                SearchSheet(onDismissRequest = {
                                    searchSheetIsShown = false
                                })
                            }
                            if (accountSheetIsShown) {
                                AccountSheet(onDismissRequest = {
                                    mainActivityViewModel.hideAccountSheet()
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchSheet(onDismissRequest: () -> Unit) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        ModalBottomSheet(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 32.dp),
            tonalElevation = 0.dp,
            sheetState = sheetState,
            onDismissRequest = { onDismissRequest() }) {
            ChargingStationListView(mainActivityViewModel, mapView, cameraCallback)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AccountSheet(onDismissRequest: () -> Unit) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        val email by mainActivityViewModel.email.collectAsState()

        ModalBottomSheet(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 32.dp),
            tonalElevation = 0.dp,
            sheetState = sheetState,
            onDismissRequest = { onDismissRequest() }) {
            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Вы авторизованы"
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "почта: $email"
                )
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            AuthenticationActivity::class.java
                        )
                    )
                }) {
                    Text("Войти в другой аккаунт")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChargingStationDetailsSheet(onDismissRequest: () -> Unit) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )

        ModalBottomSheet(modifier = Modifier
            .fillMaxHeight()
            .padding(top = 32.dp),
            tonalElevation = 0.dp,
            scrimColor = Color.Transparent,
            sheetState = sheetState,
            onDismissRequest = { onDismissRequest() }) {
            ChargingStationDetailsView(
                this@MainActivity,
                mainActivityViewModel
            )
        }
    }

    private fun move() {
        if (mainActivityViewModel.gpsProgressIndicatorIsShown.value) {
            mainActivityViewModel.hideGPSProgressIndicator()
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "You don't have the permissions to get last known location.")
            return
        }

        val manager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w(TAG, "GPS is disabled.")
            return
        }

        mainActivityViewModel.showGPSProgressIndicator()

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                mapView.map.move(
                    CameraPosition(
                        Point(location.latitude, location.longitude),
                        15.0f,
                        mapView.map.cameraPosition.azimuth,
                        mapView.map.cameraPosition.tilt
                    ), Animation(Animation.Type.SMOOTH, 1f), cameraCallback
                )
                mainActivityViewModel.hideGPSProgressIndicator()
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    move()
                }
            }
        }
    }

    private fun requestPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @Composable
    fun ChargingStationsMap() {
        AndroidView(factory = {
            mapView
        })
    }

    private fun addMarkers() {
        val marker = createBitmapFromVector(this, R.drawable.baseline_circle_24)
        val imageProvider = ImageProvider.fromBitmap(marker)

        val points = mutableListOf<Pair<Int, Point>>()
        mainActivityViewModel.chargingStations.value.forEach {
            points.add(it.id to Point(it.latitude, it.longitude))
        }

        points.forEach { pair ->
            val placemarkMapObject = mapView.map.mapObjects.addPlacemark().apply {
                geometry = pair.second
                setIcon(imageProvider)
                userData = title
            }
            val listener = MapObjectTapListener { _, _ ->
                moveTo(mainActivityViewModel, mapView, cameraCallback, pair.first, pair.second)
                true
            }
            placemarkMapObject.addTapListener(listener)
            tapListeners.add(listener)
            placemarkMapObjectList.add(placemarkMapObject)
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onResume() {
        val sharedPref =
            getSharedPreferences("myPrefs", MODE_PRIVATE)
        mainActivityViewModel.setToken(sharedPref.getString("token", null))
        mainActivityViewModel.setEmail(sharedPref.getString("email", null))
        super.onResume()
    }
}