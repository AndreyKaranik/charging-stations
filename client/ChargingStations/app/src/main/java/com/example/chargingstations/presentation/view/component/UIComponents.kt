package com.example.chargingstations.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chargingstations.R
import com.example.chargingstations.domain.model.ChargingStationMedium

@Composable
fun BasicIconButton(
    onClick: () -> Unit, imageVector: ImageVector
) {
    Box(contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            elevation = ButtonDefaults.buttonElevation(3.dp),
            modifier = Modifier.size(48.dp)
        ) {}
        Icon(
            imageVector = imageVector, contentDescription = "description", tint = Color.White
        )
    }
}

@Composable
fun BasicIconButtonWithProgress(
    onClick: () -> Unit, imageVector: ImageVector, progressIsShown: Boolean
) {
    Box(contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            elevation = ButtonDefaults.buttonElevation(3.dp),
            modifier = Modifier.size(48.dp)
        ) {}
        if (progressIsShown) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Icon(
                imageVector = imageVector, contentDescription = "description", tint = Color.White
            )
        }
    }
}