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
fun Modifier.simpleDrag(onMove: (Boolean) -> Unit = {}): Modifier {
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
                        onMove(true)
                    } else if (event.type == PointerEventType.Release) {
                        isMove = false
                        onMove(false)
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
    onStateUpdate: (BoneItem) -> Unit,
    modifier: Modifier = Modifier,
    withMovement: Boolean = false,
) {
    var offset by remember { mutableStateOf(item.offset) }
    var size by remember { mutableStateOf(item.size) }
    ImagePart(
        item,
        modifier = modifier
            .then(if (withMovement) Modifier.simpleDrag({
                onStateUpdate(
                    item.copy(isMoving = it)
                )
            }) else Modifier)
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
                        item.copy(offset = offset, size = size)
                    )
                },
                onSize = {
                    size = it.toSize()
                    onStateUpdate(
                        item.copy(offset = offset, size = size)
                    )
                }
            )



    )
}

data class BoneItem(
    val img: Int,
    val offset: Offset = Offset(0f, 0f),
    val size: Size = Size(0f, 0f),
    val alpha: Float = 1f,
    val borderSize: Dp = 1.dp,
    val isMoving: Boolean = false
) {


    override fun toString(): String {
        return "BoneItem: $img $offset $size"
    }

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
        return copy(
            head = head.copy(alpha = 0.6f),
            armLeft = armLeft.copy(alpha = 0.6f),
            armRight = armRight.copy(alpha = 0.6f),
            ribCage = ribCage.copy(alpha = 0.6f),
            pelvis = pelvis.copy(alpha = 0.6f),
            legLeft = legLeft.copy(alpha = 0.6f),
            legRight = legRight.copy(alpha = 0.6f),
        )

    }

    fun withBottomStyle(): BonesState2 {
        return copy(
            head = head.copy(alpha = 0.9f),
            armLeft = armLeft.copy(alpha = 0.9f),
            armRight = armRight.copy(alpha = 0.9f),
            ribCage = ribCage.copy(alpha = 0.9f),
            pelvis = pelvis.copy(alpha = 0.9f),
            legLeft = legLeft.copy(alpha = 0.9f),
            legRight = legRight.copy(alpha = 0.9f),
        )
    }

    fun updateItem(updatedItem: BoneItem): BonesState2 {
        return when (updatedItem.img) {
            R.drawable.head -> copy(head = updatedItem)
            R.drawable.arm_left -> copy(armLeft = updatedItem)
            R.drawable.arm_right -> copy(armRight = updatedItem)
            R.drawable.rib_cage -> copy(ribCage = updatedItem)
            R.drawable.pelvis -> copy(pelvis = updatedItem)
            R.drawable.leg_left -> copy(legLeft = updatedItem)
            R.drawable.leg_right -> copy(legRight = updatedItem)
            else -> {
                assert(false); this
            }
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

        val onTopUpdate: (BoneItem) -> Unit = { updatedItem ->
            topBones = topBones.updateItem(updatedItem)
        }
        val onBottomUpdate: (BoneItem) -> Unit = { updatedItem ->
            bottomBones = bottomBones.updateItem(updatedItem)
        }
        val onMove: (BoneItem, Boolean) -> Unit = { updatedItem, value ->
            bottomBones = bottomBones.updateItem(updatedItem.copy(isMoving = value))
        }

        LaunchedEffect(key1 = topBones, key2 = bottomBones) {
            //val isOverlap = topBones.head.overlaps(bottomBones.head)

            val active = bottomBones.head

            var cache = topBones
            bottomBones.listBody.forEach { active ->
                if (active.isMoving) {
                    topBones.listBody.forEach { top ->
                        val isOverlap = active.overlaps(top)

                        val topUpdated = if (isOverlap) {
                            top.copy(borderSize = 3.dp)
                        } else {
                            top.copy(borderSize = 1.dp)
                        }

                        cache = cache.updateItem(topUpdated)

                    }
                }
            }

            topBones = cache

//
//            if (isOverlap) {
//                topBones = topBones.copy(
//                    head = topBones.head.copy(
//                        alpha = 0.8f,
//                        borderSize = 2.dp
//                    )
//                )
//            } else {
//                topBones = topBones.copy(
//                    head = topBones.head.copy(
//                        alpha = 0.6f,
//                        borderSize = 1.dp
//                    )
//                )
//            }
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
            "Bottom: " +
                    "${bottomBones.head.size} \n" +
                    "${bottomBones.head.offset} \n", color = Color.White
        )

        ImagePartWithDetection(topBones.head, onTopUpdate)

        Row {
            ImagePartWithDetection(
                topBones.armLeft, onTopUpdate
            )
            Column {
                ImagePartWithDetection(
                    topBones.ribCage,
                    onTopUpdate
                )
                ImagePartWithDetection(
                    topBones.pelvis,
                    onTopUpdate
                )
            }

            ImagePartWithDetection(
                topBones.armRight,
                onTopUpdate
            )

        }
        Row {
            ImagePartWithDetection(
                topBones.legLeft,
                onTopUpdate
            )
            ImagePartWithDetection(
                topBones.legRight,
                onTopUpdate
            )

        }
        Spacer(modifier = Modifier.height(25.dp))

        ///////////////////////////////////
        ////////////// BOTTOM BONES ///////
        ///////////////////////////////////

        Row {
            Column {
                Row {
                    ImagePartWithDetection(
                        item = bottomBones.legLeft,
                        onStateUpdate = onBottomUpdate,
                       withMovement = true
                    )

                    Spacer(modifier = Modifier.size(padding))
                    ImagePartWithDetection(
                        item = bottomBones.legRight,
                        onStateUpdate = onBottomUpdate,
                        withMovement = true
                    )
                }
                Spacer(modifier = Modifier.size(padding))



                ImagePartWithDetection(
                    item = bottomBones.head,
                    onStateUpdate = onBottomUpdate,
                    withMovement = true
                )


            }
            Spacer(modifier = Modifier.size(padding))
            Column {
                ImagePartWithDetection(
                    item = bottomBones.ribCage,
                    onStateUpdate = onBottomUpdate,
                    withMovement = true
                )
                Spacer(modifier = Modifier.size(padding))
                ImagePartWithDetection(
                    item = bottomBones.armLeft,
                    onStateUpdate = onBottomUpdate,
                    withMovement = true
                )
            }

            Spacer(modifier = Modifier.size(padding))
            Column {
                ImagePartWithDetection(
                    item = bottomBones.legRight,
                    onStateUpdate = onBottomUpdate,
                    withMovement = true
                )
                Spacer(modifier = Modifier.size(padding))
                ImagePartWithDetection(
                    item = bottomBones.pelvis,
                    onStateUpdate = onBottomUpdate,
                    withMovement = true
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

