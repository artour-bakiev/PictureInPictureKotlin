package com.example.android.pictureinpicture

import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import com.example.android.pictureinpicture.databinding.MainActivityBinding

@TargetApi(Build.VERSION_CODES.N)
internal class MainPipDelegate24(
    activity: ComponentActivity,
    private val binding: MainActivityBinding,
    viewModel: MainViewModel
) : MainPipDelegate {

    init {
        binding.pip?.setOnClickListener {
            activity.enterPictureInPictureMode()
        }
        viewModel.started.observe(activity) { started ->
            binding.startOrPause.setImageResource(
                if (started) R.drawable.ic_pause_24dp else R.drawable.ic_play_arrow_24dp
            )
        }
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean) {
        updateViewerControls(inPictureInPictureMode)
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration) = Unit

    private fun updateViewerControls(isInPictureInPictureMode: Boolean) {
        if (isInPictureInPictureMode) {
            // Hide in-app buttons. They cannot be interacted in the picture-in-picture mode, and
            // their features are provided as the action icons.
            binding.clear.visibility = View.GONE
            binding.startOrPause.visibility = View.GONE
        } else {
            binding.clear.visibility = View.VISIBLE
            binding.startOrPause.visibility = View.VISIBLE
        }
    }
}
