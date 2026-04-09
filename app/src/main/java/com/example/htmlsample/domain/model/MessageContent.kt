package com.example.htmlsample.domain.model

sealed class MessageContent {

    /** Rich text from server — supports <b>, <i>, <a>, <ul>, <ol>, <li>, etc. */
    data class Html(val html: String) : MessageContent()

    /**
     * Tabular data from server.
     * [headers] — column titles.
     * [rows]    — each inner list is one row; cell count must match headers.
     */
    data class Table(
        val title: String,
        val headers: List<String>,
        val rows: List<List<String>>
    ) : MessageContent()
}