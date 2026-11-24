package com.project.finalmap.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.project.finalmap.BuildConfig
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var loc by remember { mutableStateOf<Location?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    val mapView = remember {
        val opt = MapLibreMapOptions
            .createFromAttributes(context)
            .textureMode(false)

        MapView(context, opt)
    }

    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { p->
        hasPermission = p[Manifest.permission.ACCESS_FINE_LOCATION] == true || p[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if(hasPermission){
            mapLibreMap?.style?.let { s->
                mapLibreMap?.let { m ->
                    enableLocationComponent(context, m, s)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if(!hasPermission){
            permLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            mapLibreMap = map

            map.setStyle(
                Style.Builder().fromUri(
                    "https://api.maptiler.com/maps/streets-v2/style.json?key=${BuildConfig.MAPTILER_API_KEY}"
                )
            ) { style ->
                if (hasPermission) {
                    enableLocationComponent(context, map, style)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        AndroidView(
            factory = {
                mapView
            },
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + slideInVertically { it/2 },
                exit = fadeOut() + slideOutVertically { it/2 }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val comp = mapLibreMap?.locationComponent
                            if (comp != null && comp.isLocationComponentActivated) {
                                loc = comp.lastKnownLocation
                                loc?.let {
                                    mapLibreMap?.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.latitude, it.longitude),
                                            16.0
                                        ),
                                        800
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.MyLocation, null)
                    }
                }
            }

            FloatingActionButton(
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = "Menu Map"
                )
            }
        }
    }
}
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun enableLocationComponent(context: Context, mapLibreMap: MapLibreMap, style: Style){
    val locationComp = mapLibreMap.locationComponent

    locationComp.activateLocationComponent(
        LocationComponentActivationOptions.builder(
            context,
            style
        ).useDefaultLocationEngine(true).build()
    )

    locationComp.isLocationComponentEnabled = true
    locationComp.cameraMode = CameraMode.TRACKING
    locationComp.renderMode = RenderMode.COMPASS

    locationComp.lastKnownLocation?.let { loc ->
        mapLibreMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(loc.latitude, loc.longitude))
                    .zoom(17.0)
                    .tilt(0.0)
                    .build()
            ),
            1000
        )
    }
}
