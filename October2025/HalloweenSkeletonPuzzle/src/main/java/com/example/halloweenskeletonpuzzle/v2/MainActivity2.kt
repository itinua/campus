@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.example.halloweenskeletonpuzzle.v2

import android.R.attr.animation
import android.R.attr.top
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.halloweenskeletonpuzzle.R
import com.example.halloweenskeletonpuzzle.RoadRageFontFamily
import com.example.halloweenskeletonpuzzle.toIntOffset
import com.example.halloweenskeletonpuzzle.ui.theme.CampusTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs


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
fun Modifier.simpleDrag(
    animateGoHome: Boolean,
    onClickStart: () -> Unit = {},
    onClickFinish: () -> Unit = {}

): Modifier {
    var offset by remember { mutableStateOf(Offset.Zero) }

    val animatedOffset by animateOffsetAsState(
        targetValue = offset,
        label = "itemPositionAnimation"
    )

    if (animateGoHome) {
        offset = Offset.Zero
    }


    return this
        .offset { animatedOffset.toIntOffset() }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.type == PointerEventType.Press) {
                        onClickStart()
                        println("sad-Press")
                    } else if (event.type == PointerEventType.Release) {
                        onClickFinish()
                        println("sad-Release")
                    }
                }
            }
        }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                offset = offset + dragAmount
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
private fun ImageBoneItem(
    item: BoneItem,
    onStateUpdate: (BoneItem, onFinish: Boolean) -> Unit,
    onClickFinish: (BoneItem) -> Unit,
    withMovement: Boolean = false,
) {
    var offset by remember { mutableStateOf(item.offset) }
    var size by remember { mutableStateOf(item.size) }


    var localAnimation by remember { mutableStateOf(Animation.INIT) }

    val animateHead = animationHeadOnce(localAnimation == Animation.ANIMATE_HEAD)
    val animateHeadLong = animationLong(localAnimation == Animation.ANIMATE_HEAD_LONG)

    var borderColor by remember { mutableStateOf(Color.White) }
    var borderSize by remember { mutableStateOf(1.dp) }
    var alpha by remember { mutableFloatStateOf(0.6f) }

    if (item.isHighlight) {
        borderSize = 2.dp
    } else {
        borderSize = 1.dp
    }

    if (item.isHighlight) {
        alpha = 0.9f
    } else {
        if (item.type == BoneType.BOTTOM) {
            alpha = 0.8f;
        }
        if (item.type == BoneType.TOP) {
            alpha = 0.6f;
        }
    }
    if (localAnimation == Animation.INIT) {
        borderColor = Color.White
    }

    if (item.isFind && item.type == BoneType.TOP) {
        borderSize = (-1).dp
        alpha = 1f
    }

    if (item.isFind && item.type == BoneType.BOTTOM) {
        borderSize = (-1).dp
        alpha = 0f
    }

    LaunchedEffect(item.animation, item.isFind) {
        if (item.animation == Animation.RED_HOME
            && !item.isFind
        ) {
            borderColor = Color.Red
            localAnimation = Animation.NONE
            delay(100)
            localAnimation = Animation.ANIMATE_HEAD
            delay(1500)
            localAnimation = Animation.MOVE_HOME
            borderColor = Color.Red
            delay(100)
            localAnimation = Animation.INIT
        }
        if (item.animation == Animation.ANIMATE_HEAD) {
            localAnimation = Animation.ANIMATE_HEAD
            delay(1500)
        }
        if (item.animation == Animation.WHITE_HOME) {
            borderColor = Color.White
            borderSize = 1.dp
            alpha = 0.8f
            localAnimation = Animation.MOVE_HOME
            delay(1500)
        }
        if (item.animation == Animation.ANIMATE_HEAD_LONG) {
            localAnimation= Animation.ANIMATE_HEAD_LONG

        }
        if (item.animation == Animation.NONE) {
            localAnimation= Animation.NONE

        }
    }


    Image(
        painter = painterResource(item.img),
        contentDescription = "",
        colorFilter = ColorFilter.tint(Color.White),
        modifier = Modifier
            .then(
                if (withMovement)
                    Modifier.simpleDrag(
                        localAnimation == Animation.MOVE_HOME,
                        onClickStart = {
                            println("onClickStart")
                            onStateUpdate(
                                item.copy(animation = Animation.ANIMATE_HEAD, isMoving = true),
                                false
                            )
                        }, onClickFinish = {
                            onClickFinish(item)
                        }) else Modifier
            )
            .rotate(animateHead.value)
            .rotate(animateHeadLong.value)
            .alpha(alpha)
            .border(
                borderSize,
                borderColor,
                RoundedCornerShape(1.dp)
            )
            .padding(10.dp)
            .detectPositionAndSize(
                onPosition = {
                    offset = it
                    onStateUpdate(
                        item.copy(offset = offset, size = size), false
                    )
                },
                onSize = {
                    size = it.toSize()
                    onStateUpdate(
                        item.copy(offset = offset, size = size), false
                    )
                }
            )


    )
}

