package com.xd.dummyapp.weather

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.xd.dummyapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Item(
    temperature: String,
    dateTime: LocalDateTime
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("EEEE h a")
    val temperatureText = "$temperature°C"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Toast
                    .makeText(context, temperatureText, Toast.LENGTH_SHORT)
                    .show()
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
    ) {
        Text(
            text = dateTime.format(formatter),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .weight(1f) // This is the key change
                .padding(start = dimensionResource(id = R.dimen.horizontal_margin))
        )
        Text(
            text = temperatureText,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.horizontal_margin)
            )
        )
    }
}