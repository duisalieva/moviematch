package com.example.moviematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {

    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    MovieMatchScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun MovieMatchScreen(
    viewModel: MovieViewModel
) {

    var durationText by rememberSaveable {
        mutableStateOf("120")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                text = "Movie Match",
                style = MaterialTheme.typography.headlineMedium
            )
        }


        item {

            OutlinedTextField(
                value = durationText,
                onValueChange = {
                    durationText = it
                },
                label = {
                    Text("Maximum duration in minutes")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }


        item {

            GenreSelector(
                title = "Her genre",
                genres = viewModel.genres,
                selectedGenre = viewModel.herGenre,
                onGenreSelected = {
                    viewModel.updateHerGenre(it)
                }
            )
        }


        item {

            GenreSelector(
                title = "His genre",
                genres = viewModel.genres,
                selectedGenre = viewModel.hisGenre,
                onGenreSelected = {
                    viewModel.updateHisGenre(it)
                }
            )
        }


        item {

            Button(
                onClick = {

                    val duration =
                        durationText.toIntOrNull()

                    if (
                        duration != null &&
                        duration > 0
                    ) {

                        viewModel.findMovies(
                            duration
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Find Movies")
            }
        }


        if (viewModel.candidateMovies.isEmpty()) {

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        "Choose duration and genres, then press Find Movies."
                    )
                }
            }

        } else {

            item {

                Text(
                    text = "Movies",
                    style = MaterialTheme.typography.titleLarge
                )
            }


            items(
                viewModel.candidateMovies,
                key = { movie ->
                    movie.title
                }
            ) { movie ->

                MovieCard(
                    movie = movie,

                    herScore =
                        viewModel.herScores[movie.title]
                            ?: 5,

                    hisScore =
                        viewModel.hisScores[movie.title]
                            ?: 5,

                    onHerScoreChange = { score ->

                        viewModel.setHerScore(
                            movie,
                            score
                        )
                    },

                    onHisScoreChange = { score ->

                        viewModel.setHisScore(
                            movie,
                            score
                        )
                    }
                )
            }


            item {

                Button(
                    onClick = {
                        viewModel.chooseWinner()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("Choose Winner")
                }
            }


            val winner = viewModel.winner

            if (winner != null) {

                item {

                    WinnerCard(
                        movie = winner,
                        averageScore =
                            viewModel.winnerScore
                    )
                }


                item {

                    OutlinedButton(
                        onClick = {
                            viewModel.reset()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Start Again")
                    }
                }
            }
        }
    }
}


@Composable
fun GenreSelector(
    title: String,
    genres: List<String>,
    selectedGenre: String,
    onGenreSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Column {

        Text(title)

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Box {

            OutlinedButton(
                onClick = {
                    expanded = true
                }
            ) {

                Text(selectedGenre)
            }


            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                genres.forEach { genre ->

                    DropdownMenuItem(
                        text = {
                            Text(genre)
                        },
                        onClick = {

                            onGenreSelected(
                                genre
                            )

                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ScoreSelector(
    name: String,
    score: Int,
    onScoreChange: (Int) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(name)

            Text("$score / 10")
        }


        Slider(
            value = score.toFloat(),

            onValueChange = { value ->

                onScoreChange(
                    value
                        .roundToInt()
                        .coerceIn(1, 10)
                )
            },

            valueRange = 1f..10f,

            steps = 8,

            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun MovieCard(
    movie: Movie,
    herScore: Int,
    hisScore: Int,
    onHerScoreChange: (Int) -> Unit,
    onHisScoreChange: (Int) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium
            )


            Text(
                text =
                    "${movie.genre} • ${movie.duration} min • Rating ${movie.rating}"
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            ScoreSelector(
                name = "Her score",
                score = herScore,
                onScoreChange =
                    onHerScoreChange
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            ScoreSelector(
                name = "His score",
                score = hisScore,
                onScoreChange =
                    onHisScoreChange
            )
        }
    }
}


@Composable
fun WinnerCard(
    movie: Movie,
    averageScore: Double
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Winner",
                style = MaterialTheme.typography.titleMedium
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            Text(
                text = movie.title
            )


            Text(
                text = "Average score: $averageScore"
            )
        }
    }
}