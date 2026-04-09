package com.example.htmlsample.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.example.htmlsample.domain.model.ChatMessage
import com.example.htmlsample.domain.model.MessageContent

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val context = LocalContext.current
    val horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isMine) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.isMine) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        if (!message.isMine) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = if (message.isMine) 16.dp else 4.dp,
                topEnd = if (message.isMine) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = bubbleColor,
            // Table bubbles can be wider than text bubbles
            modifier = Modifier.widthIn(
                max = if (message.content is MessageContent.Table) 360.dp else 300.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (val content = message.content) {
                    is MessageContent.Html -> HtmlMessageContent(
                        html = content.html,
                        textColor = textColor
                    )

                    is MessageContent.Table -> ChatTable(
                        title = content.title,
                        headers = content.headers,
                        rows = content.rows,
                        onCellClick = { cellText ->
                            Toast.makeText(context, cellText, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun HtmlMessageContent(html: String, textColor: androidx.compose.ui.graphics.Color) {
    val linkColor = MaterialTheme.colorScheme.primary
    val annotatedString = remember(html, linkColor) {
        htmlToAnnotatedString(
            html = html,
            style = HtmlStyle(
                textLinkStyles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    )
                )
            )
        )
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor
    )
}
