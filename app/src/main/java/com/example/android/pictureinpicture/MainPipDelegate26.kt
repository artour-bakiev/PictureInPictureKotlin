package com.example.android.pictureinpicture

import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.trackPipAnimationHintView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.android.pictureinpicture.databinding.MainActivityBinding
import kotlinx.coroutines.launch

@TargetApi(Build.VERSION_CODES.O)
internal class MainPipDelegate26(
    private val activity: ComponentActivity,
    private val binding: MainActivityBinding,
    private val viewModel: MainViewModel,
) : MainPipDelegate {

    /**
     * A [BroadcastReceiver] for handling action items on the picture-in-picture mode.
     */
    private val broadcastReceiver = object : BroadcastReceiver() {

        // Called when an item is clicked.
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != ACTION_STOPWATCH_CONTROL) {
                return
            }
            when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                CONTROL_TYPE_START_OR_PAUSE -> viewModel.startOrPause()
                CONTROL_TYPE_CLEAR -> viewModel.clear()
            }
        }
    }

    init {
        binding.pip?.setOnClickListener {
            activity.enterPictureInPictureMode(updatePictureInPictureParams(viewModel.started.value == true))
        }
        viewModel.started.observe(activity) { started ->
            binding.startOrPause.setImageResource(
                if (started) R.drawable.ic_pause_24dp else R.drawable.ic_play_arrow_24dp
            )
            updatePictureInPictureParams(started)
        }

        // Use trackPipAnimationHint view to make a smooth enter/exit pip transition.
        // See https://android.devsite.corp.google.com/develop/ui/views/picture-in-picture#smoother-transition
        activity.lifecycleScope.launch {
            activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activity.trackPipAnimationHintView(binding.stopwatchBackground)
            }
        }

        // Handle events from the action icons on the picture-in-picture mode.
        activity.registerReceiver(broadcastReceiver, IntentFilter(ACTION_STOPWATCH_CONTROL))
    }

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean) = Unit

    override fun onPictureInPictureModeChanged(inPictureInPictureMode: Boolean, newConfig: Configuration) {
        updateViewerControls(inPictureInPictureMode)
    }

    /**
     * Updates the parameters of the picture-in-picture mode for this activity based on the current
     * [started] state of the stopwatch.
     */
    private fun updatePictureInPictureParams(started: Boolean): PictureInPictureParams {
        val builder = PictureInPictureParams.Builder()
            // Set action items for the picture-in-picture mode. These are the only custom controls
            // available during the picture-in-picture mode.
            .setActions(
                listOf(
                    // "Clear" action.
                    createRemoteAction(
                        R.drawable.ic_refresh_24dp,
                        R.string.clear,
                        REQUEST_CLEAR,
                        CONTROL_TYPE_CLEAR
                    ),
                    if (started) {
                        // "Pause" action when the stopwatch is already started.
                        createRemoteAction(
                            R.drawable.ic_pause_24dp,
                            R.string.pause,
                            REQUEST_START_OR_PAUSE,
                            CONTROL_TYPE_START_OR_PAUSE
                        )
                    } else {
                        // "Start" action when the stopwatch is not started.
                        createRemoteAction(
                            R.drawable.ic_play_arrow_24dp,
                            R.string.start,
                            REQUEST_START_OR_PAUSE,
                            CONTROL_TYPE_START_OR_PAUSE
                        )
                    }
                )
            )
            // Set the aspect ratio of the picture-in-picture mode.
            .setAspectRatio(Rational(16, 9))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Turn the screen into the picture-in-picture mode if it's hidden by the "Home" button.
            builder.setAutoEnterEnabled(true)
                // Disables the seamless resize. The seamless resize works great for videos where the
                // content can be arbitrarily scaled, but you can disable this for non-video content so
                // that the picture-in-picture mode is resized with a cross fade animation.
                .setSeamlessResizeEnabled(false)
        }
        val params = builder.build()
        activity.setPictureInPictureParams(params)
        return params
    }

    /**
     * Creates a [RemoteAction]. It is used as an action icon on the overlay of the
     * picture-in-picture mode.
     */
    private fun createRemoteAction(
        @DrawableRes iconResId: Int,
        @StringRes titleResId: Int,
        requestCode: Int,
        controlType: Int
    ): RemoteAction {
        return RemoteAction(
            Icon.createWithResource(activity, iconResId),
            activity.getString(titleResId),
            activity.getString(titleResId),
            PendingIntent.getBroadcast(
                activity,
                requestCode,
                Intent(ACTION_STOPWATCH_CONTROL)
                    .putExtra(EXTRA_CONTROL_TYPE, controlType),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

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

    companion object {
        /** Intent action for stopwatch controls from Picture-in-Picture mode.  */
        private const val ACTION_STOPWATCH_CONTROL = "stopwatch_control"

        /** Intent extra for stopwatch controls from Picture-in-Picture mode.  */
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val CONTROL_TYPE_CLEAR = 1
        private const val CONTROL_TYPE_START_OR_PAUSE = 2

        private const val REQUEST_CLEAR = 3
        private const val REQUEST_START_OR_PAUSE = 4
    }
}