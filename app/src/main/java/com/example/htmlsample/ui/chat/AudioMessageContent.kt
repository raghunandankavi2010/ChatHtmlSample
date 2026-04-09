package com.example.htmlsample.ui.chat

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.htmlsample.domain.model.MessageContent
import kotlinx.coroutines.delay

@Composable
fun AudioMessageContent(
    audio: MessageContent.Audio,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var currentLabel by remember { mutableStateOf("0:00") }

    val mediaPlayer = remember(audio.url) {
        MediaPlayer().also { player ->
            try {
                player.setDataSource(audio.url)
                player.setOnPreparedListener { isPrepared = true }
                player.setOnCompletionListener {
                    isPlaying = false
                    sliderValue = 0f
                    currentLabel = "0:00"
                }
                player.setOnErrorListener { _, _, _ ->
                    hasError = true
                    isPlaying = false
                    true
                }
                player.prepareAsync()
            } catch (e: Exception) {
                hasError = true
            }
        }
    }

    DisposableEffect(audio.url) {
        onDispose { mediaPlayer.release() }
    }

    // Poll current position while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            if (isPrepared && mediaPlayer.duration > 0 && !isDragging) {
                sliderValue = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
                currentLabel = formatDuration(mediaPlayer.currentPosition)
            }
            delay(200L)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = {
                if (!isPrepared || hasError) return@FilledIconButton
                if (isPlaying) {
                    mediaPlayer.pause()
                    isPlaying = false
                } else {
                    mediaPlayer.start()
                    isPlaying = true
                }
            }
        ) {
            when {
                hasError -> Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Unavailable",
                    tint = MaterialTheme.colorScheme.error
                )
                !isPrepared -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                else -> Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = audio.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Slider(
                value = sliderValue,
                onValueChange = { value ->
                    isDragging = true
                    sliderValue = value
                },
                onValueChangeFinished = {
                    if (isPrepared && mediaPlayer.duration > 0) {
                        mediaPlayer.seekTo((sliderValue * mediaPlayer.duration).toInt())
                    }
                    isDragging = false
                },
                enabled = isPrepared && !hasError,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (hasError) "Unavailable" else currentLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasError) MaterialTheme.colorScheme.error
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = audio.durationLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDuration(ms: Int): String {
    val totalSec = ms / 1000
    return "%d:%02d".format(totalSec / 60, totalSec % 60)
}