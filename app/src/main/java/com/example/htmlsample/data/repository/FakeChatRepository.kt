package com.example.htmlsample.data.repository

import com.example.htmlsample.domain.model.ChatMessage
import com.example.htmlsample.domain.model.MessageContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake repository with hardcoded data demonstrating all message types.
 * Replace with a real implementation (e.g. RetrofitChatRepository) when the API is ready.
 */
class FakeChatRepository : ChatRepository {

    override fun getMessages(): Flow<List<ChatMessage>> = flowOf(dummyMessages)

    private val dummyMessages = listOf(

        // ── Bold & Italic ────────────────────────────────────────────────────
        ChatMessage(
            id = "1",
            content = MessageContent.Html(
                "Hey! This message shows <b>bold text</b> and <i>italic text</i>. " +
                "You can also combine them: <b><i>bold italic</i></b>."
            ),
            senderName = "Alice",
            isMine = false,
            timestamp = "10:00 AM"
        ),

        // ── Hyperlink ────────────────────────────────────────────────────────
        ChatMessage(
            id = "2",
            content = MessageContent.Html(
                "Tap this to open a browser: " +
                "<a href=\"https://www.google.com\">Open Google</a>. " +
                "Or visit the <a href=\"https://developer.android.com/jetpack/compose\">Compose docs</a>."
            ),
            senderName = "Alice",
            isMine = false,
            timestamp = "10:01 AM"
        ),

        ChatMessage(
            id = "3",
            content = MessageContent.Html("Got it, looks great! Can you share a list?"),
            senderName = "Me",
            isMine = true,
            timestamp = "10:02 AM"
        ),

        // ── Unordered list ───────────────────────────────────────────────────
        ChatMessage(
            id = "4",
            content = MessageContent.Html(
                "Sure! Supported rich text formats:" +
                "<ul>" +
                "<li><b>Bold</b> — use &lt;b&gt; tags</li>" +
                "<li><i>Italic</i> — use &lt;i&gt; tags</li>" +
                "<li><a href=\"https://example.com\">Hyperlinks</a> — use &lt;a href&gt; tags</li>" +
                "<li>Unordered &amp; ordered lists</li>" +
                "</ul>"
            ),
            senderName = "Alice",
            isMine = false,
            timestamp = "10:03 AM"
        ),

        // ── Ordered list ─────────────────────────────────────────────────────
        ChatMessage(
            id = "5",
            content = MessageContent.Html(
                "Steps to integrate:" +
                "<ol>" +
                "<li>Add the dependency in <b>build.gradle</b></li>" +
                "<li>Call <i>htmlToAnnotatedString()</i> in your composable</li>" +
                "<li>Pass the result to <b>Text()</b></li>" +
                "</ol>"
            ),
            senderName = "Alice",
            isMine = false,
            timestamp = "10:04 AM"
        ),

        ChatMessage(
            id = "6",
            content = MessageContent.Html("Can you also send the team list as a table?"),
            senderName = "Me",
            isMine = true,
            timestamp = "10:05 AM"
        ),

        // ── Table: Team Members ──────────────────────────────────────────────
        ChatMessage(
            id = "7",
            content = MessageContent.Table(
                title = "Team Members",
                headers = listOf("Name", "Role", "Department", "Status"),
                rows = listOf(
                    listOf("Alice Martin",  "Lead",      "Android",  "Active"),
                    listOf("Bob Chen",      "Developer", "Android",  "Active"),
                    listOf("Carol Davis",   "Designer",  "UX",       "Active"),
                    listOf("David Kim",     "QA",        "Testing",  "On Leave"),
                    listOf("Eva Rossi",     "PM",        "Product",  "Active")
                )
            ),
            senderName = "Alice",
            isMine = false,
            timestamp = "10:06 AM"
        ),

        ChatMessage(
            id = "8",
            content = MessageContent.Html("Thanks! Can you also share the Q1 sales figures?"),
            senderName = "Me",
            isMine = true,
            timestamp = "10:07 AM"
        ),

        // ── Table: Sales Data ────────────────────────────────────────────────
        ChatMessage(
            id = "9",
            content = MessageContent.Table(
                title = "Q1 Sales Report",
                headers = listOf("Month", "Units", "Revenue", "Growth"),
                rows = listOf(
                    listOf("January",  "1,240", "\$62,000", "+12%"),
                    listOf("February", "1,480", "\$74,000", "+19%"),
                    listOf("March",    "1,750", "\$87,500", "+18%")
                )
            ),
            senderName = "Alice",
            isMine = false,
            timestamp = "10:08 AM"
        ),

        ChatMessage(
            id = "10",
            content = MessageContent.Html(
                "<i>Impressive numbers!</i> March was especially <b>strong</b>. " +
                "See the full report at <a href=\"https://example.com/report\">example.com/report</a>."
            ),
            senderName = "Me",
            isMine = true,
            timestamp = "10:09 AM"
        )
    )
}