package com.mrsep.musicrecognizer.feature.concerts.presentation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder

private const val CONCERTS_ROUTE = "concerts"
private const val ARTIST_ARG = "artist"

fun NavController.navigateToConcerts(artist: String) {
    val encoded = URLEncoder.encode(artist, "UTF-8")
    navigate("$CONCERTS_ROUTE/$encoded")
}

fun NavGraphBuilder.concertsScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = "$CONCERTS_ROUTE/{$ARTIST_ARG}",
        arguments = listOf(navArgument(ARTIST_ARG) { type = NavType.StringType })
    ) {
        ConcertsScreen(onBackPressed = onBackPressed)
    }
}
