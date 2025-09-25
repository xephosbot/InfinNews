package com.xbot.designsystem.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.xbot.designsystem.theme.InfinNewsTheme
import com.xbot.designsystem.utils.LocalShimmer
import com.xbot.designsystem.utils.ProvideShimmer
import com.xbot.domain.model.Article
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ArticleCard(
    article: Article?,
    modifier: Modifier = Modifier,
    onClick: (Article) -> Unit,
) {
    Crossfade(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable {
                article?.let(onClick)
            },
        targetState = article,
        label = "ArticleCard CrossFade to ${if (article == null) "Loading" else "Loaded Article"}"
    ) { state ->
        when (state) {
            null -> ArticleCardPlaceholder()
            else -> ArticleCardContent(state)
        }
    }
}

@Composable
private fun ArticleCardContent(
    article: Article,
    modifier: Modifier = Modifier
) {
    ArticleCardLayout(
        modifier = modifier,
        poster = {
            ArticleImage(
                modifier = Modifier.matchParentSize(),
                article = article
            )
        },
        title = {
            Text(
                text = article.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    )
}

@Composable
private fun ArticleCardLayout(
    poster: @Composable BoxScope.() -> Unit,
    title: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                poster()
            }
            Spacer(Modifier.height(12.dp))
            ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    title()
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ArticleCardPlaceholder(
    modifier: Modifier = Modifier
) {
    val shimmer = LocalShimmer.current

    ArticleCardLayout(
        modifier = modifier,
        poster = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shimmer(shimmer)
                    .background(Color.LightGray)
            )
        },
        title = { /*TODO*/ }
    )
}

@Preview(apiLevel = 34)
@Composable
private fun ArticleCardPreview() {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val article = remember {
        Article(
            title = "Matthew McConaughey on starring with his family in film about California's deadliest wildfire",
            url = "",
            publishedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    InfinNewsTheme {
        ProvideShimmer(shimmer) {
            ArticleCard(
                article = article,
                onClick = { article ->

                }
            )
        }
    }
}