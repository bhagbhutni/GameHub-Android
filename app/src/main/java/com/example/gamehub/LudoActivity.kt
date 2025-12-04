// Path: app/src/main/java/com/example/gamehub/LudoActivity.kt
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

class LudoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameHubTheme {
                LudoGame(
                    onBack = { finish() },
                    onGameComplete = { winnerName ->
                        // Update statistics
                        val statsManager = StatsManager(this)
                        val stats = statsManager.loadStats()
                        val updatedWins = stats.ludoWins.toMutableMap()
                        updatedWins[winnerName] = (updatedWins[winnerName] ?: 0) + 1
                        val updatedStats = stats.copy(
                            ludoGamesPlayed = stats.ludoGamesPlayed + 1,
                            ludoWins = updatedWins
                        )
                        statsManager.saveStats(updatedStats)
                    }
                )
            }
        }
    }
}

@Composable
fun LudoGame(
    onBack: () -> Unit,
    onGameComplete: (String) -> Unit
) {
    // ============================================
    // PASTE YOUR LUDO GAME CODE HERE
    // ============================================
    
    // TEMPLATE - Replace this with your actual game
    // When a player wins, call: onGameComplete("Player 1") or onGameComplete("AI 2")
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
                text = "🎲",
                fontSize = 80.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ludo Game",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paste your Ludo game code here",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Example back button
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("← Back to Menu")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Test buttons - Remove these after adding your game
            Button(
                onClick = { onGameComplete("Player 1") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Test: Player 1 Wins")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { onGameComplete("AI 2") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Test: AI 2 Wins")
            }
        }
    }
}
