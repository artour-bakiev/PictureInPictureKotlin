package com.example.android.pictureinpicture

import android.annotation.TargetApi
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import com.example.android.pictureinpicture.databinding.MovieActivityBinding

@TargetApi(Build.VERSION_CODES.N)
internal class MoviePipDelegate24(private val activity: Activity, private val binding: MovieActivityBinding) :
    MoviePipDelegate {

    override fun onRestart() {
        if (!activity.isInPictureInPictureMode) {
            // Show the video controls so the video can be easily resumed.
            binding.movie.showControls()
        }
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean) {
        updateViewerControls(inPictureInPictureMode)
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration) = Unit

    override fun minimize() {
        activity.enterPictureInPictureMode()
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