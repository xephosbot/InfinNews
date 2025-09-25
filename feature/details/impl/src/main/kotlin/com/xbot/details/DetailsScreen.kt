package com.xbot.details

import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreenContent(
        state = state,
        modifier = modifier,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreenContent(
    state: DetailsScreenState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        innerPadding

        Crossfade(targetState = state) { state ->
            when (state) {
                DetailsScreenState.Loading -> {

                }
                is DetailsScreenState.Success -> {
                    Text(text = state.article.content.orEmpty())
                }
            }
        }
    }
}