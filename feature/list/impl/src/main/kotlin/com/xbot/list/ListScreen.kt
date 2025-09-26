package com.xbot.list

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.xbot.designsystem.components.ArticleListItem
import com.xbot.designsystem.components.Tab
import com.xbot.designsystem.components.pagerTabIndicatorOffset
import com.xbot.designsystem.components.pagingItems
import com.xbot.designsystem.utils.ArticleSharedElementKey
import com.xbot.designsystem.utils.LocalAnimatedContentScope
import com.xbot.designsystem.utils.LocalSharedTransitionScope
import com.xbot.designsystem.utils.ProvideShimmer
import com.xbot.designsystem.utils.sharedBoundsRevealWithShapeMorph
import com.xbot.designsystem.utils.shimmerUpdater
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.list.impl.R
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = koinViewModel(),
    navigateToDetails: (article: Article, category: NewsCategory) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState { NewsCategory.entries.size }
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = "InfinNews") }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            PrimaryIndicator(
                                modifier = Modifier.pagerTabIndicatorOffset(
                                    pagerState,
                                    tabPositions
                                ),
                                width = Dp.Unspecified
                            )
                        }
                    }
                ) {
                    NewsCategory.entries.forEachIndexed { index, category ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(category.ordinal)
                                }
                            },
                            text = {
                                Text(stringResource(category.stringRes))
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            contentPadding = innerPadding,
        ) { pageIndex ->
            val category = NewsCategory.entries[pageIndex]
            val items = viewModel.getArticles(category).collectAsLazyPagingItems()

            CategoryPage(
                modifier = Modifier.fillMaxSize(),
                category = category,
                items = items,
                onShowErrorMessage = {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = it.message.orEmpty(),
                            actionLabel = "Retry",
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            items.refresh()
                        }
                    }
                },
                onArticleClick = { article ->
                    navigateToDetails(article, category)
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CategoryPage(
    modifier: Modifier = Modifier,
    category: NewsCategory,
    items: LazyPagingItems<Article>,
    onShowErrorMessage: (Throwable) -> Unit,
    onArticleClick: (Article) -> Unit,
) {
    LaunchedEffect(items) {
        snapshotFlow { items.loadState }.collect { loadState ->
            (loadState.refresh as? LoadState.Error)?.let { onShowErrorMessage(it.error) }
            (loadState.append as? LoadState.Error)?.let { onShowErrorMessage(it.error) }
            (loadState.prepend as? LoadState.Error)?.let { onShowErrorMessage(it.error) }
        }
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current

    val shimmer = rememberShimmer(ShimmerBounds.Custom)

    ProvideShimmer(shimmer) {
        LazyColumn(
            modifier = modifier.shimmerUpdater(shimmer),
        ) {
            item {
                Spacer(Modifier.height(16.dp))
            }
            if (items.loadState.refresh is LoadState.Loading) {
                items(5) {
                    Column {
                        ArticleListItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem(),
                            article = null,
                            onClick = onArticleClick
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            } else {
                pagingItems(items) { _, article ->
                    Column {
                        with(sharedTransitionScope) {
                            ArticleListItem(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                                    .then(
                                        if (article != null) {
                                            Modifier.sharedBoundsRevealWithShapeMorph(
                                                sharedContentState = rememberSharedContentState(
                                                    ArticleSharedElementKey(article.url, category.toString())
                                                ),
                                                animatedVisibilityScope = animatedContentScope,
                                                targetShapeCornerRadius = 0.dp,
                                                restingShapeCornerRadius = 12.dp,
                                                keepChildrenSizePlacement = false,
                                            )
                                        } else {
                                            Modifier
                                        }
                                    ),
                                article = article,
                                onClick = onArticleClick
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            if (items.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@get:StringRes
private val NewsCategory.stringRes: Int
    get() = when (this) {
        NewsCategory.GENERAL -> R.string.category_general
        NewsCategory.BUSINESS -> R.string.category_business
        NewsCategory.ENTERTAINMENT -> R.string.category_entertainment
        NewsCategory.HEALTH -> R.string.category_health
        NewsCategory.SCIENCE -> R.string.category_science
        NewsCategory.SPORTS -> R.string.category_sports
        NewsCategory.TECHNOLOGY -> R.string.category_technology
    }