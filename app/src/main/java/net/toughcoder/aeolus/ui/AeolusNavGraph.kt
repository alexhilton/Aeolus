package net.toughcoder.aeolus.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import net.toughcoder.aeolus.data.DataContainer
import net.toughcoder.aeolus.ui.favorites.FavoritesScreen
import net.toughcoder.aeolus.ui.favorites.FavoritesViewModel
import net.toughcoder.aeolus.ui.search.SearchScreen
import net.toughcoder.aeolus.ui.search.SearchViewModel
import net.toughcoder.aeolus.ui.home.HomeScreen
import net.toughcoder.aeolus.ui.home.HomeViewModel

@Composable
fun AeolusNavGraph(
    appContainer: DataContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AeolusDestinations.WEATHER_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = AeolusDestinations.WEATHER_ROUTE,
            deepLinks = listOf(
                navDeepLink { uriPattern = "${AeolusDestinations.APP_URI}/${AeolusDestinations.WEATHER_ROUTE}" }
            )
        ) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    appContainer.locationRepository,
                    appContainer.weatherRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                modifier,
                uiState,
                viewModel::refresh
            ) {
                navController.navigate(AeolusDestinations.FAVORITES_ROUTE)
            }
        }

        composable(
            route = AeolusDestinations.FAVORITES_ROUTE,
            deepLinks = listOf(
                navDeepLink { uriPattern = "${AeolusDestinations.APP_URI}/${AeolusDestinations.FAVORITES_ROUTE}" }
            )
        ) {
            val viewModel:FavoritesViewModel = viewModel(
                factory = FavoritesViewModel.providerFactory(
                    appContainer.locationRepository,
                    appContainer.weatherRepository
                )
            )
            FavoritesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSearch = { navController.navigate(AeolusDestinations.SEARCH_ROUTE) },
            )
        }

        composable(
            route = AeolusDestinations.SEARCH_ROUTE,
            deepLinks = listOf(
                navDeepLink { uriPattern = "${AeolusDestinations.APP_URI}/${AeolusDestinations.SEARCH_ROUTE}" }
            )
        ) {
            val viewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.providerFactory(
                    appContainer.datastore,
                    appContainer.locationRepository,
                    appContainer.searchRepository
                )
            )
            SearchScreen(
                searchViewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}