enum class Animation {
    NONE, INIT, ANIMATE_HEAD, MOVE_HOME, RED_HOME, WHITE_HOME,ANIMATE_HEAD_LONG
}

enum class BoneType {
    TOP, BOTTOM
}

data class BoneItem(
    val img: Int,
    val offset: Offset = Offset(0f, 0f),
    val size: Size = Size(0f, 0f),
    val type: BoneType = BoneType.TOP,
    val isMoving: Boolean = false,
    val isHighlight: Boolean = false,
    val isFind: Boolean = false,
    val animation: Animation = Animation.INIT,
) {

    fun isInPlace(second: BoneItem): Boolean {
        println("${this.offset} + ${second.offset}")
        return this.img == second.img &&
        abs(this.offset.x - second.offset.x) < 50 &&
                abs(this.offset.y - second.offset.y) < 50
    }


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
            head = head.copy(type = BoneType.TOP),
            armLeft = armLeft.copy(type = BoneType.TOP),
            armRight = armRight.copy(type = BoneType.TOP),
            ribCage = ribCage.copy(type = BoneType.TOP),
            pelvis = pelvis.copy(type = BoneType.TOP),
            legLeft = legLeft.copy(type = BoneType.TOP),
            legRight = legRight.copy(type = BoneType.TOP),
        )

    }

    fun withBottomStyle(): BonesState2 {
        return copy(
            head = head.copy(type = BoneType.BOTTOM),
            armLeft = armLeft.copy(type = BoneType.BOTTOM),
            armRight = armRight.copy(type = BoneType.BOTTOM),
            ribCage = ribCage.copy(type = BoneType.BOTTOM),
            pelvis = pelvis.copy(type = BoneType.BOTTOM),
            legLeft = legLeft.copy(type = BoneType.BOTTOM),
            legRight = legRight.copy(type = BoneType.BOTTOM),
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

        var isFinish by remember { mutableStateOf(false) }
        var currentItem by remember { mutableStateOf(topBones.head) }

        val onTopUpdate: (BoneItem, Boolean) -> Unit = { updatedItem, it ->
            topBones = topBones.updateItem(updatedItem)
        }
        val onBottomUpdate: (BoneItem, Boolean) -> Unit = { updatedItem, it ->
            bottomBones = bottomBones.updateItem(updatedItem)
            currentItem = updatedItem
        }

        var findAll by remember { mutableStateOf(false) }

        val onClickFinish: (BoneItem) -> Unit = {

            val topItem = topBones.listBody.find {
                it.isInPlace(currentItem)
            }


            println("onClickFinish ")

            if (topItem == null) {
                bottomBones = bottomBones.updateItem(
                    currentItem.copy(
                        animation = Animation.RED_HOME
                    )
                )
            } else {
                bottomBones = bottomBones.updateItem(
                    currentItem.copy(
                        isFind = true
                    )
                )
                topBones = topBones.updateItem(
                    topItem.copy(
                        isFind = true
                    )
                )
            }

            findAll = topBones.listBody.all { it.isFind }
            if (findAll) {

                topBones = topBones.updateItem(
                    topBones.head.copy(
                        animation = Animation.ANIMATE_HEAD_LONG
                    )
                )

                topBones = topBones.updateItem(
                    topBones.armRight.copy(
                        animation = Animation.ANIMATE_HEAD_LONG
                    )
                )

                topBones = topBones.updateItem(
                    topBones.armLeft.copy(
                        animation = Animation.ANIMATE_HEAD_LONG
                    )
                )
            }


        }



        LaunchedEffect(key1 = topBones, key2 = bottomBones) {
            var cacheTop = topBones

            topBones.listBody.forEach { top ->
                val isOverlap = currentItem.overlaps(top)

                val topUpdated = if (isOverlap) {
                    top.copy(isHighlight = true)
                } else {
                    top.copy(isHighlight = false)
                }
                cacheTop = cacheTop.updateItem(topUpdated)


            }
            topBones = cacheTop
        }

        Spacer(modifier = Modifier.height(60.dp))

        if (false) {
            Text(
                "Top: ${topBones.head.isInPlace(bottomBones.head)} " +
                        "${topBones.head.size} \n" +
                        "${topBones.head.offset} \n",
                color = Color.White
            )
            Text(
                "Bottom: " +
                        "${bottomBones.head.size} \n" +
                        "${bottomBones.head.offset} \n", color = Color.White
            )
        }

        ImageBoneItem(topBones.head, onTopUpdate, onClickFinish)

        Row {
            ImageBoneItem(
                topBones.armLeft, onTopUpdate, onClickFinish
            )
            Column {
                ImageBoneItem(
                    topBones.ribCage,
                    onTopUpdate, onClickFinish
                )
                ImageBoneItem(
                    topBones.pelvis,
                    onTopUpdate, onClickFinish
                )
            }

            ImageBoneItem(
                topBones.armRight,
                onTopUpdate, onClickFinish
            )

        }
        Row {
            ImageBoneItem(
                topBones.legLeft,
                onTopUpdate, onClickFinish
            )
            ImageBoneItem(
                topBones.legRight,
                onTopUpdate, onClickFinish
            )

        }
        Spacer(modifier = Modifier.height(25.dp))

        ///////////////////////////////////
        ////////////// BOTTOM BONES ///////
        ///////////////////////////////////

        Row {
            Column {
                Row {
                    ImageBoneItem(
                        item = bottomBones.legLeft,
                        onStateUpdate = onBottomUpdate, onClickFinish,
                        withMovement = true
                    )

                    Spacer(modifier = Modifier.size(padding))
                    ImageBoneItem(
                        item = bottomBones.armRight,
                        onStateUpdate = onBottomUpdate, onClickFinish,
                        withMovement = true
                    )
                }
                Spacer(modifier = Modifier.size(padding))



                ImageBoneItem(
                    item = bottomBones.head,
                    onStateUpdate = onBottomUpdate, onClickFinish,
                    withMovement = true
                )


            }
            Spacer(modifier = Modifier.size(padding))
            Column {
                ImageBoneItem(
                    item = bottomBones.ribCage,
                    onStateUpdate = onBottomUpdate, onClickFinish,
                    withMovement = true
                )
                Spacer(modifier = Modifier.size(padding))
                ImageBoneItem(
                    item = bottomBones.armLeft,
                    onStateUpdate = onBottomUpdate, onClickFinish,
                    withMovement = true
                )
            }

            Spacer(modifier = Modifier.size(padding))
            Column {
                ImageBoneItem(
                    item = bottomBones.legRight,
                    onStateUpdate = onBottomUpdate, onClickFinish,
                    withMovement = true
                )
                Spacer(modifier = Modifier.size(padding))
                ImageBoneItem(
                    item = bottomBones.pelvis,
                    onStateUpdate = onBottomUpdate, onClickFinish,
                    withMovement = true
                )
            }


        }

        Spacer(modifier = Modifier.size(42.dp))
        if (findAll) {
//        Button(onClick = {
//
//            topBones = topBones.updateItem(
//                topBones.head.copy(
//                    animation = Animation.ANIMATE_HEAD_LONG
//                )
//            )
//
//            topBones = topBones.updateItem(
//                topBones.armRight.copy(
//                    animation = Animation.ANIMATE_HEAD_LONG
//                )
//            )
//
//            topBones = topBones.updateItem(
//                topBones.armLeft.copy(
//                    animation = Animation.ANIMATE_HEAD_LONG
//                )
//            )
//        }) {
//         Text("Head", color = Color.White)
//        }
            Button(
                onClick = {
                    findAll = false
                    topBones.listBody.forEach {
                        topBones = topBones.updateItem(
                            it.copy(
                                isFind = false,
                                isHighlight = false,
                                animation = Animation.NONE
                            )
                        )
                    }

                    bottomBones.listBody.forEach {
                        bottomBones = bottomBones.updateItem(
                            it.copy(
                                isFind = false,
                                isHighlight = false,
                                animation = Animation.WHITE_HOME
                            )
                        )
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
        }

    }

}


@Composable
fun animationHeadOnce(animate: Boolean): Animatable<Float, AnimationVector1D> {
    val currentRotation = remember { Animatable(0f) }

    LaunchedEffect(animate) {
        if (animate) {
            currentRotation.stop()
            listOf(15f, -15f, 15f, 0f).forEach { target ->
                currentRotation.animateTo(target, tween(250))
            }
        } else {
            currentRotation.animateTo(0f, tween(250))
        }
    }
    return currentRotation
}

@Composable
fun animationLong(animate: Boolean): Animatable<Float, AnimationVector1D> {
    val currentRotation = remember { Animatable(0f) }

    LaunchedEffect(animate) {
        while (animate) {
            currentRotation.stop()
            listOf(15f, -15f).forEach { target ->
                currentRotation.animateTo(target, tween(250))
            }
        }

        currentRotation.animateTo(0f, tween(250))

    }
    return currentRotation
}



