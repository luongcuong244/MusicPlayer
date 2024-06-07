package com.kma.musicplayer.service

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.Player
import com.kma.musicplayer.R
import com.kma.musicplayer.model.RepeatMode
import com.kma.musicplayer.model.Song
import com.kma.musicplayer.ui.customview.BottomMiniAudioPlayer
import com.kma.musicplayer.utils.AudioPlayerManager

class PlaySongService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlaySongService {
            return this@PlaySongService
        }
    }

    override fun onBind(p0: Intent?): IBinder = binder

    var songs: MutableList<Song> = mutableListOf()
    var currentIndex: Int = 0
    var playingSong = MutableLiveData<Song>()
    var isPlayRandomlyEnabled = MutableLiveData<Boolean>().apply {
        value = false
    }
    var repeatMode = MutableLiveData<RepeatMode>().apply {
        value = RepeatMode.NONE
    }
    var isPlaying = MutableLiveData<Boolean>().apply {
        value = false
    }
    var thumbnailRotation = MutableLiveData<Float>().apply {
        value = 0f
    }
    var bottomMiniAudioPlayer: BottomMiniAudioPlayer? = null

    private var _audioPlayerManager: AudioPlayerManager? = null
    val audioPlayerManager: AudioPlayerManager?
        get() = _audioPlayerManager

    override fun onCreate() {
        super.onCreate()
        setupThumbnailAnimation()
        _audioPlayerManager = AudioPlayerManager(this, songs)
        _audioPlayerManager?.simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    when (repeatMode.value) {
                        RepeatMode.NONE -> {
                            if (currentIndex == songs.size - 1) {
                                isPlaying.value = false
                            } else {
                                playNext()
                            }
                        }
                        RepeatMode.REPEAT_ONE -> playAt(currentIndex)
                        RepeatMode.REPEAT_ALL -> playNext()
                        null -> TODO()
                    }
                }
            }
        })
    }

    private fun setupThumbnailAnimation() {
        val va = ValueAnimator.ofFloat(0f, 360f)
        va.duration = 15000 //in millis
        va.addUpdateListener { animation ->
            thumbnailRotation.value = animation.animatedValue as Float
        }
        va.repeatCount = ValueAnimator.INFINITE
        va.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun pause() {
        _audioPlayerManager?.simpleExoPlayer?.pause()
        isPlaying.value = false
    }

    fun resume() {
        _audioPlayerManager?.simpleExoPlayer?.play()
        isPlaying.value = true
    }

    fun addMore(songs: List<Song>) {
        this.songs.addAll(songs)
    }

    fun playNext() {
        if (isPlayRandomlyEnabled.value == true) {
            val randomIndex = (0 until songs.size).random()
            updateCurrentIndexValue(randomIndex)
        } else {
            val nextIndex = if (currentIndex == songs.size - 1) 0 else currentIndex + 1
            updateCurrentIndexValue(nextIndex)
        }
        playAt(currentIndex)
    }

    fun playPrevious() {
        val index = if (currentIndex == 0) songs.size - 1 else currentIndex - 1
        updateCurrentIndexValue(index)
        playAt(currentIndex)
    }

    fun playAt(index: Int) {
        updateCurrentIndexValue(index)
        _audioPlayerManager?.play(currentIndex)
        isPlaying.value = true
    }

    fun setPlayRandomlyEnabled(isEnabled: Boolean) {
        isPlayRandomlyEnabled.value = isEnabled
    }

    fun setRepeatMode(mode: RepeatMode) {
        repeatMode.value = mode
    }

    fun changeCurrentIndex(index: Int) {
        currentIndex = index
    }

    private fun updateCurrentIndexValue(index: Int) {
        currentIndex = index
        playingSong.value = songs[currentIndex]
    }

    override fun onDestroy() {
        super.onDestroy()
        _audioPlayerManager?.releasePlayer()
    }
}