package com.example.htmlsample.domain.model

sealed class MessageContent {

    /** Rich text — supports <b>, <i>, <a>, <ul>, <ol>, <li>, etc. */
    data class Html(val html: String) : MessageContent()

    /** Tabular data. Each inner list in [rows] maps 1-to-1 with [headers]. */
    data class Table(
        val title: String,
        val headers: List<String>,
        val rows: List<List<String>>
    ) : MessageContent()

    /** A remote image with an optional caption. */
    data class Image(
        val url: String,
        val caption: String = ""
    ) : MessageContent()

    /** A streamable audio clip. [durationLabel] is display-only, e.g. "1:04". */
    data class Audio(
        val url: String,
        val title: String,
        val durationLabel: String
    ) : MessageContent()
}
