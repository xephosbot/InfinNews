package com.xbot.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.xbot.designsystem.theme.InfinNewsTheme
import com.xbot.designsystem.utils.ProvideShimmer
import com.xbot.designsystem.utils.toLocalizedString
import com.xbot.domain.model.Article
import com.xbot.domain.model.Source
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ArticleAuthorItem(
    article: Article,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceBright
    ) {
        Row(
            modifier = modifier
                .height(56.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArticleImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp)),
                article = article
            )

            Column(Modifier.weight(1f)) {
                article.author?.let { author ->
                    ProvideTextStyle(MaterialTheme.typography.titleSmall) {
                        Text(
                            text = author,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                    Text(
                        modifier = Modifier.graphicsLayer { alpha = 0.75f },
                        text = article.publishedAt.toLocalizedString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            article.source?.let { source ->
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .graphicsLayer { alpha = 0.75f }
                ) {
                    ArticleSourcePill(source = source)
                }
            }
        }
    }
}

@Preview(apiLevel = 34)
@Composable
private fun ArticleAuthorItemPreview() {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val article = remember {
        Article(
            title = "Matthew McConaughey on starring with his family in film about California's deadliest wildfire",
            source = Source(
                name = "google.com"
            ),
            url = "",
            author = "Scott Lemieux",
            publishedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    InfinNewsTheme {
        ProvideShimmer(shimmer) {
            ArticleAuthorItem(
                article = article,
            )
        }
    }
}