package com.example.chargingstations.presentation.view.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.chargingstations.R

@Composable
fun ChargingStationSearchBar(searchQuery: String, onSearchQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        singleLine = true,
        onValueChange = onSearchQueryChanged,
        placeholder = {
            Text(
                text = stringResource(R.string.charging_station_search_bar_placeholder),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        label = {
            Text(
                text = stringResource(R.string.charging_station_search_bar_label)
            )
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (searchQuery != "") {
                IconButton(onClick = {
                    onSearchQueryChanged("")
                }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}