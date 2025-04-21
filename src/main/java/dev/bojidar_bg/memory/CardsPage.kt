package dev.bojidar_bg.memory

import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import generated.images
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random
import android.media.SoundPool
import android.media.AudioAttributes


data class Card(val value: Int, val image: Int, val guessed: Boolean)

@Composable
fun CardsPage() {
    val context = LocalContext.current
    val soundpool = remember {
        SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
            .setContext(context)
            .build()
    }
    val flipCardSound = remember { soundpool.load(context, R.raw.rollover3, 1) }
    val wrongSound = remember { soundpool.load(context, R.raw.click1, 1) }
    val correctSound = remember { soundpool.load(context, R.raw.switch35, 1) }
    val audioAmplitude = 0.5f
    val cardShape = RoundedCornerShape(10.dp)
    val gameKey = remember { mutableStateOf(0) }
    val size = 12
    val height = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 4
    val width = size / height
    // val size = width * height
    val matching = 2
    val cardColor =
        remember(gameKey.value) { ->
            Color(Random.nextFloat() * 0.5f + 0.5f, Random.nextFloat() * 0.5f + 0.5f, Random.nextFloat() * 0.5f + 0.5f)
        }
    val (backImage, cards) =
        remember(gameKey.value) { ->
            val imagesShuffled = images.shuffled().subList(0, size / matching + 1)
            Pair(
                imagesShuffled.last(),
                (0 until size).toList().map({ i ->
                    val v = i / matching
                    Card(v, imagesShuffled.get(v), false)
                }).shuffled().toMutableStateList(),
            )
        }
    val flippedCards = remember(gameKey.value) { mutableStateMapOf<Int, Boolean>() }
    val leftoverCards = remember(gameKey.value) { mutableStateOf(size) }
    val view = LocalView.current

    if (flippedCards.size == matching) {
        LaunchedEffect("flipper") {
            var firstValue = -1
            var wrong = false
            for (cardI in flippedCards.keys) {
                val value = cards.get(cardI).value
                if (firstValue == -1) {
                    firstValue = value
                } else if (value != firstValue) {
                    wrong = true
                }
            }
            delay(1500)
            if (!wrong) {
                for (cardI in flippedCards.keys) {
                    cards.set(cardI, cards.get(cardI).copy(guessed = true))
                    leftoverCards.value -= 1
                }
                soundpool.play(correctSound, audioAmplitude, audioAmplitude, 0, 0, 1.0f)
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                soundpool.play(wrongSound, audioAmplitude, audioAmplitude, 0, 0, 1.0f)
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            }
            flippedCards.clear()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            for (i in 0 until height) {
                Row(Modifier.fillMaxWidth().weight(1f), Arrangement.Center, Alignment.CenterVertically) {
                    for (j in 0 until width) {
                        val cardI = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) i * width + j else i + j * height
                        val card = cards.get(cardI)
                        val flipped = flippedCards.containsKey(cardI) || card.guessed

                        val animatedFlip = animateFloatAsState(if (flipped || card.guessed) 1f else -1f, label = "flip")
                        val animatedColor =
                            animateColorAsState(
                                if (card.guessed) Color.Transparent else cardColor,
                                tween(durationMillis = 500),
                                label = "color",
                            )

                        Box(
                            Modifier.weight(1f).padding(4.dp).graphicsLayer(scaleX = abs(animatedFlip.value)).clickable {
                                if (flipped) {
                                    // pass
                                } else if (flippedCards.size < matching) {
                                    soundpool.play(flipCardSound, audioAmplitude, audioAmplitude, 0, 0, 1.0f)
                                    view.performHapticFeedback(HapticFeedbackConstants.TOGGLE_ON)
                                    flippedCards.put(cardI, true)
                                }
                            }.border(3.dp, Color.White, cardShape).background(animatedColor.value, cardShape),
                        ) {
                            if (animatedFlip.value > 0) {
                                Image(painterResource(card.image), card.value.toString(), Modifier.padding(10.dp).fillMaxSize())
                            } else {
                                Image(painterResource(backImage), "Hidden card", Modifier.padding(30.dp).fillMaxSize(), alpha = 0.5f)
                            }
                        }
                    }
                }
            }
        }

        val animatedWin = animateFloatAsState(if (leftoverCards.value <= 0) 1f else 0f, label = "win")
        if (leftoverCards.value <= 0) {
            Row(Modifier.fillMaxSize().background(Color(0x66000000)), Arrangement.Center, Alignment.CenterVertically) {
                Button({
                    gameKey.value++
                }, Modifier.graphicsLayer(scaleX = animatedWin.value, scaleY = animatedWin.value)) {
                    Text(stringResource(R.string.new_game), Modifier.padding(40.dp), style = MaterialTheme.typography.h5)
                }
            }
        }
    }
}
