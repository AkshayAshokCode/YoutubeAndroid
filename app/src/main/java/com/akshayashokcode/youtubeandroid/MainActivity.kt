package com.akshayashokcode.youtubeandroid

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.akshayashokcode.youtubeandroid.databinding.ActivityMainBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubePlayerView

class MainActivity : YouTubeBaseActivity() {
    private val TAG = "YoutubePlayerActivity"
    private var videoID: String? = null
    private lateinit var mPlayer: YouTubePlayer
    private var mHandler: Handler? = null
    private var seekTime = 0
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get the video id
        //videoID = intent.getStringExtra("video_id")
        videoID ="7fcb5HiaFYE"
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding.playButton.setOnClickListener(View.OnClickListener { view: View? -> playButtonManager() })
        binding.btFullscreen.setOnClickListener(View.OnClickListener { view: View? ->
            //To handle orientation changes
            val orientation = this.resources.configuration.orientation
            if (null != mPlayer) {
                if (mPlayer.isPlaying) {
                    mPlayer.pause()
                }
                requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    //  btFullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit));
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    //  btFullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen));
                }
                mPlayer.play()
            }
        })
        binding.videoSeekbar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener)
        mHandler = Handler()
        initializeYoutubePlayer()

    }

   /* override fun onResume() {
        super.onResume()
        if (mPlayer != null) {
            initializeYoutubePlayer()
        }
    }
*/
    private fun initializeYoutubePlayer() {
        binding.youtubePlayerView.initialize(BuildConfig.YOUTUBE_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer,
                    wasRestored: Boolean,
                ) {
                    if (null == youTubePlayer) return
                    mPlayer = youTubePlayer
                    displayCurrentTime()
                    //if initialization success then load the video id to youtube player
                    if (!wasRestored) {
                        if (videoID != null) {
                            youTubePlayer.loadVideo(videoID, seekTime)
                        }
                    }
                    //set the player style here: like CHROMELESS, MINIMAL, DEFAULT
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)

                    // Add listeners to YouTubePlayer instance
                    youTubePlayer.setPlayerStateChangeListener(mPlayerStateChangeListener)
                    youTubePlayer.setPlaybackEventListener(playbackEventListener)
                }

                private val playbackEventListener: PlaybackEventListener =
                    object : PlaybackEventListener {
                        override fun onPlaying() {
                            mHandler!!.postDelayed(runnable, 100)
                            displayCurrentTime()
                        }

                        override fun onPaused() {
                            mHandler!!.removeCallbacks(runnable)
                            if (mPlayer != null) {
                                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,
                                    R.drawable.play_arrow_24))
                            }
                        }

                        override fun onStopped() {
                            mHandler!!.removeCallbacks(runnable)
                        }

                        override fun onBuffering(b: Boolean) {}
                        override fun onSeekTo(i: Int) {
                            mHandler!!.postDelayed(runnable, 100)
                        }
                    }

                override fun onInitializationFailure(
                    arg0: YouTubePlayer.Provider,
                    arg1: YouTubeInitializationResult,
                ) {
                    //print or show error if initialization failed
                    Log.e(TAG,
                        "Youtube Player View initialization failed:$arg1")
                    if (arg1.toString() == "SERVICE_VERSION_UPDATE_REQUIRED") {
                        Toast.makeText(this@MainActivity,
                            "Please Update your Youtube to latest version.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    var mPlayerStateChangeListener: PlayerStateChangeListener = object : PlayerStateChangeListener {
        override fun onLoading() {}
        override fun onLoaded(s: String) {}
        override fun onAdStarted() {}
        override fun onVideoStarted() {
            displayCurrentTime()
        }

        override fun onVideoEnded() {
            // To seek youtube video player in the starting position and avoid adds (but it can fail to avoid adds)
            if (null == mPlayer) return
            mPlayer.seekToMillis(0)
            mPlayer.pause()
        }

        override fun onError(errorReason: YouTubePlayer.ErrorReason) {}
    }
    var mVideoSeekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (null == mPlayer) return
            val lengthPlayed = (mPlayer.getDurationMillis() * seekBar.progress / 100).toLong()
            mPlayer.seekToMillis(lengthPlayed.toInt())
        }
    }

    private fun displayCurrentTime() {
        //To stop video 1.2s before it ends & to seek youtube video player in the starting position and avoid adds
        if (null == mPlayer) return
        if (mPlayer.getCurrentTimeMillis() >= mPlayer.getDurationMillis() - 1200L) {
            mPlayer.seekToMillis(0)
            mPlayer.pause()
        }
        val formattedTime = formatTime(mPlayer.getDurationMillis() - mPlayer.getCurrentTimeMillis())
        binding.playTime.text = formattedTime
        val playPercent = (mPlayer.getCurrentTimeMillis().toFloat() / mPlayer.getDurationMillis()
            .toFloat() * 100).toInt()

        // update live progress
        binding.videoSeekbar.progress = playPercent
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_baseline_pause_24))
            } else {
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.play_arrow_24))
            }
        }
    }

    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return (if (hours == 0) "" else "$hours:") + String.format("%02d:%02d",
            minutes % 60,
            seconds % 60)
    }


    private val runnable: Runnable = object : Runnable {
        override fun run() {
            displayCurrentTime()
            mHandler!!.postDelayed(this, 100)
        }
    }

    // To manage play button drawable to be in sync with video
    private fun playButtonManager() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause()
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.play_arrow_24))
            } else {
                mPlayer.play()
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_baseline_pause_24))
            }
        }
    }

    override fun onDestroy() {
        if (mPlayer != null) {
            mPlayer.release()
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (mPlayer != null) {
            seekTime = mPlayer.getCurrentTimeMillis()
        }
    }

    override fun onStop() {
        if (mPlayer != null) {
            mPlayer.release()
        }
        super.onStop()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.btFullscreen.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_fullscreen))
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.btFullscreen.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_fullscreen_exit))
        }
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_baseline_pause_24))
            } else {
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.play_arrow_24))
            }
        }
    }

}