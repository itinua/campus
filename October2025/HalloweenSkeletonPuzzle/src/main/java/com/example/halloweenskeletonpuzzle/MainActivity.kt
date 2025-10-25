package com.example.halloweenskeletonpuzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.halloweenskeletonpuzzle.ui.theme.CampusTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

val RoadRageFontFamily = FontFamily(
    Font(
        resId = R.font.roadrage_regular, weight = FontWeight.Normal
    )
)

data class Item(
    val img: Int,
    val top: Offset = Offset(0f, 0f),
    val bottom: Offset = Offset(0f, 0f),
    var size: Size = Size(0f, 0f),
) {
    fun isOverlap() = top.overlap(bottom)
    var isWrongOverlap = false

//    fun overlaps(second: Item) =
//        bottom.x in second.top.x..second.top.x + size.width &&
//                bottom.y in second.top.y..second.top.y + size.height

    fun overlaps(second: Item) = Rect(bottom,size).overlaps(Rect(second.top,size))


}

val padding = 10.dp

data class BonesState(
    val head: Item = Item(R.drawable.head),
    val armLeft: Item = Item(R.drawable.arm_left),
    val armRight: Item = Item(R.drawable.arm_right),
    val ribCage: Item = Item(R.drawable.rib_cage),
    val pelvis: Item = Item(R.drawable.pelvis),
    val legLeft: Item = Item(R.drawable.leg_left),
    val legRight: Item = Item(R.drawable.leg_right),
) {
    val listBody = listOf(head, armLeft, armRight, ribCage, pelvis, legLeft, legRight)

    fun updateBone(originalItem: Item, updatedItem: Item): BonesState {
        return when (originalItem) {
            head -> copy(head = updatedItem)
            armLeft -> copy(armLeft = updatedItem)
            armRight -> copy(armRight = updatedItem)
            ribCage -> copy(ribCage = updatedItem)
            pelvis -> copy(pelvis = updatedItem)
            legLeft -> copy(legLeft = updatedItem)
            legRight -> copy(legRight = updatedItem)
            else -> this
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CampusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xff1A0D39))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.bg),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            contentDescription = ""
                        )



                        Skeleton()


                    }

                }
            }
        }
    }
}

