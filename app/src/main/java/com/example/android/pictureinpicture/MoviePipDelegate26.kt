package com.example.android.pictureinpicture

import android.annotation.TargetApi
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.util.Rational
import androidx.core.view.doOnLayout
import com.example.android.pictureinpicture.databinding.MovieActivityBinding

@TargetApi(Build.VERSION_CODES.O)
internal class MoviePipDelegate26(private val activity: Activity, private val binding: MovieActivityBinding) :
    MoviePipDelegate {

    init {
        // Configure parameters for the picture-in-picture mode. We do this at the first layout of
        // the MovieView because we use its layout position and size.
        binding.movie.doOnLayout { updatePictureInPictureParams() }
    }

    override fun onRestart() {
        if (!activity.isInPictureInPictureMode) {
            // Show the video controls so the video can be easily resumed.
            binding.movie.showControls()
        }
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean) = Unit

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration) {
        updateViewerControls(inPictureInPictureMode)
    }

    override fun minimize() {
        activity.enterPictureInPictureMode(updatePictureInPictureParams())
    }

    private fun updatePictureInPictureParams(): PictureInPictureParams {
        // Calculate the aspect ratio of the PiP screen.
        val aspectRatio = Rational(binding.movie.width, binding.movie.height)
        // The movie view turns into the picture-in-picture mode.
        val visibleRect = Rect()
        binding.movie.getGlobalVisibleRect(visibleRect)
        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            // Specify the portion of the screen that turns into the picture-in-picture mode.
            // This makes the transition animation smoother.
            .setSourceRectHint(visibleRect)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // The screen automatically turns into the picture-in-picture mode when it is hidden
            // by the "Home" button.
            builder.setAutoEnterEnabled(true)
        }
        val params = builder.build()
        activity.setPictureInPictureParams(params)
        return params
    }

    private fun updateViewerControls(isInPictureInPictureMode: Boolean) {
        if (isInPictureInPictureMode) {
            // Hide the controls in picture-in-picture mode.
            binding.movie.hideControls()
        } else {
            // Show the video controls if the video is not playing
            if (!binding.movie.isPlaying) {
                binding.movie.showControls()
            }
        }
    }
}
