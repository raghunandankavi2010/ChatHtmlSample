package com.example.htmlsample.data.repository

import com.example.htmlsample.domain.model.ChatMessage
import com.example.htmlsample.domain.model.MessageContent
import kotlinx.coroutines.delay

/**
 * In-memory fake repository with 100 seed messages spread over ~5 hours,
 * all within the 6-hour auto-delete window so nothing is pruned on startup.
 *
 * The [store] is sorted **oldest-first** (like a real DB ORDER BY createdAt ASC).
 * Page size 10 → initial load shows messages 91–100; scrolling up loads 81–90, 71–80, …
 */
class FakeChatRepository : ChatRepository {

    private val store: MutableList<ChatMessage> = buildStore()

    // ── ChatRepository ────────────────────────────────────────────────────────

    override suspend fun getLatestMessages(limit: Int): List<ChatMessage> {
        delay(400)
        return store.takeLast(limit).reversed() // newest-first
    }

    override suspend fun getOlderMessages(beforeId: String, limit: Int): List<ChatMessage> {
        delay(600)
        val idx = store.indexOfFirst { it.id == beforeId }
        if (idx <= 0) return emptyList()
        return store.subList(maxOf(0, idx - limit), idx).reversed() // newest-first
    }

    override suspend fun deleteMessagesOlderThan(cutoffMs: Long) {
        store.removeAll { it.createdAt < cutoffMs }
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    private fun buildStore(): MutableList<ChatMessage> {
        val now = System.currentTimeMillis()
        val m = 60 * 1000L

        // 100 messages spread 3 min apart over 5 hours — all inside the 6h delete window
        fun createdAt(index: Int) = now - (100 - index) * 3 * m

        // Wall-clock label starting at 08:00, 3 min per step
        fun clockAt(index: Int): String {
            val totalMin = 8 * 60 + index * 3
            val h = totalMin / 60
            val min = totalMin % 60
            val ampm = if (h < 12) "AM" else "PM"
            val h12 = if (h == 0) 12 else if (h > 12) h - 12 else h
            return "%d:%02d %s".format(h12, min, ampm)
        }

        // ── Messages 1–80: simple text conversation ──────────────────────────
        val textMessages: List<Pair<String, Boolean>> = listOf(
            "Hey! How's the new Android feature coming along?" to false,
            "Going well! Just finished wiring up the ViewModel." to true,
            "Nice! Did you handle config changes?" to false,
            "Yes, using rememberSaveable for UI state." to true,
            "Smart. What about the repository layer?" to false,
            "Using Flow + coroutines. Clean and reactive." to true,
            "Love it. When can I review the PR?" to false,
            "It's up! Check the main branch." to true,
            "Left some comments — mostly style suggestions." to false,
            "Thanks, will address them shortly." to true,
            "No rush. The architecture looks solid." to false,
            "Appreciate it! Navigation is next on my list." to true,
            "Are you using the Compose Navigation library?" to false,
            "Yes, NavHost with type-safe routes." to true,
            "Good choice. Deep links too?" to false,
            "Not yet, but planned for v2." to true,
            "Makes sense. Let's not over-engineer v1." to false,
            "Exactly. Ship first, iterate later." to true,
            "How's the UI shaping up? Any screenshots?" to false,
            "Sending one now!" to true,
            "Whoa, that looks polished already." to false,
            "Thanks! Dark mode support is next." to true,
            "Users love dark mode. High priority!" to false,
            "Already done — toggled via system setting." to true,
            "Impressive. What about accessibility?" to false,
            "Content descriptions and semantic roles added." to true,
            "Perfect. Did you test with TalkBack?" to false,
            "Yes, all interactive elements are reachable." to true,
            "Excellent work. Ready for QA?" to false,
            "Almost — just fixing a layout issue on tablets." to true,
            "Let me know when ready and I'll assign David." to false,
            "Will do! Should be done by end of day." to true,
            "Great. We're targeting the Friday build." to false,
            "Should be fine. I'm on track." to true,
            "Also, Carol wants to review the UX flows." to false,
            "Good idea. I'll share the Figma link with her." to true,
            "She'll appreciate that. Sync tomorrow?" to false,
            "Works for me. 10 AM?" to true,
            "Perfect. I'll send the calendar invite." to false,
            "Thanks! See you then." to true,
            "Quick question — which image loading lib are you using?" to false,
            "Coil 3. It's Compose-first and lightweight." to true,
            "We used Glide before. Is Coil better for Compose?" to false,
            "Much better — no ViewTarget hacks needed." to true,
            "Good to know for future projects." to false,
            "Also supports animated GIFs natively now." to true,
            "That's huge. We struggled with that in the old app." to false,
            "Those days are behind us!" to true,
            "Ha! Any network layer changes?" to false,
            "Switched to Ktor from Retrofit. Leaner for KMP." to true,
            "Planning Kotlin Multiplatform eventually?" to false,
            "Down the road, yes. iOS is on the roadmap." to true,
            "Bold move. The codebase will need cleanup." to false,
            "Starting with the domain layer — pure Kotlin, no Android deps." to true,
            "That's the right approach. UI stays platform-specific." to false,
            "Exactly the plan. Shared business logic only." to true,
            "Have you looked at Compose Multiplatform?" to false,
            "Briefly. Not quite production-ready yet." to true,
            "Agreed. Let's wait for stability." to false,
            "Wise choice. Stability over hype." to true,
            "Speaking of which — any third-party libs to drop?" to false,
            "Removing RxJava. Full coroutines now." to true,
            "Finally! RxJava was a headache to onboard." to false,
            "The learning curve was brutal." to true,
            "Coroutines are so much more intuitive." to false,
            "And structured concurrency is a lifesaver." to true,
            "No more forgotten CompositeDisposable calls!" to false,
            "Exactly. No leaks since the migration." to true,
            "How long did the migration take?" to false,
            "Two sprints. Mostly mechanical but tedious." to true,
            "Worth it in the long run." to false,
            "100%. Codebase feels much lighter." to true,
            "Any new Compose features you're excited about?" to false,
            "Predictive back gesture support is top of mind." to true,
            "That animation looks so smooth in the demos." to false,
            "It does! Added it yesterday. Want to see?" to true,
            "Yes please!" to false,
            "Check the latest commit on the ui-polish branch." to true,
            "Just pulled. That back swipe feels premium." to false,
            "Glad you like it! Merging to main tonight." to true,
        )

        val result = mutableListOf<ChatMessage>()

        for (i in 0 until 80) {
            val (text, isMine) = textMessages[i]
            result.add(
                ChatMessage(
                    id = "${i + 1}",
                    content = MessageContent.Html(text),
                    senderName = if (isMine) "Me" else "Alice",
                    isMine = isMine,
                    timestamp = clockAt(i),
                    createdAt = createdAt(i)
                )
            )
        }

        // ── Messages 81–100: rich content ────────────────────────────────────
        result.addAll(
            listOf(
                ChatMessage(
                    id = "81",
                    content = MessageContent.Html("Can't wait to show the team. Could you also share the updated team roster?"),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(80), createdAt = createdAt(80)
                ),
                ChatMessage(
                    id = "82",
                    content = MessageContent.Html("Sure! Here it is:"),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(81), createdAt = createdAt(81)
                ),
                ChatMessage(
                    id = "83",
                    content = MessageContent.Table(
                        title = "Team Members",
                        headers = listOf("Name", "Role", "Department", "Status"),
                        rows = listOf(
                            listOf("Alice Martin",  "Lead",      "Android", "Active"),
                            listOf("Bob Chen",      "Developer", "Android", "Active"),
                            listOf("Carol Davis",   "Designer",  "UX",      "Active"),
                            listOf("David Kim",     "QA",        "Testing", "On Leave"),
                            listOf("Eva Rossi",     "PM",        "Product", "Active")
                        )
                    ),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(82), createdAt = createdAt(82)
                ),
                ChatMessage(
                    id = "84",
                    content = MessageContent.Html("Thanks! Can you also share the Q1 sales figures?"),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(83), createdAt = createdAt(83)
                ),
                ChatMessage(
                    id = "85",
                    content = MessageContent.Table(
                        title = "Q1 Sales Report",
                        headers = listOf("Month", "Units", "Revenue", "Growth"),
                        rows = listOf(
                            listOf("January",  "1,240", "\$62,000", "+12%"),
                            listOf("February", "1,480", "\$74,000", "+19%"),
                            listOf("March",    "1,750", "\$87,500", "+18%")
                        )
                    ),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(84), createdAt = createdAt(84)
                ),
                ChatMessage(
                    id = "86",
                    content = MessageContent.Html("<i>Impressive numbers!</i> March was especially <b>strong</b>. See the full report at <a href=\"https://example.com/report\">example.com/report</a>."),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(85), createdAt = createdAt(85)
                ),
                ChatMessage(
                    id = "87",
                    content = MessageContent.Html("Here's a photo from last week's team outing!"),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(86), createdAt = createdAt(86)
                ),
                ChatMessage(
                    id = "88",
                    content = MessageContent.Image(url = "https://picsum.photos/seed/office/400/220"),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(87), createdAt = createdAt(87)
                ),
                ChatMessage(
                    id = "89",
                    content = MessageContent.Image(
                        url = "https://picsum.photos/seed/chart/400/220",
                        caption = "Q1 growth chart — numbers look good!"
                    ),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(88), createdAt = createdAt(88)
                ),
                ChatMessage(
                    id = "90",
                    content = MessageContent.Html("Love the visuals! Can you send the voice note from the last meeting?"),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(89), createdAt = createdAt(89)
                ),
                ChatMessage(
                    id = "91",
                    content = MessageContent.Html("Of course! Here you go:"),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(90), createdAt = createdAt(90)
                ),
                ChatMessage(
                    id = "92",
                    content = MessageContent.Audio(
                        url = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Sevish_-__VolQres.ogg",
                        title = "Meeting summary — March 8",
                        durationLabel = "1:04"
                    ),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(91), createdAt = createdAt(91)
                ),
                ChatMessage(
                    id = "93",
                    content = MessageContent.Html("Perfect, thanks! Here's my voice reply."),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(92), createdAt = createdAt(92)
                ),
                ChatMessage(
                    id = "94",
                    content = MessageContent.Audio(
                        url = "https://commondatastorage.googleapis.com/codeskulptor-demos/pyman_assets/intromusic.ogg",
                        title = "Voice note",
                        durationLabel = "0:22"
                    ),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(93), createdAt = createdAt(93)
                ),
                ChatMessage(
                    id = "95",
                    content = MessageContent.Html("Got it! See you in the standup."),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(94), createdAt = createdAt(94)
                ),
                ChatMessage(
                    id = "96",
                    content = MessageContent.Html("Looking forward to it! Great work today."),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(95), createdAt = createdAt(95)
                ),
                ChatMessage(
                    id = "97",
                    content = MessageContent.Html("Thank you! Really happy with how this sprint went."),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(96), createdAt = createdAt(96)
                ),
                ChatMessage(
                    id = "98",
                    content = MessageContent.Html("Me too. Let's keep up the momentum next sprint!"),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(97), createdAt = createdAt(97)
                ),
                ChatMessage(
                    id = "99",
                    content = MessageContent.Html("Absolutely! I'll have the final PR ready by Thursday."),
                    senderName = "Alice", isMine = false,
                    timestamp = clockAt(98), createdAt = createdAt(98)
                ),
                ChatMessage(
                    id = "100",
                    content = MessageContent.Html("Awesome. Talk soon!"),
                    senderName = "Me", isMine = true,
                    timestamp = clockAt(99), createdAt = createdAt(99)
                ),
            )
        )

        return result
    }
}