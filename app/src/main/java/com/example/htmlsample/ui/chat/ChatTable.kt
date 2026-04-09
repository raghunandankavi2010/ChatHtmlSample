package com.example.htmlsample.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A pure Column+Row table — safe to embed in a LazyColumn without scroll conflicts.
 * For wide tables, the content scrolls horizontally while the LazyColumn handles
 * vertical scrolling as usual.
 */
@Composable
fun ChatTable(
    title: String,
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
    cellMinWidth: Dp = 100.dp,
    onCellClick: (cellText: String) -> Unit = {}
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val headerBg = MaterialTheme.colorScheme.primaryContainer
    val headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    val evenRowBg = MaterialTheme.colorScheme.surface
    val oddRowBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

    Column(modifier = modifier) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // Horizontal scroll for tables wider than the bubble
        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier.border(
                    width = 0.5.dp,
                    color = borderColor,
                    shape = MaterialTheme.shapes.small
                )
            ) {
                // ── Header row (not clickable) ──────────────────────────────
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .background(
                            color = headerBg,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    headers.forEachIndexed { index, header ->
                        TableCell(
                            text = header,
                            textColor = headerTextColor,
                            fontWeight = FontWeight.Bold,
                            cellMinWidth = cellMinWidth,
                            showEndDivider = index < headers.lastIndex,
                            dividerColor = borderColor,
                            onClick = null
                        )
                    }
                }

                // ── Data rows (cells are clickable) ──────────────────────────
                rows.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .background(if (rowIndex % 2 == 0) evenRowBg else oddRowBg)
                    ) {
                        row.forEachIndexed { colIndex, cell ->
                            TableCell(
                                text = cell,
                                textColor = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal,
                                cellMinWidth = cellMinWidth,
                                showEndDivider = colIndex < row.lastIndex,
                                dividerColor = borderColor,
                                onClick = { onCellClick(cell) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    textColor: Color,
    fontWeight: FontWeight,
    cellMinWidth: Dp,
    showEndDivider: Boolean,
    dividerColor: Color,
    onClick: (() -> Unit)?
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = fontWeight,
            color = textColor,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .widthIn(min = cellMinWidth)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
        )
        if (showEndDivider) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(dividerColor)
            )
        }
    }
}
