package com.example.chargingstations.presentation.view.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chargingstations.R

@Composable
fun ConfirmDismissDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    icon: ImageVector,
    title: Int,
    dismissButtonLabel: Int,
    confirmButtonLabel: Int
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = stringResource(title),
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 8.dp)
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        },
                    ) {
                        Text(
                            text = stringResource(dismissButtonLabel)
                        )
                    }
                    TextButton(
                        onClick = {
                            onConfirmRequest()
                        },
                    ) {
                        Text(
                            text = stringResource(confirmButtonLabel)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    icon: ImageVector,
    title: String,
    confirmButtonLabel: String
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = title,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                TextButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 8.dp),
                    onClick = {
                        onConfirmRequest()
                    },
                ) {
                    Text(
                        text = confirmButtonLabel
                    )
                }
            }
        }
    }
}

@Composable
fun DismissDialog(
    onDismissRequest: () -> Unit,
    icon: ImageVector,
    title: Int
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = stringResource(title),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}


@Composable
fun GPSDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDismissDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Info,
        title = R.string.gps_dialog_title,
        dismissButtonLabel = R.string.cancel,
        confirmButtonLabel = R.string.enable
    )
}

@Composable
fun NoInternetConnectionDialog() {
    DismissDialog(
        onDismissRequest = {},
        icon = Icons.Default.Warning,
        title = R.string.no_internet_connection_dialog_title
    )
}

@Composable
fun IncorrectQRCodeDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Warning,
        title = stringResource(R.string.incorrect_qrcode_dialog_title),
        confirmButtonLabel = stringResource(R.string.continue_)
    )
}

@Composable
fun ChargingStationNotFoundDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Warning,
        title = stringResource(R.string.charging_station_not_found_dialog_title),
        confirmButtonLabel = stringResource(R.string.continue_)
    )
}

@Composable
fun ConnectionProblemDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Warning,
        title = stringResource(R.string.connection_problem_dialog_title),
        confirmButtonLabel = stringResource(R.string.try_it_again)
    )
}


@Composable
fun RegistrationConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Info,
        title = "Подтвердите регистрацию на почте",
        confirmButtonLabel = "ok"
    )
}

@Composable
fun ErrorDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Info,
        title = "Произошла ошибка",
        confirmButtonLabel = "ok"
    )
}

@Composable
fun RegistrationUsernameDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Info,
        title = "Данное имя пользователя уже занято",
        confirmButtonLabel = "ok"
    )
}

@Composable
fun RegistrationEmailDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    ConfirmDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = onConfirmRequest,
        icon = Icons.Default.Info,
        title = "Данная почта уже занята",
        confirmButtonLabel = "ok"
    )
}