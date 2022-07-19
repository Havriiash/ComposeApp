package com.dvh.composetest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvh.composetest.R
import com.dvh.composetest.domain.entities.ConferenceUi

object ConferenceDetailScreen {
    const val ROUTE = "ConferenceDetailScreen"
}

@Composable
fun ConferenceDetailScreen(item: ConferenceUi?) {
    item ?: throw RuntimeException("Not passed Conference")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AsyncImage(
            modifier = Modifier
                .width(156.dp)
                .height(156.dp)
                .clip(RoundedCornerShape(78.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(), contentDescription = ""
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp),
            text = item.speaker,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Image(
                modifier = Modifier
                    .width(22.dp)
                    .height(22.dp)
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.icv_calendar), contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
            Text(
                text = "${item.date}, ${item.timeInterval}",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.onBackground
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            text = item.description,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onBackground
        )
    }

}