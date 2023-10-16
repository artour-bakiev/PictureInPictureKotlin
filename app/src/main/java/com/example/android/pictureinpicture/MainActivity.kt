/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.pictureinpicture

import android.annotation.TargetApi
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.android.pictureinpicture.databinding.MainActivityBinding

/**
 * Demonstrates usage of Picture-in-Picture mode on phones and tablets.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: MainActivityBinding
    private lateinit var pipDelegate: MainPipDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Event handlers
        binding.clear.setOnClickListener { viewModel.clear() }
        binding.startOrPause.setOnClickListener { viewModel.startOrPause() }
        binding.switchExample.setOnClickListener {
            startActivity(Intent(this@MainActivity, MovieActivity::class.java))
            finish()
        }
        // Observe data from the viewModel.
        viewModel.time.observe(this) { time -> binding.time.text = time }
        pipDelegate = MainPipDelegate.create(this, binding, viewModel)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        pipDelegate.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    // This is called when the activity gets into or out of the picture-in-picture mode.
    @TargetApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        pipDelegate.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }
}