@Composable
fun Skeleton() {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var state by remember { mutableStateOf(BonesState()) }
        var resetTrigger by remember { mutableStateOf(false) }

        val update: (BonesState) -> Unit = {
            state = it
        }

        Spacer(modifier = Modifier.height(60.dp))


        val isAnimateHead = remember(state) {
            val res = state.listBody.all { it.isOverlap() }

            state.listBody.forEach { item ->
                if(item.isOverlap()){
                    item.isWrongOverlap = false
                }else
                item.isWrongOverlap = state.listBody.filter { it != item }.any {
                    item.overlaps(it)
                }
            }
            res
        }




        //Text("Size ${state.armRight}", color = Color.White)

        val animation = animateHead(isAnimateHead)

        ImageTop(
            state.head, modifier = Modifier
                .rotate(animation.value)
                .globalPosition(onMove = {
                    update(state.copy(head = state.head.copy(top = it)))
                }, onSizeChange = {
                    update(state.copy(head = state.head.copy(size = it.toSize())))
                })
        )
        Row {
            ImageTop(
                state.armLeft, modifier = Modifier.globalPosition(
                    onMove = {
                        update(state.copy(armLeft = state.armLeft.copy(top = it)))
                    }, onSizeChange = {
                        update(state.copy(armLeft = state.armLeft.copy(size = it.toSize())))
                    }
                )
            )

            Column {
                ImageTop(
                    state.ribCage, modifier = Modifier.globalPosition(
                        onMove = {
                            update(
                                state.copy(ribCage = state.ribCage.copy(top = it))
                            )
                        }, onSizeChange = {
                            update(state.copy(ribCage = state.ribCage.copy(size = it.toSize())))
                        })
                )
                ImageTop(
                    state.pelvis, modifier = Modifier.globalPosition(
                        onMove = {
                            update(state.copy(pelvis = state.pelvis.copy(top = it)))
                        }, onSizeChange = {
                            update(state.copy(pelvis = state.pelvis.copy(size = it.toSize())))
                        }
                    )
                )
            }

            ImageTop(
                state.armRight, modifier = Modifier.globalPosition(
                    onMove = {
                        update(state.copy(armRight = state.armRight.copy(top = it)))

                    }, onSizeChange = {
                        update(state.copy(armRight = state.armRight.copy(size = it.toSize())))
                    })
            )

        }
        Row {
            ImageTop(
                state.legLeft, modifier = Modifier.globalPosition(
                    onMove = {
                        update(state.copy(legLeft = state.legLeft.copy(top = it)))

                    }, onSizeChange = {
                        update(state.copy(legLeft = state.legLeft.copy(size = it.toSize())))
                    })
            )
            ImageTop(
                state.legRight, modifier = Modifier.globalPosition(
                    onMove = {
                        update(state.copy(legRight = state.legRight.copy(top = it)))
                    }, onSizeChange = {
                        update(state.copy(legRight = state.legRight.copy(size = it.toSize())))
                    })
            )
        }
        Spacer(modifier = Modifier.height(25.dp))


        Row {
            Column {
                Row {
                    ImageBottom(
                        state.legLeft,
                        modifier = Modifier
                            .drag(state.legLeft, resetTrigger)
                            .globalPosition(
                                onMove = {
                                    update(
                                        state.copy(
                                            legLeft = state.legLeft.copy(
                                                bottom = it
                                            )
                                        )
                                    )
                                })
                    )

                    Spacer(modifier = Modifier.size(padding))
                    ImageBottom(
                        state.armRight,
                        modifier = Modifier
                            .drag(state.armRight, resetTrigger)
                            .globalPosition(
                                onMove = {
                                    update(
                                        state.copy(
                                            armRight = state.armRight.copy(
                                                bottom = it
                                            )
                                        )
                                    )
                                })
                    )
                }
                Spacer(modifier = Modifier.size(padding))
                ImageBottom(
                    state.head, modifier = Modifier
                        .drag(state.head, resetTrigger)
                        .globalPosition(
                            onMove = {
                                update(
                                    state.copy(head = state.head.copy(bottom = it))
                                )

                            })
                )

            }
            Spacer(modifier = Modifier.size(padding))
            Column {
                ImageBottom(
                    state.ribCage,
                    modifier = Modifier
                        .drag(state.ribCage, resetTrigger)
                        .globalPosition(
                            onMove = {
                                update(
                                    state.copy(
                                        ribCage = state.ribCage.copy(bottom = it)
                                    )
                                )
                            })
                )
                Spacer(modifier = Modifier.size(padding))
                ImageBottom(
                    state.armLeft,
                    modifier = Modifier
                        .drag(state.armLeft, resetTrigger)
                        .globalPosition(
                            onMove = {
                                update(
                                    state.copy(
                                        armLeft = state.armLeft.copy(bottom = it)
                                    )
                                )

                            })
                )
            }

            Spacer(modifier = Modifier.size(padding))
            Column {
                ImageBottom(
                    state.legRight,
                    modifier = Modifier
                        .drag(state.legRight, resetTrigger)
                        .globalPosition(
                            onMove = {
                                update(
                                    state.copy(
                                        legRight = state.legRight.copy(bottom = it)
                                    )
                                )

                            })
                )
                Spacer(modifier = Modifier.size(padding))
                ImageBottom(
                    state.pelvis,
                    modifier = Modifier
                        .drag(state.pelvis, resetTrigger)
                        .globalPosition(
                            onMove = {
                                update(
                                    state.copy(
                                        pelvis = state.pelvis.copy(bottom = it)
                                    )
                                )
                            })
                )
            }


        }


        Spacer(modifier = Modifier.size(42.dp))

        val scope = rememberCoroutineScope()

        //  AnimatedVisibility(isAnimateHaed) {
        if (isAnimateHead) Button(
            onClick = {
                scope.launch {
                    resetTrigger = true
                    delay(100)
                    resetTrigger = false
                }
            },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF934A)),
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Text(
                "He's back... and he remembers everything!",
                fontSize = 28.sp,
                fontFamily = RoadRageFontFamily,
                color = Color.Black
            )
        }
        //   }


    }

}

