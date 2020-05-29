package com.luca992.compose.image

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.IntPx
import coil.Coil
import coil.request.LoadRequest
import coil.request.LoadRequestBuilder
import coil.size.Scale
import coil.target.Target
import kotlinx.coroutines.*


@Composable
fun CoilImage(
    model: Any,
    modifier : Modifier = Modifier,
    customize: LoadRequestBuilder.() -> Unit = {}
) {
    WithConstraints(modifier) {
        var width =
            if (constraints.maxWidth > IntPx.Zero && constraints.maxWidth < IntPx.Infinity) {
                constraints.maxWidth.value
            } else {
                -1
            }

        var height =
            if (constraints.maxHeight > IntPx.Zero && constraints.maxHeight < IntPx.Infinity) {
                constraints.maxHeight.value
            } else {
                -1
            }

        //if height xor width not able to be determined, make image a square of the determined dimension
        if (width == -1) width = height
        if (height == -1) height = width

        val image = state<ImageAsset> { ImageAsset(width,height) }
        val context = ContextAmbient.current
        var animationJob : Job? = remember { null }
        onCommit(model) {


            val target = object : Target {
                override fun onStart(placeholder: Drawable?) {
                    placeholder?.apply {
                        if (height == 1) {
                            val scaledHeight = intrinsicHeight* (width / intrinsicWidth )
                            image.value = toBitmap(width, scaledHeight).asImageAsset()
                        }
                        if (width == 1) {
                            val scaledWidth = intrinsicWidth * (height / intrinsicWidth )
                            image.value = toBitmap(scaledWidth, height).asImageAsset()
                        }
                    }
                }

                override fun onSuccess(result: Drawable) {
                    if (result is Animatable) {
                        (result as Animatable).start()

                        animationJob = GlobalScope.launch(Dispatchers.Default) {
                            while (true) {
                                val asset = result.toBitmap().asImageAsset()
                                withContext(Dispatchers.Main) {
                                    image.value = asset
                                }
                                delay(16)
                                //1000 ms / 60 fps = 16.666 ms/fps
                                //TODO: figure out most efficient way to dispaly a gif
                            }
                        }
                    } else {
                        image.value = result.toBitmap().asImageAsset()
                    }
                }

                override fun onError(error: Drawable?) {
                    error?.run {
                        image.value = toBitmap().asImageAsset()
                    }
                }
            }



            val request = LoadRequest.Builder(context)
                .data(model)
                .size(width, height)
                .scale(Scale.FILL)
                .apply{customize(this)}
                .target(target)

            val requestDisposable = Coil.imageLoader(context).execute(request.build())

            onDispose {
                image.value = ImageAsset(width,height)
                requestDisposable.dispose()
                animationJob?.cancel()
            }
        }
        Image(modifier = modifier, asset = image.value)
    }
}

@Preview
@Composable
fun ProfilePreview() {
    //Todo: figure out how to make preview work
    CoilImage(R.drawable.ic_baseline_tag_faces_24)
}
