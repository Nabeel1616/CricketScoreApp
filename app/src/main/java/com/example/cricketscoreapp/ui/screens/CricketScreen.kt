package com.example.cricketscoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketscoreapp.ui.components.*

@Composable
fun CricketScreen(
    initialTeamA: String,
    initialTeamB: String,
    initialVenue: String,
    initialOvers: String,
    initialStriker: String,
    initialNonStriker: String,
    initialBowler: String
) {
    var venue by remember { mutableStateOf(initialVenue) }
    var teamA by remember { mutableStateOf(initialTeamA) }
    var teamB by remember { mutableStateOf(initialTeamB) }
    var totalOversLimit by remember { mutableStateOf(initialOvers) }

    // Batsmen Stats
    var strikerName by remember { mutableStateOf(initialStriker) }
    var strikerRuns by remember { mutableStateOf(0) }
    var strikerBalls by remember { mutableStateOf(0) }

    var nonStrikerName by remember { mutableStateOf(initialNonStriker) }
    var nonStrikerRuns by remember { mutableStateOf(0) }
    var nonStrikerBalls by remember { mutableStateOf(0) }

    // Bowler Stats
    var bowlerName by remember { mutableStateOf(initialBowler) }
    var bowlerRunsConceded by remember { mutableStateOf(0) }
    var bowlerWickets by remember { mutableStateOf(0) }
    var bowlerBallsBowled by remember { mutableStateOf(0) }

    // Dialog States for Wicket, Over Change, Manual Bowler Change, and Editing Names
    var showNewBatsmanDialog by remember { mutableStateOf(false) }
    var newBatsmanInput by remember { mutableStateOf("") }

    var showNewBowlerDialog by remember { mutableStateOf(false) }
    var newBowlerInput by remember { mutableStateOf("") }

    var showEditBatsmanDialog by remember { mutableStateOf(false) }
    var editingTargetIsStriker by remember { mutableStateOf(true) }
    var editBatsmanInput by remember { mutableStateOf("") }

    // Team Totals
    var totalRuns by remember { mutableStateOf(0) }
    var totalWickets by remember { mutableStateOf(0) }
    var totalBalls by remember { mutableStateOf(0) }
    val ballHistory = remember { mutableStateListOf<String>() }

    val overs = totalBalls / 6
    val currentBall = totalBalls % 6
    val maxOvers = totalOversLimit.toIntOrNull() ?: 5
    val isMatchOver = overs >= maxOvers || totalWickets >= 10

    val runRate = if (totalBalls > 0) String.format("%.2f", (totalRuns.toFloat() / totalBalls) * 6) else "0.00"

    // Strike Swap Function
    fun swapStrike() {
        val tempName = strikerName
        strikerName = nonStrikerName
        nonStrikerName = tempName

        val tempRuns = strikerRuns
        strikerRuns = nonStrikerRuns
        nonStrikerRuns = tempRuns

        val tempBalls = strikerBalls
        strikerBalls = nonStrikerBalls
        nonStrikerBalls = tempBalls
    }

    fun addBallEvent(label: String, runsScored: Int, isLegalBall: Boolean = true, isWicket: Boolean = false, isExtra: Boolean = false) {
        if (isMatchOver) return

        totalRuns += runsScored
        bowlerRunsConceded += runsScored

        if (!isExtra) {
            strikerRuns += runsScored
        }

        if (isLegalBall) {
            strikerBalls += 1
            totalBalls += 1
            bowlerBallsBowled += 1
        }

        if (isWicket && totalWickets < 10) {
            totalWickets += 1
            bowlerWickets += 1
            showNewBatsmanDialog = true
        }

        ballHistory.add(label)

        if (!isExtra && runsScored % 2 != 0 && !isWicket) {
            swapStrike()
        }

        if (isLegalBall && totalBalls % 6 == 0 && totalBalls > 0 && !isMatchOver) {
            swapStrike()
            showNewBowlerDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🏏 PROFESSIONAL CRICKET SCORER",
                    color = Color(0xFF00E676),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "$teamA vs $teamB | Venue: $venue", color = Color.Yellow, fontSize = 11.sp)
            }

            ScoreCardSection(
                teamName = teamA,
                runs = totalRuns,
                wickets = totalWickets,
                overs = "$overs.$currentBall / $maxOvers",
                runRate = runRate
            )

            // Crease Stats Card with Edit & Strike Controls
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xCC111C24)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📊 Live Crease & Bowler", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        // Strike Change Button
                        OutlinedButton(
                            onClick = { swapStrike() },
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81D4FA))
                        ) {
                            Text(text = "🔄 Change Strike", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Striker Row with Edit Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏏 $strikerName *", color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            TextButton(
                                onClick = {
                                    editingTargetIsStriker = true
                                    editBatsmanInput = strikerName
                                    showEditBatsmanDialog = true
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(20.dp)
                            ) {
                                Text(text = "[Edit]", fontSize = 10.sp, color = Color(0xFF81D4FA))
                            }
                        }
                        Text(text = "$strikerRuns runs ($strikerBalls b)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Non-Striker Row with Edit Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏃 $nonStrikerName", color = Color.LightGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            TextButton(
                                onClick = {
                                    editingTargetIsStriker = false
                                    editBatsmanInput = nonStrikerName
                                    showEditBatsmanDialog = true
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(20.dp)
                            ) {
                                Text(text = "[Edit]", fontSize = 10.sp, color = Color(0xFF81D4FA))
                            }
                        }
                        Text(text = "$nonStrikerRuns runs ($nonStrikerBalls b)", color = Color.LightGray, fontSize = 12.sp)
                    }

                    Divider(color = Color.Gray, thickness = 0.4.dp)

                    // Bowler Row with Manual Change Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚾ $bowlerName", color = Color(0xFFFFB74D), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(6.dp))
                            TextButton(
                                onClick = { showNewBowlerDialog = true },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(20.dp)
                            ) {
                                Text(text = "[Change Bowler]", fontSize = 10.sp, color = Color(0xFF81D4FA))
                            }
                        }
                        val bOvers = bowlerBallsBowled / 6
                        val bBalls = bowlerBallsBowled % 6
                        Text(text = "$bOvers.$bBalls ov | $bowlerRunsConceded R | $bowlerWickets W", color = Color(0xFFFFB74D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (isMatchOver) {
                Text(text = "🚨 INNINGS / MATCH OVER! 🚨", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (ballHistory.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text("Recent Balls:", color = Color.LightGray, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(ballHistory.takeLast(10)) { ball ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        when (ball) {
                                            "4" -> Color(0xFF0288D1)
                                            "6" -> Color(0xFF388E3C)
                                            "W" -> Color(0xD3D32F2F)
                                            else -> Color(0xFF424242)
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = ball, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            ScoringButtonsGrid(
                onRunClick = { label, r -> addBallEvent(label, r) },
                onExtraClick = { label, r, legal -> addBallEvent(label, r, isLegalBall = legal, isExtra = true) },
                onWicketClick = { addBallEvent("W", 0, isWicket = true) }
            )

            OutlinedButton(
                onClick = {
                    totalRuns = 0; totalWickets = 0; totalBalls = 0
                    strikerRuns = 0; strikerBalls = 0
                    nonStrikerRuns = 0; nonStrikerBalls = 0
                    bowlerRunsConceded = 0; bowlerWickets = 0; bowlerBallsBowled = 0
                    ballHistory.clear()
                },
                modifier = Modifier.fillMaxWidth().height(36.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252))
            ) {
                Text("RESET MATCH", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }

    // --- DIALOG: NEW BATSMAN ON WICKET ---
    if (showNewBatsmanDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("☝️ WICKET! Enter New Batsman") },
            text = {
                OutlinedTextField(
                    value = newBatsmanInput,
                    onValueChange = { newBatsmanInput = it },
                    label = { Text("New Batsman Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBatsmanInput.isNotBlank()) {
                            strikerName = newBatsmanInput
                            strikerRuns = 0
                            strikerBalls = 0
                            newBatsmanInput = ""
                        }
                        showNewBatsmanDialog = false
                    }
                ) {
                    Text("Bring to Crease")
                }
            }
        )
    }

    // --- DIALOG: EDIT BATSMAN NAME ---
    if (showEditBatsmanDialog) {
        AlertDialog(
            onDismissRequest = { showEditBatsmanDialog = false },
            title = { Text("✏️ Edit Batsman Name") },
            text = {
                OutlinedTextField(
                    value = editBatsmanInput,
                    onValueChange = { editBatsmanInput = it },
                    label = { Text("Batsman Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editBatsmanInput.isNotBlank()) {
                            if (editingTargetIsStriker) {
                                strikerName = editBatsmanInput
                            } else {
                                nonStrikerName = editBatsmanInput
                            }
                        }
                        showEditBatsmanDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBatsmanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- DIALOG: NEW / CHANGE BOWLER ---
    if (showNewBowlerDialog) {
        AlertDialog(
            onDismissRequest = { showNewBowlerDialog = false },
            title = { Text("🔄 Change / Select Bowler") },
            text = {
                OutlinedTextField(
                    value = newBowlerInput,
                    onValueChange = { newBowlerInput = it },
                    label = { Text("Bowler Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBowlerInput.isNotBlank()) {
                            bowlerName = newBowlerInput
                            bowlerRunsConceded = 0
                            bowlerWickets = 0
                            bowlerBallsBowled = 0
                            newBowlerInput = ""
                        }
                        showNewBowlerDialog = false
                    }
                ) {
                    Text("Set Bowler")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewBowlerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}