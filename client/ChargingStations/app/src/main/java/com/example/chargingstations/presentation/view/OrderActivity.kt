package com.example.chargingstations.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chargingstations.presentation.theme.ChargingStationsTheme
import com.example.chargingstations.presentation.viewmodel.AuthenticationActivityViewModel
import com.example.chargingstations.presentation.viewmodel.OrderActivityViewModel

class OrderActivity : ComponentActivity() {

    private val viewModel: OrderActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.let {
            viewModel.setConnectorId(it.getInt("connector_id"))
            viewModel.setChargingStationId(it.getInt("charging_station_id"))
            viewModel.setChargingTypeId(it.getInt("charging_type_id"))
            viewModel.setChargingStationAddress(it.getString("charging_station_address"))
            viewModel.setConnectorTypeName(it.getString("charging_type_name"))
            viewModel.setConnectorRate(it.getFloat("connector_rate"))
            viewModel.setToken(it.getString("token"))
        }


        setContent {
            ChargingStationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val chargingStationAddress by viewModel.chargingStationAddress.collectAsState()
                    val connectorTypeName by viewModel.connectorTypeName.collectAsState()
                    val connectorRate by viewModel.connectorRate.collectAsState()
                    val amount by viewModel.amount.collectAsState()
                    val orderStatus by viewModel.orderStatus.collectAsState()
                    val orderProgress by viewModel.orderProgress.collectAsState()
                    val finished by viewModel.finished.collectAsState()
                    val amountIsIncorrect by viewModel.amountIsIncorrect.collectAsState()

                    when (finished) {
                        true -> {
                            finish()
                        }

                        false -> {}
                    }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (orderStatus) {
                            null -> {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxWidth()
                                        .padding(horizontal = 64.dp),
                                ) {
                                    Text(
                                        text = "Укажите объем зарядки (kW/h): "
                                    )
                                    TextField(
                                        value = amount,
                                        isError = amountIsIncorrect,
                                        onValueChange = {
                                            viewModel.setAmount(it)
                                        },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        )
                                    )
                                    Spacer(modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Адрес: "
                                    )
                                    Text(
                                        text = chargingStationAddress ?: "null"
                                    )
                                    Text(
                                        text = "Тип зарядки: "
                                    )
                                    Text(
                                        text = connectorTypeName ?: "null"
                                    )
                                    Text(
                                        text = "Скорость зарядки: "
                                    )
                                    Text(
                                        text = "${connectorRate ?: "null"} kW/h"
                                    )
                                    Spacer(modifier = Modifier.size(16.dp))
                                    Button(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxWidth(),
                                        onClick = {
                                            viewModel.charge()
                                        },
                                        enabled = !amountIsIncorrect
                                    ) {
                                        Text(text = "Зарядить")
                                    }
                                }
                            }

                            0 -> {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxWidth()
                                        .padding(horizontal = 64.dp),
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        text = "Прогресс: $orderProgress"
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    LinearProgressIndicator(
                                        progress = orderProgress!! / 100.0f,
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(8.dp)
                                            .align(Alignment.CenterHorizontally),
                                    )
                                }
                            }

                            1 -> {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxWidth()
                                        .padding(horizontal = 64.dp),
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        text = "Зарядка завершена!",
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        text = "Можете оставить отметку"
                                    )
                                    Spacer(modifier = Modifier.size(16.dp))
                                    Button(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxWidth(),
                                        onClick = {
                                            viewModel.mark(1)
                                        }
                                    ) {
                                        Text(text = "Зарядка прошла успешно")
                                    }
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Button(
                                        colors = ButtonDefaults.buttonColors()
                                            .copy(containerColor = Color.Red),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxWidth(),
                                        onClick = {
                                            viewModel.mark(0)
                                        }
                                    ) {
                                        Text(text = "Зарядка не удалась")
                                    }
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Button(
                                        colors = ButtonDefaults.buttonColors()
                                            .copy(containerColor = Color.Gray),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxWidth(),
                                        onClick = {
                                            finish()
                                        }
                                    ) {
                                        Text(text = "Не оставлять")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}