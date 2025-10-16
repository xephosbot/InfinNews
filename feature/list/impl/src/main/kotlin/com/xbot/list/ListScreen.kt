package com.xbot.list

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.xbot.common.event.ObserveEvents
import com.xbot.common.event.SnackbarData
import com.xbot.designsystem.components.ArticleListItem
import com.xbot.designsystem.components.ArticleListItemDefaults
import com.xbot.designsystem.components.Crossfade
import com.xbot.designsystem.components.ObserveLoadState
import com.xbot.designsystem.components.pagerTabIndicatorOffset
import com.xbot.designsystem.components.pagingItems
import com.xbot.designsystem.utils.ArticleSharedElementKey
import com.xbot.designsystem.utils.LocalAnimatedContentScope
import com.xbot.designsystem.utils.LocalSharedTransitionScope
import com.xbot.designsystem.utils.sharedBoundsRevealWithShapeMorph
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.feature.list.impl.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
internal fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel,
    onArticleClick: (article: Article) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ListScreenContent(
        modifier = modifier,
        state = state,
        events = viewModel.event,
        onAction = viewModel::onAction,
        onArticleClick = onArticleClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreenContent(
    modifier: Modifier = Modifier,
    state: ListScreenState,
    events: Flow<ListScreenEvent>,
    onAction: (ListScreenAction) -> Unit,
    onArticleClick: (article: Article) -> Unit
) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    events.ObserveEvents { event ->
        when (event) {
            is ListScreenEvent.ShowSnackbar -> {
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

    val pagerState = rememberPagerState { NewsCategory.entries.size }
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                val category = NewsCategory.entries[it]
                onAction(ListScreenAction.SelectCategory(category))
            }
    }

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
            key = { NewsCategory.entries[it] }
        ) { pageIndex ->
            val category = remember(pageIndex) { NewsCategory.entries[pageIndex] }
            val listState = rememberLazyListState()

            Crossfade(
                targetState = state,
                contentKey = { it::class }
            ) { state ->
                when (state) {
                    is ListScreenState.Loading -> CategoryPagePlaceholder(contentPadding = innerPadding)
                    is ListScreenState.Success -> {
                        when (val pagingFlow = state.pagingFlows[category]) {
                            null -> CategoryPagePlaceholder(contentPadding = innerPadding)
                            else -> {
                                val items = pagingFlow.collectAsLazyPagingItems()
                                items.ObserveLoadState { error ->
                                    onAction(
                                        ListScreenAction.ShowSnackbar(
                                            SnackbarData(
                                                value = error,
                                                onDismissed = { /*Nothing to do*/ },
                                                onActionPerformed = { items.retry() }
                                            )
                                        )
                                    )
                                }

                                CategoryPage(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = innerPadding,
                                    listState = listState,
                                    items = items,
                                    onArticleClick = onArticleClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    listState: LazyListState,
    items: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing by remember(items) {
        derivedStateOf { items.loadState.refresh is LoadState.Loading }
    }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier.pullToRefresh(
                isRefreshing = isRefreshing,
                state = pullToRefreshState,
                onRefresh = {
                    items.refresh()
                }
            ),
        ) {
            LazyColumn(
                modifier = modifier,
                state = listState,
                contentPadding = contentPadding,
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                }
                if (items.loadState.refresh is LoadState.Loading) {
                    items(ListPlaceholdersCount) {
                        Column {
                            ArticleListItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                article = null,
                                onClick = onArticleClick
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                } else {
                    pagingItems(
                        items = items,
                        key = items.itemKey { it.id }
                    ) { _, article ->
                        Column(
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 16.dp)
                        ) {
                            ArticleListItem(
                                modifier = Modifier.then(
                                    article?.let {
                                        Modifier.sharedBoundsRevealWithShapeMorph(
                                            sharedContentState = rememberSharedContentState(
                                                ArticleSharedElementKey(id = it.id)
                                            ),
                                            animatedVisibilityScope = animatedContentScope,
                                            targetShapeCornerRadius = 0.dp,
                                            restingShapeCornerRadius = ArticleListItemDefaults.CornerRadius,
                                            keepChildrenSizePlacement = false,
                                        )
                                    } ?: Modifier
                                ),
                                article = article,
                                onClick = onArticleClick
                            )
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

            PullToRefreshDefaults.Indicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(contentPadding),
                isRefreshing = isRefreshing,
                state = pullToRefreshState
            )
        }
    }
}

@Composable
private fun CategoryPagePlaceholder(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState(), false)
            .padding(contentPadding)
    ) {
        repeat(ListPlaceholdersCount) {
            Column {
                Spacer(Modifier.height(16.dp))
                ArticleListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    article = null,
                    onClick = {}
                )
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

private const val ListPlaceholdersCount = 10
