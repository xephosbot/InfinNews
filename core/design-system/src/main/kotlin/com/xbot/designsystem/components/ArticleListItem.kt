package com.xbot.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xbot.designsystem.theme.InfinNewsTheme
import com.xbot.designsystem.utils.shimmer
import com.xbot.designsystem.utils.toLocalizedString
import com.xbot.domain.model.Article
import com.xbot.domain.model.Source
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ArticleListItem(
    article: Article?,
    modifier: Modifier = Modifier,
    onClick: (Article) -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = { article?.let(onClick) },
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = ArticleListItemDefaults.Shape,
    ) {
        Crossfade(
            targetState = article,
            label = "ArticleCard CrossFade to ${if (article == null) "Loading" else "Loaded Article"}"
        ) { state ->
            when (state) {
                null -> ArticleListItemPlaceholder()
                else -> ArticleListItemContent(state)
            }
        }
    }
}

@Composable
private fun ArticleListItemContent(
    article: Article,
    modifier: Modifier = Modifier
) {
    ArticleListItemLayout(
        modifier = modifier,
        poster = {
            ArticleImage(
                modifier = Modifier.fillMaxSize(),
                article = article
            )
        },
        title = {
            Text(
                text = article.title,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            article.source?.let { source ->
                ArticleSourcePill(source)
            }
            Text(
                text = article.publishedAt.toLocalizedString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    )
}

@Composable
private fun ArticleListItemLayout(
    poster: @Composable BoxScope.() -> Unit,
    title: @Composable BoxScope.() -> Unit,
    subtitle: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ArticleListItemDefaults.ContainerHeight)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ProvideTextStyle(MaterialTheme.typography.titleSmall) {
                Box(
                    Modifier.weight(1f)
                ) {
                    title()
                }
            }
            ProvideTextStyle(MaterialTheme.typography.labelSmall) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.graphicsLayer {
                        alpha = SubheaderTextAlpha
                    }
                ) {
                    subtitle()
                }
            }
        }

        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small)
        ) {
            poster()
        }
    }
}

@Composable
private fun ArticleListItemPlaceholder(
    modifier: Modifier = Modifier
) {
    ArticleListItemLayout(
        modifier = modifier,
        poster = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shimmer()
            )
        },
        title = { },
        subtitle = { }
    )
}

private const val SubheaderTextAlpha = 0.75f

@Preview(apiLevel = 34)
@Composable
private fun ArticleListItemPreview() {
    val article = remember {
        Article(
            id = "123",
            title = "Matthew McConaughey on starring with his family in film about California's deadliest wildfire",
            source = Source(
                name = "google.com"
            ),
            url = "",
            publishedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    InfinNewsTheme {
        ArticleListItem(
            article = article,
            onClick = { article ->
            }
        )
    }
}

object ArticleListItemDefaults {
    val CornerRadius = 12.dp
    val ContainerHeight = 120.dp

    val Shape = RoundedCornerShape(CornerRadius)
}
