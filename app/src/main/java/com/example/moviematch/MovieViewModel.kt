package com.example.moviematch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MovieViewModel : ViewModel() {

    val genres = listOf(
        "Horror",
        "Thriller",
        "Comedy",
        "Drama"
    )

    private val movies = listOf(

        Movie("The Shining", "Horror", 146, 8.4),
        Movie("Misery", "Horror", 107, 7.8),
        Movie("It", "Horror", 135, 7.3),
        Movie("The Conjuring", "Horror", 112, 7.5),
        Movie("Hereditary", "Horror", 127, 7.3),
        Movie("A Quiet Place", "Horror", 90, 7.5),
        Movie("Sinister", "Horror", 110, 6.8),
        Movie("Smile", "Horror", 115, 6.5),
        Movie("The Ring", "Horror", 115, 7.1),
        Movie("Get Out", "Horror", 104, 7.8),
        Movie("The Others", "Horror", 104, 7.6),
        Movie("Scream", "Horror", 111, 7.4),

        Movie("Gone Girl", "Thriller", 149, 8.1),
        Movie("Prisoners", "Thriller", 153, 8.2),
        Movie("The Menu", "Thriller", 107, 7.2),
        Movie("Se7en", "Thriller", 127, 8.6),
        Movie("Shutter Island", "Thriller", 138, 8.2),
        Movie("Nightcrawler", "Thriller", 117, 7.8),
        Movie("Zodiac", "Thriller", 157, 7.7),
        Movie("Split", "Thriller", 117, 7.3),
        Movie("Searching", "Thriller", 102, 7.6),
        Movie("The Invisible Man", "Thriller", 124, 7.1),
        Movie("Panic Room", "Thriller", 112, 6.8),
        Movie("The Gift", "Thriller", 108, 7.0),

        Movie("Shrek", "Comedy", 90, 7.9),
        Movie("Superbad", "Comedy", 113, 7.6),
        Movie("The Hangover", "Comedy", 100, 7.7),
        Movie("Mean Girls", "Comedy", 97, 7.1),
        Movie("Game Night", "Comedy", 100, 6.9),
        Movie("Palm Springs", "Comedy", 90, 7.4),
        Movie("The Nice Guys", "Comedy", 116, 7.4),
        Movie("Bridesmaids", "Comedy", 125, 6.8),
        Movie("Easy A", "Comedy", 92, 7.0),
        Movie("Crazy Stupid Love", "Comedy", 118, 7.4),
        Movie("Booksmart", "Comedy", 102, 7.1),
        Movie("21 Jump Street", "Comedy", 109, 7.2),

        Movie("Interstellar", "Drama", 169, 8.7),
        Movie("Whiplash", "Drama", 106, 8.5),
        Movie("Fight Club", "Drama", 139, 8.8),
        Movie("The Social Network", "Drama", 120, 7.8),
        Movie("Black Swan", "Drama", 108, 8.0),
        Movie("The Father", "Drama", 97, 8.2),
        Movie("Marriage Story", "Drama", 137, 7.9),
        Movie("Good Will Hunting", "Drama", 126, 8.3),
        Movie("The Truman Show", "Drama", 103, 8.2),
        Movie("Joker", "Drama", 122, 8.4),
        Movie("Room", "Drama", 118, 8.1),
        Movie("Her", "Drama", 126, 8.0)
    )

    var herGenre by mutableStateOf("Horror")
        private set

    var hisGenre by mutableStateOf("Thriller")
        private set

    var candidateMovies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var herScores by mutableStateOf<Map<String, Int>>(emptyMap())
        private set

    var hisScores by mutableStateOf<Map<String, Int>>(emptyMap())
        private set

    var winner by mutableStateOf<Movie?>(null)
        private set

    var winnerScore by mutableStateOf(0.0)
        private set


    fun updateHerGenre(genre: String) {
        herGenre = genre
    }


    fun updateHisGenre(genre: String) {
        hisGenre = genre
    }


    fun findMovies(maxDuration: Int) {

        val herMovies = movies
            .filter {
                it.duration <= maxDuration &&
                        it.genre == herGenre
            }
            .sortedByDescending {
                it.rating
            }
            .take(10)

        val hisMovies = movies
            .filter {
                it.duration <= maxDuration &&
                        it.genre == hisGenre
            }
            .sortedByDescending {
                it.rating
            }
            .take(10)

        candidateMovies =
            (herMovies + hisMovies)
                .distinctBy { it.title }

        herScores =
            candidateMovies.associate {
                it.title to 5
            }

        hisScores =
            candidateMovies.associate {
                it.title to 5
            }

        winner = null
        winnerScore = 0.0
    }


    fun setHerScore(movie: Movie, score: Int) {

        herScores =
            herScores + (
                    movie.title to score.coerceIn(1, 10)
                    )

        winner = null
    }


    fun setHisScore(movie: Movie, score: Int) {

        hisScores =
            hisScores + (
                    movie.title to score.coerceIn(1, 10)
                    )

        winner = null
    }


    fun chooseWinner() {

        if (candidateMovies.isEmpty()) {
            return
        }

        val bestMovie =
            candidateMovies.maxByOrNull { movie ->

                val herScore =
                    herScores[movie.title] ?: 5

                val hisScore =
                    hisScores[movie.title] ?: 5

                (herScore + hisScore) / 2.0
            }

        if (bestMovie != null) {

            winner = bestMovie

            winnerScore =
                (
                        (herScores[bestMovie.title] ?: 5) +
                                (hisScores[bestMovie.title] ?: 5)
                        ) / 2.0
        }
    }


    fun reset() {

        candidateMovies = emptyList()

        herScores = emptyMap()
        hisScores = emptyMap()

        winner = null
        winnerScore = 0.0
    }
}