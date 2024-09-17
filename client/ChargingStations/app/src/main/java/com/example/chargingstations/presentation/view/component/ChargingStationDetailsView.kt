package com.example.chargingstations.presentation.view.component

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chargingstations.R
import com.example.chargingstations.presentation.theme.Gray1
import com.example.chargingstations.presentation.theme.Gray2
import com.example.chargingstations.presentation.theme.Green1
import com.example.chargingstations.presentation.view.OrderActivity
import com.example.chargingstations.presentation.viewmodel.MainActivityViewModel

@Composable
fun ChargingStationDetailsView(
    context: Context,
    mainActivityViewModel: MainActivityViewModel
) {
    val chargingStationDetails by mainActivityViewModel.chargingStationDetails.collectAsState()
    val chargingStationImageBitmap by mainActivityViewModel.chargingStationImageBitmap.collectAsState()
    val cSDetailsFetchProblemIsShown by mainActivityViewModel.cSDetailsFetchProblemIsShown.collectAsState()
    if (chargingStationDetails != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (chargingStationImageBitmap != null) {
                        Image(
                            modifier = Modifier
                                .size(256.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            bitmap = chargingStationImageBitmap!!,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            tint = Color.Gray,
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_image_24),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Column {
                    Text(
                        text = chargingStationDetails!!.name,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Text(
                        text = chargingStationDetails!!.address,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp, 0.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterVertically),
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_access_time_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = chargingStationDetails!!.openingHours,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.charging_station_details_connectors_title),
                fontSize = 16.sp,
                color = Color.LightGray
            )
            if (chargingStationDetails!!.connectors.isEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.charging_station_details_no_connectors),
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            } else {
                chargingStationDetails!!.connectors.forEach {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Gray1, shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 0.5.dp, color = Gray2, shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row {
                            Column {
                                Text(
                                    text = it.chargingType.name,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = it.rate.toInt().toString() + " kW/h",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    val connectorStatusName: Int
                                    val connectorStatusColor: Color
                                    when (it.status) {
                                        1 -> {
                                            connectorStatusName =
                                                R.string.charging_station_details_connector_active
                                            connectorStatusColor = Green1
                                        }

                                        2 -> {
                                            connectorStatusName =
                                                R.string.charging_station_details_connector_occupied
                                            connectorStatusColor = Color.Yellow
                                        }

                                        else -> {
                                            connectorStatusName =
                                                R.string.charging_station_details_connector_inactive
                                            connectorStatusColor = Color.Red
                                        }
                                    }

                                    if (it.status == 1) {
                                        Button(
                                            onClick = {
                                                val bundle = Bundle()
                                                bundle.putInt("connector_id", it.id)
                                                bundle.putInt("charging_station_id", it.chargingStationId)
                                                bundle.putInt("charging_type_id", it.chargingType.id)
                                                bundle.putString("charging_station_address", chargingStationDetails!!.address)
                                                bundle.putString("charging_type_name", it.chargingType.name)
                                                bundle.putFloat("connector_rate", it.rate)
                                                bundle.putString("token", mainActivityViewModel.token.value)
                                                val intent =
                                                    Intent(context, OrderActivity::class.java)
                                                intent.putExtras(bundle)
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Text(text = stringResource(R.string.charging_station_details_connector_button_label))
                                        }
                                    }
                                    Text(
                                        text = stringResource(connectorStatusName),
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                connectorStatusColor,
                                                shape = RoundedCornerShape(percent = 50)
                                            )
                                            .size(12.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color.LightGray,
                                                shape = RoundedCornerShape(percent = 50)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.charging_station_details_description_title),
                fontSize = 16.sp,
                color = Color.LightGray
            )


            if (chargingStationDetails!!.description == null) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.charging_station_details_no_description),
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            } else {
                Box(
                    modifier = Modifier
                        .background(
                            color = Gray1, shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 64.dp)
                        .padding(8.dp)
                ) {
                    Text(
                        text = chargingStationDetails!!.description!!,
                        fontSize = 14.sp,
                        color = Color.Black,
                    )
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.charging_station_details_marks_title),
                fontSize = 16.sp,
                color = Color.LightGray
            )
            if (chargingStationDetails!!.chargingMarksWithUserName.isEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.charging_station_details_no_marks),
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            } else {
                chargingStationDetails!!.chargingMarksWithUserName.forEach {
                    Box(
                        modifier = Modifier
                            .background(
                                Gray1, shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column {
                            if (it.userId != null) {
                                Text(
                                    text = it.userName!!,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.charging_station_details_anonymous),
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                            if (it.status == 1) {
                                Text(
                                    text = stringResource(R.string.charging_station_details_mark_success),
                                    fontSize = 12.sp,
                                    color = Green1
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.charging_station_details_mark_failed),
                                    fontSize = 12.sp,
                                    color = Color.Red
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(8.dp, 0.dp)
                            ) {
                                Text(
                                    text = it.chargingType.name,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = it.time,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(32.dp))
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            if (cSDetailsFetchProblemIsShown) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.charging_station_details_fetch_problem_message)
                    )
                    Button(
                        onClick = {
                            mainActivityViewModel.tryAgainFetchCSDetails()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.charging_station_details_fetch_problem_button_label)
                        )
                    }
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}

//@Preview
//@Composable
//fun ChargingStationDetailsPreview() {
//    ChargingStationsTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
//        ) {
//            val connectors = listOf<ConnectorDetails>(
//                ConnectorDetails(0, 1, 0, ChargingType(0, "TYPE 2", "AC"), 22.0),
//                ConnectorDetails(1, 1, 1, ChargingType(0, "GB/T", "DC"), 65.0)
//            )
//            val marks = listOf<ChargingMarkWithUserName>(
//                ChargingMarkWithUserName(
//                    0,
//                    1,
//                    0,
//                    1,
//                    "John",
//                    ChargingType(0, "TYPE 2", "AC"),
//                    "12:12:12"
//                ),
//                ChargingMarkWithUserName(
//                    1,
//                    1,
//                    1,
//                    null,
//                    null,
//                    ChargingType(0, "GB/T", "DC"),
//                    "12:12:12"
//                )
//            )
//            val chargingStationImageBitmap = null
//            val cSDetailsFetchProblemIsShown = false
//            val chargingStationDetails = ChargingStationDetails(
//                0,
//                "SuperCharger",
//                "ул. Иванова, Москва",
//                0.0,
//                0.0,
//                0,
//                "8-22",
//                "Отличная зарядная станция в Москве не улице Иванова.",
//                connectors,
//                marks,
//                emptyList()
//            )
//            Column(
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Box(
//                        modifier = Modifier
//                            .size(96.dp)
//                            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        if (chargingStationImageBitmap != null) {
//                            Image(
//                                modifier = Modifier
//                                    .size(256.dp)
//                                    .clip(RoundedCornerShape(16.dp)),
//                                bitmap = chargingStationImageBitmap!!,
//                                contentDescription = null,
//                                contentScale = ContentScale.Crop
//                            )
//                        } else {
//                            Icon(
//                                modifier = Modifier.size(32.dp),
//                                tint = Color.Gray,
//                                imageVector = ImageVector.vectorResource(R.drawable.baseline_image_24),
//                                contentDescription = null
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.size(16.dp))
//                    Column {
//                        Text(
//                            text = chargingStationDetails!!.name,
//                            fontSize = 24.sp,
//                            color = Color.Black
//                        )
//                        Text(
//                            text = chargingStationDetails!!.address,
//                            fontSize = 16.sp,
//                            color = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.size(4.dp))
//                        Row(
//                            modifier = Modifier
//                                .background(
//                                    MaterialTheme.colorScheme.primary,
//                                    shape = RoundedCornerShape(8.dp)
//                                )
//                                .padding(4.dp, 0.dp)
//                        ) {
//                            Icon(
//                                modifier = Modifier
//                                    .size(20.dp)
//                                    .align(Alignment.CenterVertically),
//                                imageVector = ImageVector.vectorResource(R.drawable.baseline_access_time_24),
//                                contentDescription = null,
//                                tint = Color.White
//                            )
//                            Spacer(modifier = Modifier.size(2.dp))
//                            Text(
//                                text = chargingStationDetails!!.openingHours,
//                                fontSize = 12.sp,
//                                color = Color.White
//                            )
//                        }
//                    }
//                }
//                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
//                Text(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    text = stringResource(R.string.charging_station_details_connectors_title),
//                    fontSize = 16.sp,
//                    color = Color.LightGray
//                )
//                if (chargingStationDetails!!.connectors.isEmpty()) {
//                    Text(
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        text = stringResource(R.string.charging_station_details_no_connectors),
//                        fontSize = 20.sp,
//                        color = Color.Gray
//                    )
//                } else {
//                    chargingStationDetails!!.connectors.forEach {
//                        Box(
//                            modifier = Modifier
//                                .background(
//                                    color = Gray1, shape = RoundedCornerShape(8.dp)
//                                )
//                                .border(
//                                    width = 0.5.dp, color = Gray2, shape = RoundedCornerShape(8.dp)
//                                )
//                                .fillMaxWidth()
//                                .padding(8.dp)
//                        ) {
//                            Row {
//                                Column {
//                                    Text(
//                                        text = it.chargingType.name,
//                                        fontSize = 16.sp,
//                                        color = Color.Black
//                                    )
//                                    Text(
//                                        text = it.rate.toInt().toString() + " kW/h",
//                                        fontSize = 14.sp,
//                                        color = Color.Gray
//                                    )
//                                }
//
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .align(Alignment.CenterVertically),
//                                    contentAlignment = Alignment.CenterEnd
//                                ) {
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
//                                    ) {
//                                        val connectorStatusName: Int
//                                        val connectorStatusColor: Color
//                                        when (it.status) {
//                                            1 -> {
//                                                connectorStatusName =
//                                                    R.string.charging_station_details_connector_active
//                                                connectorStatusColor = Green1
//                                            }
//
//                                            2 -> {
//                                                connectorStatusName =
//                                                    R.string.charging_station_details_connector_occupied
//                                                connectorStatusColor = Color.Yellow
//                                            }
//
//                                            else -> {
//                                                connectorStatusName =
//                                                    R.string.charging_station_details_connector_inactive
//                                                connectorStatusColor = Color.Red
//                                            }
//                                        }
//
//                                        if (it.status == 1) {
//                                            Button(onClick = { /*TODO*/ }) {
//                                                Text(text = stringResource(R.string.charging_station_details_connector_button_label))
//                                            }
//                                        }
//                                        Text(
//                                            text = stringResource(connectorStatusName),
//                                            fontSize = 14.sp,
//                                            color = Color.Black
//                                        )
//                                        Box(
//                                            modifier = Modifier
//                                                .background(
//                                                    connectorStatusColor,
//                                                    shape = RoundedCornerShape(percent = 50)
//                                                )
//                                                .size(12.dp)
//                                                .border(
//                                                    width = 1.dp,
//                                                    color = Color.LightGray,
//                                                    shape = RoundedCornerShape(percent = 50)
//                                                )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
//                Text(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    text = stringResource(R.string.charging_station_details_description_title),
//                    fontSize = 16.sp,
//                    color = Color.LightGray
//                )
//
//
//                if (chargingStationDetails!!.description == null) {
//                    Text(
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        text = stringResource(R.string.charging_station_details_no_description),
//                        fontSize = 20.sp,
//                        color = Color.Gray
//                    )
//                } else {
//                    Box(
//                        modifier = Modifier
//                            .background(
//                                color = Gray1, shape = RoundedCornerShape(8.dp)
//                            )
//                            .fillMaxWidth()
//                            .defaultMinSize(minHeight = 64.dp)
//                            .padding(8.dp)
//                    ) {
//                        Text(
//                            text = chargingStationDetails!!.description!!,
//                            fontSize = 14.sp,
//                            color = Color.Black,
//                        )
//                    }
//                }
//                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
//                Text(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    text = stringResource(R.string.charging_station_details_marks_title),
//                    fontSize = 16.sp,
//                    color = Color.LightGray
//                )
//                if (chargingStationDetails!!.chargingMarksWithUserName.isEmpty()) {
//                    Text(
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        text = stringResource(R.string.charging_station_details_no_marks),
//                        fontSize = 20.sp,
//                        color = Color.Gray
//                    )
//                } else {
//                    chargingStationDetails!!.chargingMarksWithUserName.forEach {
//                        Box(
//                            modifier = Modifier
//                                .background(
//                                    Gray1, shape = RoundedCornerShape(8.dp)
//                                )
//                                .fillMaxWidth()
//                                .padding(8.dp)
//                        ) {
//                            Column {
//                                if (it.userId != null) {
//                                    Text(
//                                        text = it.userName!!,
//                                        fontSize = 16.sp,
//                                        color = Color.Black
//                                    )
//                                } else {
//                                    Text(
//                                        text = stringResource(R.string.charging_station_details_anonymous),
//                                        fontSize = 16.sp,
//                                        color = Color.Black
//                                    )
//                                }
//                                if (it.status == 1) {
//                                    Text(
//                                        text = stringResource(R.string.charging_station_details_mark_success),
//                                        fontSize = 12.sp,
//                                        color = Green1
//                                    )
//                                } else {
//                                    Text(
//                                        text = stringResource(R.string.charging_station_details_mark_failed),
//                                        fontSize = 12.sp,
//                                        color = Color.Red
//                                    )
//                                }
//                                Box(
//                                    modifier = Modifier
//                                        .background(
//                                            MaterialTheme.colorScheme.primary,
//                                            shape = RoundedCornerShape(4.dp)
//                                        )
//                                        .padding(8.dp, 0.dp)
//                                ) {
//                                    Text(
//                                        text = it.chargingType.name,
//                                        fontSize = 12.sp,
//                                        color = MaterialTheme.colorScheme.onPrimary
//                                    )
//                                }
//                                Text(
//                                    text = it.time,
//                                    fontSize = 12.sp,
//                                    color = Color.Gray
//                                )
//                            }
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.size(32.dp))
//            }
//        }
//    }
//}