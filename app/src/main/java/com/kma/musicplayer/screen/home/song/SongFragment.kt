package com.kma.musicplayer.screen.home.song

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kma.musicplayer.R
import com.kma.musicplayer.databinding.FragmentSongBinding
import com.kma.musicplayer.network.common.ApiCallState
import com.kma.musicplayer.screen.core.BaseFragment
import com.kma.musicplayer.screen.songselector.SongSelectorActivity
import com.kma.musicplayer.utils.Constant
import java.io.Serializable

class SongFragment : BaseFragment<FragmentSongBinding>() {

    private lateinit var songViewModel: SongViewModel
    private lateinit var songAdapter: SongAdapter

    override fun getContentView(): Int = R.layout.fragment_song

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songViewModel = ViewModelProvider(requireActivity()).get(SongViewModel::class.java)
        setupListeners()
        setupObserver()
    }

    private fun setupListeners() {
        dataBinding.ivCheckbox.setOnClickListener {
            showActivity(SongSelectorActivity::class.java, Bundle().apply {
                putSerializable(Constant.BUNDLE_SONG, songViewModel.songs as Serializable)
            })
        }
    }

    private fun setupObserver() {
        songViewModel.apiCallState.observe(viewLifecycleOwner) {
            when (it) {
                ApiCallState.LOADING -> {
                    dataBinding.rvSongs.visibility = View.GONE
                    dataBinding.pbLoading.visibility = View.VISIBLE
                    dataBinding.tvError.visibility = View.GONE
                }

                ApiCallState.SUCCESS -> {
                    dataBinding.rvSongs.visibility = View.VISIBLE
                    dataBinding.pbLoading.visibility = View.GONE
                    dataBinding.tvError.visibility = View.GONE
                    setupRecyclerView()
                }

                ApiCallState.ERROR -> {
                    dataBinding.rvSongs.visibility = View.GONE
                    dataBinding.pbLoading.visibility = View.GONE
                    dataBinding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(songViewModel.songs) {
            // handle song click
        }
        dataBinding.rvSongs.adapter = songAdapter
    }
}