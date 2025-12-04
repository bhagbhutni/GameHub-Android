// Path: app/src/main/java/com/example/gamehub/MainActivity.kt
package com.example.gamehub

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameHubTheme {
                GameHubMainMenu()
            }
        }
    }
}

@Composable
fun GameHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFFF57C00)
        ),
        content = content
    )
}

data class GameStats(
    var ticTacToeGamesPlayed: Int = 0,
    var ticTacToeXWins: Int = 0,
    var ticTacToeOWins: Int = 0,
    var ticTacToeDraws: Int = 0,
    var ludoGamesPlayed: Int = 0,
    var ludoWins: Map<String, Int> = emptyMap()
)

class StatsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("GameHubStats", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveStats(stats: GameStats) {
        val json = gson.toJson(stats)
        prefs.edit().putString("stats", json).apply()
    }
    
    fun loadStats(): GameStats {
        val json = prefs.getString("stats", null) ?: return GameStats()
        return try {
            gson.fromJson(json, GameStats::class.java)
        } catch (e: Exception) {
            GameStats()
        }
    }
}

class FeedbackManager(private val context: Context) {
    private var soundPlayer: MediaPlayer? = null
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun playClick() {
        try {
            soundPlayer?.release()
            soundPlayer = MediaPlayer.create(context, android.R.raw.ic_menu_camera)
            soundPlayer?.setVolume(0.3f, 0.3f)
            soundPlayer?.start()
        } catch (e: Exception) {}
        vibrateLight()
    }

    private fun vibrateLight() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, 50))
        } else {
            vibrator.vibrate(20)
        }
    }

    fun release() {
        soundPlayer?.release()
    }
}

@Composable
fun GameHubMainMenu() {
    val context = LocalContext.current
    val feedback = remember { FeedbackManager(context) }
    val statsManager = remember { StatsManager(context) }
    var stats by remember { mutableStateOf(statsManager.loadStats()) }
    var showStats by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        onDispose { 
            feedback.release()
            stats = statsManager.loadStats()
        }
    }
    
    LaunchedEffect(Unit) {
        stats = statsManager.loadStats()
    }
    
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D47A1),
            Color(0xFF1565C0),
            Color(0xFF1976D2),
            Color(0xFF1E88E5)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        if (showStats) {
            StatsScreen(
                stats = stats,
                onBack = { 
                    feedback.playClick()
                    showStats = false 
                },
                onResetStats = {
                    feedback.playClick()
                    stats = GameStats()
                    statsManager.saveStats(stats)
                }
            )
        } else {
            MainMenuScreen(
                stats = stats,
                onSelectTicTacToe = {
                    feedback.playClick()
                    context.startActivity(Intent(context, TicTacToeActivity::class.java))
                },
                onSelectLudo = {
                    feedback.playClick()
                    context.startActivity(Intent(context, LudoActivity::class.java))
                },
                onShowStats = {
                    feedback.playClick()
                    stats = statsManager.loadStats()
                    showStats = true
                }
            )
        }
    }
}

@Composable
fun MainMenuScreen(
    stats: GameStats,
    onSelectTicTacToe: () -> Unit,
    onSelectLudo: () -> Unit,
    onShowStats: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "title")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎮 GAME HUB",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.scale(titleScale),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Choose Your Game",
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        var card1Visible by remember { mutableStateOf(false) }
        var card2Visible by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            delay(200)
            card1Visible = true
            delay(200)
            card2Visible = true
        }
        
        AnimatedVisibility(
            visible = card1Visible,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
        ) {
            GameCard(
                title = "Tic-Tac-Toe",
                emoji = "⭕❌",
                description = "Classic 3×3 and 4×4 strategy",
                gamesPlayed = stats.ticTacToeGamesPlayed,
                color = Color(0xFF9C27B0),
                onClick = onSelectTicTacToe
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AnimatedVisibility(
            visible = card2Visible,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        ) {
            GameCard(
                title = "Ludo",
                emoji = "🎲",
                description = "Multiplayer board game with AI",
                gamesPlayed = stats.ludoGamesPlayed,
                color = Color(0xFF4CAF50),
                onClick = onSelectLudo
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onShowStats,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "📊 View Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun GameCard(
    title: String,
    emoji: String,
    description: String,
    gamesPlayed: Int,
    color: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Played: $gamesPlayed games",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun StatsScreen(
    stats: GameStats,
    onBack: () -> Unit,
    onResetStats: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "← Back",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Text(
                text = "📊 Statistics",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            IconButton(onClick = { showResetDialog = true }) {
                Text("🗑️", fontSize = 24.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        StatsCard(
            title = "⭕❌ Tic-Tac-Toe",
            stats = listOf(
                "Games Played" to stats.ticTacToeGamesPlayed.toString(),
                "X Wins" to stats.ticTacToeXWins.toString(),
                "O Wins" to stats.ticTacToeOWins.toString(),
                "Draws" to stats.ticTacToeDraws.toString()
            ),
            color = Color(0xFF9C27B0)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val ludoStatsList = mutableListOf<Pair<String, String>>()
        ludoStatsList.add("Games Played" to stats.ludoGamesPlayed.toString())
        stats.ludoWins.forEach { (player, wins) ->
            ludoStatsList.add("$player Wins" to wins.toString())
        }
        
        StatsCard(
            title = "🎲 Ludo",
            stats = ludoStatsList,
            color = Color(0xFF4CAF50)
        )
        
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Statistics?") },
                text = { Text("This will delete all game statistics. This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onResetStats()
                            showResetDialog = false
                        }
                    ) {
                        Text("Reset", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    stats: List<Pair<String, String>>,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            stats.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = value,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                if (stats.last() != label to value) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}