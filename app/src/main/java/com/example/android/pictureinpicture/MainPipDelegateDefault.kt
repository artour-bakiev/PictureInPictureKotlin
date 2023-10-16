package com.example.android.pictureinpicture

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import com.example.android.pictureinpicture.databinding.MainActivityBinding

internal class MainPipDelegateDefault(
    activity: ComponentActivity,
    binding: MainActivityBinding,
    viewModel: MainViewModel,
) : MainPipDelegate {
    init {
        viewModel.started.observe(activity) { started ->
            binding.startOrPause.setImageResource(
                if (started) R.drawable.ic_pause_24dp else R.drawable.ic_play_arrow_24dp
            )
        }
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean) = Unit

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration) = Unit
}
