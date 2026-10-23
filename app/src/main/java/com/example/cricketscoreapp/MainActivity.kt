package com.example.cricketscoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.cricketscoreapp.ui.screens.CricketScreen
import com.example.cricketscoreapp.ui.screens.MatchSetupScreen
import com.example.cricketscoreapp.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("splash") }

            var teamAData by remember { mutableStateOf("Team A") }
            var teamBData by remember { mutableStateOf("Team B") }
            var venueData by remember { mutableStateOf("City Cricket Ground") }
            var oversData by remember { mutableStateOf("5") }
            var strikerData by remember { mutableStateOf("Batsman 1") }
            var nonStrikerData by remember { mutableStateOf("Batsman 2") }
            var bowlerData by remember { mutableStateOf("Bowler 1") }

            when (currentScreen) {
                "splash" -> {
                    SplashScreen {
                        currentScreen = "setup"
                    }
                }
                "setup" -> {
                    MatchSetupScreen { tA, tB, v, o, str, nonStr, bowl ->
                        teamAData = tA
                        teamBData = tB
                        venueData = v
                        oversData = o
                        strikerData = str
                        nonStrikerData = nonStr
                        bowlerData = bowl
                        currentScreen = "scorer"
                    }
                }
                "scorer" -> {
                    CricketScreen(
                        initialTeamA = teamAData,
                        initialTeamB = teamBData,
                        initialVenue = venueData,
                        initialOvers = oversData,
                        initialStriker = strikerData,
                        initialNonStriker = nonStrikerData,
                        initialBowler = bowlerData
                    )
                }
            }
        }
    }
}