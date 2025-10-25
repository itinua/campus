@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.example.halloweenskeletonpuzzle.v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.halloweenskeletonpuzzle.R
import com.example.halloweenskeletonpuzzle.RoadRageFontFamily
import com.example.halloweenskeletonpuzzle.animateHead
import com.example.halloweenskeletonpuzzle.toIntOffset
import com.example.halloweenskeletonpuzzle.ui.theme.CampusTheme


val padding = 10.dp


fun Offset.toIntOffset() = IntOffset(this.x.toInt(), this.y.toInt())


class MainActivity2 : ComponentActivity() {
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

                        Skeleton2()
                    }

                }
            }
        }
    }
}

@Composable
fun Modifier.simpleDrag(): Modifier {
    var offset by remember { mutableStateOf(Offset.Zero) }

    var isMove by remember { mutableStateOf(false) }

    val animateHaed = animateHead(isMove)

    val animatedOffset by animateOffsetAsState(
        targetValue = offset,
        label = "itemPositionAnimation"
    )


    return this
        .offset { animatedOffset.toIntOffset() }
        .rotate(animateHaed.value)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.type == PointerEventType.Press) {
                        isMove = true
                    } else if (event.type == PointerEventType.Release) {
                        isMove = false
                        offset = Offset.Zero
                    }
                }
            }
        }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                offset = offset + dragAmount
                isMove = false
            }

        }

}

@Composable
fun Modifier.detectPositionAndSize(
    onPosition: (Offset) -> Unit,
    onSize: (IntSize) -> Unit = {}
): Modifier =
    this
        .onGloballyPositioned {
            onPosition(it.positionInRoot())
        }
        .onSizeChanged(onSize)


@Composable
private fun ImagePartWithDetection(
    item: BoneItem,
    state: BonesState2,
    onStateUpdate: (BonesState2) -> Unit,
    modifier: Modifier = Modifier
) {
    var offset by remember { mutableStateOf(item.offset) }
    var size by remember { mutableStateOf(item.size) }
    ImagePart(
        item,
        modifier = modifier
            .alpha(item.alpha)
            .border(
                item.borderSize,
                Color.White,
                RoundedCornerShape(1.dp)
            )
            .padding(10.dp)
            .detectPositionAndSize(
                onPosition = {
                    offset = it
                    onStateUpdate(
                        state.updateBone(
                            item,
                            item.copy(offset = offset, size = size)
                        )
                    )
                },
                onSize = {
                    size = it.toSize()
                    onStateUpdate(
                        state.updateBone(
                            item,
                            item.copy(offset = offset, size = size)
                        )
                    )
                }
            )
    )
}

data class BoneItem(
    val img: Int,
    val offset: Offset = Offset(0f, 0f),
    var size: Size = Size(0f, 0f),
    var alpha: Float = 1f,
    var borderSize: Dp = 1.dp
) {
    fun overlaps(second: BoneItem): Boolean =
        Rect(this.offset, this.size).overlaps(
            Rect(
                second.offset, second.size
            )
        )
}

data class BonesState2(
    val head: BoneItem = BoneItem(R.drawable.head),
    val armLeft: BoneItem = BoneItem(R.drawable.arm_left),
    val armRight: BoneItem = BoneItem(R.drawable.arm_right),
    val ribCage: BoneItem = BoneItem(R.drawable.rib_cage),
    val pelvis: BoneItem = BoneItem(R.drawable.pelvis),
    val legLeft: BoneItem = BoneItem(R.drawable.leg_left),
    val legRight: BoneItem = BoneItem(R.drawable.leg_right),
) {


    val listBody = listOf(head, armLeft, armRight, ribCage, pelvis, legLeft, legRight)

    fun withTopStyle(): BonesState2 {
        listBody.forEach {
            it.alpha = 0.6f
        }
        return this
    }

    fun withBottomStyle(): BonesState2 {
        listBody.forEach {
            it.alpha = 0.9f

        }
        return this
    }

    fun updateBone(originalItem: BoneItem, updatedItem: BoneItem): BonesState2 {
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

@Composable
fun Skeleton2() {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var topBones by remember { mutableStateOf(BonesState2().withTopStyle()) }
        var bottomBones by remember { mutableStateOf(BonesState2().withBottomStyle()) }

        LaunchedEffect(key1 = topBones, key2 = bottomBones) {
            val isOverlap = topBones.head.overlaps(bottomBones.head)
            if (isOverlap) {
                topBones = topBones.copy(
                    head = topBones.head.copy(
                        alpha = 0.8f,
                        borderSize = 2.dp
                    )
                )
            } else {
                topBones = topBones.copy(
                    head = topBones.head.copy(
                        alpha = 0.6f,
                        borderSize = 1.dp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text("${topBones.head.borderSize}", color = Color.White)
        Text(
            "Top: " +
                    "${topBones.head.size} \n" +
                    "${topBones.head.offset} \n",
            color = Color.White
        )
        Text(
            "Top: " +
                    "${bottomBones.head.size} \n" +
                    "${bottomBones.head.offset} \n", color = Color.White
        )

        //1
        ImagePartWithDetection(
            topBones.head,
            topBones,
            onStateUpdate = {
                topBones = it
            }, modifier = Modifier
        )

        Row {
            ImagePart(
                topBones.armLeft, modifier = Modifier
            )

            Column {
                ImagePart(
                    topBones.ribCage, modifier = Modifier
                )
                ImagePart(
                    topBones.pelvis, modifier = Modifier
                )
            }

            ImagePart(
                topBones.armRight, modifier = Modifier
            )

        }
        Row {
            ImagePart(
                topBones.legLeft, modifier = Modifier
            )
            ImagePart(
                topBones.legRight, modifier = Modifier
            )
        }
        Spacer(modifier = Modifier.height(25.dp))


        Row {
            Column {
                Row {
                    ImagePart(
                        bottomBones.legLeft,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.size(padding))
                    ImagePart(
                        bottomBones.armRight,
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.size(padding))

                //2
                ImagePartWithDetection(
                    item = bottomBones.head,
                    bottomBones,
                    onStateUpdate = { bottomBones = it },
                    modifier = Modifier
                        .simpleDrag()

                )


            }
            Spacer(modifier = Modifier.size(padding))
            Column {
                ImagePart(
                    bottomBones.ribCage,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.size(padding))
                ImagePart(
                    bottomBones.armLeft,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.size(padding))
            Column {
                ImagePart(
                    bottomBones.legRight,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.size(padding))
                ImagePart(
                    bottomBones.pelvis,
                    modifier = Modifier
                )
            }


        }


        Spacer(modifier = Modifier.size(42.dp))

        val scope = rememberCoroutineScope()

        Button(
            onClick = {

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

    }

}

@Composable
fun ImagePart(item: BoneItem, modifier: Modifier = Modifier) {
    Column {
        Image(
            painter = painterResource(item.img),
            contentDescription = "head",
            colorFilter = ColorFilter.tint(Color.White),
            //alpha = if (item.isOverlap()) 1f else 0.5f,
            modifier = modifier
        )
    }
}

