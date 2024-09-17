package com.example.chargingstations.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chargingstations.domain.model.ChargingStationMedium

@Composable
fun ChargingStationItemView(chargingStation: ChargingStationMedium, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = chargingStation.name,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            color = Color.Gray,
            text = chargingStation.address,
            fontSize = 16.sp
        )
        if (!chargingStation.chargingTypes.isEmpty()) {
            Spacer(modifier = Modifier.size(4.dp))
            Row {
                chargingStation.chargingTypes.forEach {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp, 0.dp)
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}