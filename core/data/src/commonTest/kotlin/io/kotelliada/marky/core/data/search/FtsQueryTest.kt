package io.kotelliada.marky.core.data.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class FtsQueryTest {

    private fun match(query: String) = FtsQuery.from(query)?.matchQuery

    @Test
    fun `query without letters or digits gives no query`() {
        listOf("", "   ", "\n\t", "***", "\"\"", "-:()^", "👍🏽", "👨‍👩‍👧‍👦", "​").forEach { query ->
            assertThat(FtsQuery.from(query), name = "query=\"$query\"").isNull()
        }
    }

    @Test
    fun `each word becomes a prefix term and title query ORs them on the title column`() {
        assertThat(FtsQuery.from("hotel lisbon")).isEqualTo(
            FtsQuery(matchQuery = "hotel* lisbon*", titleMatchQuery = "title:hotel* OR title:lisbon*"),
        )
    }

    @Test
    fun `fts syntax characters cannot reach the match expression`() {
        assertThat(match("title:\"foo\" -bar (baz)* ^qux NEAR/3"))
            .isEqualTo("title* foo* bar* baz* qux* near* 3*")
    }

    @Test
    fun `operator words are lowercased so they are searched for`() {
        assertThat(match("cats AND dogs OR NOT birds")).isEqualTo("cats* and* dogs* or* not* birds*")
    }

    @Test
    fun `any non letter or digit separates words`() {
        assertThat(match("v1.2\tsnake_case\nhot👍🏽dog")).isEqualTo("v1* 2* snake* case* hot* dog*")
    }

    @Test
    fun `repeated words in any case are searched once`() {
        assertThat(match("Hotel hotel HOTEL")).isEqualTo("hotel*")
    }

    @Test
    fun `non latin scripts and accented letters stay words`() {
        assertThat(match("Café 日本語 Привет مرحبا")).isEqualTo("café* 日本語* привет* مرحبا*")
    }

    @Test
    fun `decomposed accent ends the word at the combining mark`() {
        // unicode61 strips the mark when indexing, so "cafe" still finds "café".
        assertThat(match("café")).isEqualTo("cafe*")
    }
}
