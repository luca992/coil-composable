package com.luca992.compose.image

import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.onCommit
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


@Composable
fun CoilImage(
    model: Any,
    modifier : Modifier = Modifier,
    customize: LoadRequestBuilder.() -> Unit = {}
) {
    WithConstraints {
        val image = state<ImageAsset?> { null }
        val context = ContextAmbient.current

        onCommit(model) {
            val width =
                if (constraints.maxWidth > IntPx.Zero && constraints.maxWidth < IntPx.Infinity) {
                    constraints.maxWidth.value
                } else {
                    1
                }

            val height =
                if (constraints.maxHeight > IntPx.Zero && constraints.maxHeight < IntPx.Infinity) {
                    constraints.maxHeight.value
                } else {
                    1
                }

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
                    image.value = result.toBitmap().asImageAsset()
                }

                override fun onError(error: Drawable?) {
                    image.value = error?.toBitmap()?.asImageAsset()
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
                image.value = null
                requestDisposable.dispose()
            }
        }

        image.value?.apply {
            Image(modifier = modifier, asset = this)
        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    //Todo: figure out how to make preview work
    CoilImage(R.drawable.ic_baseline_tag_faces_24)
}
