package com.example.android.pictureinpicture

import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import com.example.android.pictureinpicture.databinding.MainActivityBinding

internal interface MainPipDelegate {
    // API 24
    fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean)

    // API 26
    fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration)

    companion object {
        fun create(
            activity: ComponentActivity,
            binding: MainActivityBinding,
            viewModel: MainViewModel,
        ): MainPipDelegate = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> MainPipDelegate26(activity, binding, viewModel)

            // It seems `enterPictureInPictureMode` doesn't not necessary work on API 24
            // The Pixel emulator runs `java.lang.IllegalStateException`
            // "enterPictureInPictureMode: Device doesn't support picture-in-picture mode."
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> MainPipDelegate24(activity, binding, viewModel)

            else -> MainPipDelegateDefault(activity, binding, viewModel)
        }
    }
}
