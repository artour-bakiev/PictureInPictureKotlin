package com.example.android.pictureinpicture

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import com.example.android.pictureinpicture.databinding.MovieActivityBinding

internal interface MoviePipDelegate {
    fun onRestart()

    fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean)

    fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration)

    fun minimize()

    companion object {
        fun create(activity: Activity, binding: MovieActivityBinding): MoviePipDelegate = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> MoviePipDelegate26(activity, binding)

            // It seems `enterPictureInPictureMode` doesn't not necessary work on API 24
            // The Pixel emulator runs `java.lang.IllegalStateException`
            // "enterPictureInPictureMode: Device doesn't support picture-in-picture mode."
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> MoviePipDelegate24(activity, binding)

            else -> MoviePipDelegateDefault()
        }

        internal class MoviePipDelegateDefault : MoviePipDelegate {
            override fun onRestart() = Unit
            override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean) = Unit
            override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration) = Unit
            override fun minimize() = Unit
        }
    }
}