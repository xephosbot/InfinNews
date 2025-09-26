@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.xbot.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.xbot.designsystem.components.ArticleAuthorItem
import com.xbot.designsystem.components.ArticleImage
import com.xbot.designsystem.utils.ArticleSharedElementKey
import com.xbot.designsystem.utils.LocalAnimatedContentScope
import com.xbot.designsystem.utils.LocalSharedTransitionScope
import com.xbot.designsystem.utils.LocalShimmer
import com.xbot.designsystem.utils.ProvideShimmer
import com.xbot.designsystem.utils.sharedBoundsRevealWithShapeMorph
import com.xbot.designsystem.utils.shimmerUpdater
import com.xbot.domain.model.Article
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreenContent(
        articleUrl = viewModel.articleUrl,
        category = viewModel.category,
        state = state,
        modifier = modifier,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreenContent(
    articleUrl: String,
    category: String,
    state: DetailsScreenState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current

    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier
                .sharedBoundsRevealWithShapeMorph(
                    sharedContentState = rememberSharedContentState(
                        ArticleSharedElementKey(articleUrl, category)
                    ),
                    animatedVisibilityScope = animatedContentScope,
                    targetShapeCornerRadius = 12.dp,
                    restingShapeCornerRadius = 0.dp,
                ),
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
            },
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ) { innerPadding ->
            val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Custom)
            ProvideShimmer(shimmer) {
                Crossfade(
                    modifier = Modifier
                        .padding(innerPadding)
                        .shimmerUpdater(shimmer),
                    targetState = state
                ) { state ->
                    when (state) {
                        DetailsScreenState.Loading -> ArticlePagePlaceholder()
                        is DetailsScreenState.Success -> ArticlePage(state.article)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticlePage(
    article: Article,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
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
        Text(
            modifier = Modifier.padding(16.dp),
            text = article.content.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ArticlePagePlaceholder(
    modifier: Modifier = Modifier
) {
    val shimmer = LocalShimmer.current

    Column(
        modifier = modifier.verticalScroll(
            state = rememberScrollState(),
            enabled = false,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .aspectRatio(1f)
                .shimmer(shimmer)
                .background(Color.LightGray)
        )
    }
}