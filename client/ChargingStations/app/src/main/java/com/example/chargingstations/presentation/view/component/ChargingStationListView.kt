package com.example.chargingstations.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chargingstations.R
import com.example.chargingstations.moveTo
import com.example.chargingstations.presentation.viewmodel.MainActivityViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.Map.CameraCallback
import com.yandex.mapkit.mapview.MapView

@Composable
fun ChargingStationListView(mainActivityViewModel: MainActivityViewModel, mapView: MapView, cameraCallback: CameraCallback) {
    val searchQuery by mainActivityViewModel.searchQuery.collectAsState()
    val filteredChargingStations by mainActivityViewModel.filteredChargingStations.collectAsState()
    val filteredChargingStationsFetched by mainActivityViewModel.filteredChargingStationsFetched.collectAsState()
    val filteredChargingStationsFetching by mainActivityViewModel.filteredChargingStationsFetching.collectAsState()
    val searchProblemIsShown by mainActivityViewModel.searchProblemIsShown.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp)
    ) {
        ChargingStationSearchBar(searchQuery = searchQuery,
            onSearchQueryChanged = { mainActivityViewModel.updateSearchQuery(it) })
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 64.dp),
            modifier = Modifier.fillMaxSize()

        ) {
            item {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(R.string.charging_stations),
                    fontSize = 16.sp
                )
            }
            if (filteredChargingStationsFetching) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            if (filteredChargingStationsFetched) {
                items(filteredChargingStations, key = { it.id }) { station ->
                    ChargingStationItemView(station) {
                        moveTo(
                            mainActivityViewModel,
                            mapView,
                            cameraCallback,
                            station.id,
                            Point(station.latitude, station.longitude))
                    }
                }
            }
            if (searchProblemIsShown) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.charging_station_search_problem_message))
                        Button(onClick = {
                            mainActivityViewModel.tryAgainSearch()
                        }) {
                            Text(text = stringResource(R.string.charging_station_search_problem_button_label))
                        }
                    }
                }
            }
        }
    }
}