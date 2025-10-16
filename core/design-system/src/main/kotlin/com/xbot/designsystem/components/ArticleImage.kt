package com.xbot.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.xbot.designsystem.utils.shimmer
import com.xbot.domain.model.Article

@Composable
fun ArticleImage(
    article: Article?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    var isLoading by rememberSaveable(article) { mutableStateOf(true) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = article?.urlToImage,
            contentDescription = contentDescription,
            contentScale = contentScale,
            onLoading = {
                isLoading = true
            },
            onSuccess = {
                isLoading = false
            },
            onError = {
                isLoading = true
            }
        )

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer()
            )
        }
    }
}