fun Offset.overlap(offset: Offset): Boolean =
    abs(this.x - offset.x) < 10 && abs(this.y - offset.y) < 10


@Composable
fun animateHead(isAnimation: Boolean): Animatable<Float, AnimationVector1D> {
    val currentRotation = remember { Animatable(0f) }

    LaunchedEffect(isAnimation) {
        if (isAnimation) {
            while (true) {
                currentRotation.stop()
                currentRotation.animateTo(
                    targetValue = 15f,
                    animationSpec = tween(durationMillis = 250),
                )
                currentRotation.animateTo(
                    targetValue = -15f, animationSpec = tween(durationMillis = 250)
                )
            }
        } else {
            currentRotation.animateTo(
                targetValue = 0f, animationSpec = tween(durationMillis = 100)
            )
        }
    }

    return currentRotation
}

@Composable
fun Modifier.drag(item: Item, resetTrigger: Boolean): Modifier {
    var offset by remember { mutableStateOf(Offset.Zero) }

    var isMove by remember { mutableStateOf(false) }

    val currentRotation = remember { Animatable(0f) }

    val animateHaed = animateHead(isMove)


    if (resetTrigger) {
        offset = Offset.Zero
    }

    LaunchedEffect(isMove) {
        if (isMove) {
            currentRotation.stop()
            while (true) {
                currentRotation.animateTo(
                    targetValue = 15f,
                    animationSpec = tween(durationMillis = 250),
                )
                currentRotation.animateTo(
                    targetValue = -15f, animationSpec = tween(durationMillis = 250)
                )

            }
        } else {
            currentRotation.animateTo(
                targetValue = 0f, animationSpec = tween(durationMillis = 100)
            )
        }
    }



    return if (!item.isOverlap()) {
        this
            .offset { offset.toIntOffset() }
            .rotate(animateHaed.value)

            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    isMove = true
                }, onTap = { isMove = false }

                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset = offset + dragAmount
                    isMove = false
                }

            }
    } else {
        this.offset { offset.toIntOffset() }
    }
}

@Composable
fun Modifier.globalPosition(
    onMove: (Offset) -> Unit,
    onSizeChange: (IntSize) -> Unit = {}
): Modifier =
    this
        .onGloballyPositioned {
            onMove(it.positionInRoot())
        }
        .onSizeChanged(onSizeChange)


fun Offset.toIntOffset() = IntOffset(this.x.toInt(), this.y.toInt())

@Composable
fun ImageTop(item: Item, modifier: Modifier = Modifier) {
    Column {
        Image(
            painter = painterResource(item.img),
            contentDescription = "head",
            colorFilter = ColorFilter.tint(Color.White),
            alpha = if (item.isOverlap()) 1f else 0.5f,
            modifier = modifier
                // .scale(0.9f)
                .border(
                    if (item.isOverlap()) (-1).dp else 1.dp,
                    Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(1.dp)
                )
                .padding(padding)
        )
    }
}

@Composable
fun ImageBottom(item: Item, modifier: Modifier = Modifier) {
    var borderWidth = if (!item.isOverlap()) 1.dp else (-1).dp
    if (item.isWrongOverlap) {
        borderWidth = 2.dp
    }

    Image(
        painter = painterResource(item.img),
        contentDescription = "head",
        colorFilter = ColorFilter.tint(Color.White),
        alpha = if (!item.isOverlap()) 1f else 0f,
        modifier = modifier
            // .scale(0.9f)
            .border(
                borderWidth,
                if (item.isWrongOverlap) Color.Red else Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(1.dp)
            )
            .padding(padding)
    )
}
