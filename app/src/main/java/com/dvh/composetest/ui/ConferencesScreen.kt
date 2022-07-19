package com.dvh.composetest.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvh.composetest.R
import com.dvh.composetest.domain.entities.ConferenceUi
import com.dvh.composetest.domain.shared.Resource
import com.dvh.composetest.ui.theme.ComposeTestTheme
import com.dvh.composetest.ui.viewmodels.ConferencesScreenViewModel
import com.dvh.composetest.ui.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

object ConferencesScreen {
    const val ROUTE = "ConferencesScreen"
}

@Composable
fun ConferencesScreen(
    navController: NavController,
    viewModel: ConferencesScreenViewModel,
    sharedViewModel: SharedViewModel
) {
    val activity = LocalContext.current as Activity

    val favorites = viewModel.favorites.observeAsState(listOf())
    val sessions = viewModel.data.observeAsState(Resource.Loading())

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isShow = remember {
        mutableStateOf(false)
    }

    val navigateToDetails: (ConferenceUi) -> Unit = {
        sharedViewModel.setConference(it)
        navController.navigate(ConferenceDetailScreen.ROUTE)
    }

    BackHandler { isShow.value = true }

    if (isShow.value) {
        ExitDialog(
            onPositiveClick = { activity.finish() },
            onNegativeClick = { isShow.value = false }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        LazyColumn(content = {
            item {
                SearchView {
                    viewModel.search(it.lowercase())
                }
            }
            if (favorites.value.isNotEmpty()) {
                item {
                    FavoritesBlock(favorites.value, navigateToDetails)
                }
            }
            item {
                SessionsBlock(favorites.value, sessions.value,
                    onItemClick = navigateToDetails,
                    onFavoriteClick = {
                        if (!viewModel.toggleFavorites(it)) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Не удалось добавить сессию в избранное")
                            }
                        }
                    }
                )
            }
        })
    }
    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        SnackbarHost(hostState = snackbarHostState)
    }

    LaunchedEffect(key1 = viewModel, block = {
        viewModel.fetchItems()
    })
}

@Composable
fun ExitDialog(
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { onNegativeClick() },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Вы уверены, что хотите выйти из приложения?",
                    fontWeight = FontWeight.Bold
                )

                Row {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                        .padding(top = 8.dp, end = 4.dp),
                        onClick = { onPositiveClick() }) {
                        Text(text = "Да")
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                        .padding(top = 8.dp, start = 4.dp),
                        onClick = { onNegativeClick() }) {
                        Text(text = "Отмена")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchView(onSearchRequest: (request: String) -> Unit) {
    val searchStr = remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        value = searchStr.value, onValueChange = {
            searchStr.value = it
            onSearchRequest(it)
        },
        textStyle = TextStyle(color = MaterialTheme.colors.onBackground),
        placeholder = { Text(text = "поиск") },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.icv_search),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        },
        trailingIcon = {
            Image(
                modifier = Modifier.clickable {
                    searchStr.value = ""
                    onSearchRequest("")
                },
                painter = painterResource(id = R.drawable.icv_close),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    )
}

// region Favorite
@Composable
fun FavoritesBlock(items: List<ConferenceUi>, onItemClick: (item: ConferenceUi) -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Избранное",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colors.onBackground
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(content = {
            for (item in items.withIndex()) {
                item {
                    FavoriteItem(position = item.index, item = item.value) {
                        onItemClick(it)
                    }
                }
            }
        })
    }
}

@Composable
fun FavoriteItem(position: Int, item: ConferenceUi, onItemClick: (item: ConferenceUi) -> Unit) {
    val paddingStart = if (position == 0) 8.dp else 0.dp
    val paddingEnd = 8.dp

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(start = paddingStart, end = paddingEnd)
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(16.dp))
            .width(132.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClick(item) }
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            text = item.timeInterval,
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = item.date,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground
        )

        Text(
            text = item.speaker,
            maxLines = 1,
            style = TextStyle(fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            text = item.description,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground
        )
    }
}
// endregion

// region Sessions
@Composable
fun SessionsBlock(
    favorites: List<ConferenceUi>,
    resource: Resource<List<ConferenceUi>>,
    onItemClick: (item: ConferenceUi) -> Unit,
    onFavoriteClick: (item: ConferenceUi) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Сессии",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            color = MaterialTheme.colors.onBackground
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (resource) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> {
                val items = resource.data ?: listOf()
                if (items.isEmpty()) {
                    EmptySessions()
                } else {
                    for (item in items.withIndex()) {
                        SessionItemHeader(
                            items.getOrNull(item.index - 1)?.date ?: "",
                            item.value.date
                        )
                        SessionItem(item = item.value, favorites = favorites,
                            onItemClick = { onItemClick(it) },
                            onFavoriteClick = { onFavoriteClick(it) }
                        )
                    }
                }
            }
            is Resource.Error -> Text(text = "Ошибка загрузки данных")
        }
    }

}

@Composable
fun SessionItemHeader(prevDate: String, date: String) {
    if (prevDate != date) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 8.dp, end = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = date,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun SessionItem(
    favorites: List<ConferenceUi>,
    item: ConferenceUi,
    onItemClick: (item: ConferenceUi) -> Unit,
    onFavoriteClick: (item: ConferenceUi) -> Unit
) {
    Card(modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        .clickable { onItemClick(item) }
        .fillMaxWidth()
        .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .clickable { onItemClick(item) }
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .align(Alignment.CenterVertically),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = ""
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.speaker,
                        maxLines = 1,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        fontSize = 16.sp
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                        text = item.timeInterval,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 14.sp
                    )

                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        text = item.description,
                        fontSize = 14.sp
                    )
                }
                Image(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(56.dp))
                        .align(Alignment.CenterVertically)
                        .clickable { onFavoriteClick(item) },
                    painter = painterResource(id = R.drawable.icv_favorite),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        if (favorites.contains(item)) Color.Red else MaterialTheme.colors.onBackground
                    )
                )
            }
        }
    }
}

@Composable
fun EmptySessions() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = "По вашему запросу данных не найдено"
    )
}
// endregion


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    ComposeTestTheme {
//        ConferencesScreen(ConferencesScreenViewModel())
//    }
    ComposeTestTheme {
        SessionItem(listOf(), ConferenceUi(
            id = "1",
            date = "20",
            description = "Description",
            imageUrl = "",
            isFavorite = false,
            speaker = "Speaker",
            timeInterval = "10:00 - 11:00"
        ),
            onItemClick = {},
            onFavoriteClick = {}
        )
    }
}