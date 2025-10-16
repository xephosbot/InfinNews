@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.xbot.details

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xbot.common.event.ObserveEvents
import com.xbot.designsystem.components.ArticleAuthorItem
import com.xbot.designsystem.components.ArticleImage
import com.xbot.designsystem.components.ArticleListItemDefaults
import com.xbot.designsystem.components.Crossfade
import com.xbot.designsystem.utils.ArticleSharedElementKey
import com.xbot.designsystem.utils.LocalAnimatedContentScope
import com.xbot.designsystem.utils.LocalSharedTransitionScope
import com.xbot.designsystem.utils.sharedBoundsRevealWithShapeMorph
import com.xbot.designsystem.utils.shimmer
import com.xbot.domain.model.Article
import com.xbot.feature.details.impl.R
import kotlinx.coroutines.flow.Flow

@Composable
internal fun DetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel,
    onClickBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreenContent(
        modifier = modifier,
        state = state,
        events = viewModel.event,
        onClickBack = onClickBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailsScreenContent(
    modifier: Modifier = Modifier,
    state: DetailsScreenState,
    events: Flow<DetailsScreenEvent>,
    onClickBack: () -> Unit
) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    events.ObserveEvents { event ->
        when (event) {
            is DetailsScreenEvent.ShowSnackbar -> {
                val result = snackbarHostState.showSnackbar(
                    message = event.data.value.message.orEmpty(),
                    actionLabel = context.getString(R.string.button_retry),
                )
                when (result) {
                    SnackbarResult.Dismissed -> event.data.onDismissed()
                    SnackbarResult.ActionPerformed -> event.data.onActionPerformed()
                }
            }
        }
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current

    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier
                .sharedBoundsRevealWithShapeMorph(
                    sharedContentState = rememberSharedContentState(
                        ArticleSharedElementKey(id = state.articleId)
                    ),
                    animatedVisibilityScope = animatedContentScope,
                    targetShapeCornerRadius = ArticleListItemDefaults.CornerRadius,
                    restingShapeCornerRadius = 0.dp,
                ),
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        ) { innerPadding ->
            Crossfade(
                modifier = Modifier,
                targetState = state.article,
                label = "Details screen crossfade"
            ) { article ->
                when (article) {
                    null -> ArticlePagePlaceholder(contentPadding = innerPadding)
                    else -> ArticlePage(article = article, contentPadding = innerPadding)
                }
            }
        }
    }
}

@Composable
private fun ArticlePage(
    article: Article,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        ArticleImage(
            article = article,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .heightIn(max = 300.dp),
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineLarge,
                )
                ArticleAuthorItem(article = article)
            }
        }
        article.content?.let { content ->
            Text(
                modifier = Modifier.padding(16.dp),
                text = content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ArticlePagePlaceholder(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState(), false)
            .padding(contentPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .aspectRatio(1f)
                .shimmer()
        )
    }
}
