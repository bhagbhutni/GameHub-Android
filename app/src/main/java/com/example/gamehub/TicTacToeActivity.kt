// Path: app/src/main/java/com/example/gamehub/TicTacToeActivity.kt
package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TicTacToeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameHubTheme {
                TicTacToeGame(
                    onBack = { finish() },
                    onGameComplete = { winner ->
                        // Update statistics
                        val statsManager = StatsManager(this)
                        val stats = statsManager.loadStats()
                        val updatedStats = stats.copy(
                            ticTacToeGamesPlayed = stats.ticTacToeGamesPlayed + 1,
                            ticTacToeXWins = if (winner == "X") stats.ticTacToeXWins + 1 else stats.ticTacToeXWins,
                            ticTacToeOWins = if (winner == "O") stats.ticTacToeOWins + 1 else stats.ticTacToeOWins,
                            ticTacToeDraws = if (winner == "Draw") stats.ticTacToeDraws + 1 else stats.ticTacToeDraws
                        )
                        statsManager.saveStats(updatedStats)
                    }
                )
            }
        }
    }
}

@Composable
fun TicTacToeGame(
    onBack: () -> Unit,
    onGameComplete: (String) -> Unit
) {
    // ============================================
    // PASTE YOUR TIC-TAC-TOE GAME CODE HERE
    // ============================================
    
    // TEMPLATE - Replace this with your actual game
    // When a player wins, call: onGameComplete("X") or onGameComplete("O")
    // When it's a draw, call: onGameComplete("Draw")
    // For back button, call: onBack()
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⭕❌",
                fontSize = 80.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tic-Tac-Toe Game",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paste your Tic-Tac-Toe game code here",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Example back button
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C27B0)
                )
            ) {
                Text("← Back to Menu")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Test buttons - Remove these after adding your game
            Button(
                onClick = { onGameComplete("X") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Test: X Wins")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { onGameComplete("O") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Test: O Wins")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { onGameComplete("Draw") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Test: Draw")
            }
        }
    }
}
