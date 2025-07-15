package com.example.commit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.adapter.AuthorCardAdapter
import com.example.commit.adapter.HomeCardAdapter
import com.example.commit.adapter.ReviewCardAdapter
import com.example.commit.activity.AlarmActivity
import com.example.commit.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.ivAlarm.setOnClickListener {
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }

        binding.rvTodayRecommendations.apply {
            adapter = HomeCardAdapter(listOf("추천1", "추천2", "추천3", "추천4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewRegistrations.apply {
            adapter = HomeCardAdapter(listOf("추천1", "추천2", "추천3", "추천4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvHotContent.apply {
            adapter = HomeCardAdapter(listOf("추천1", "추천2", "추천3", "추천4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvDeadlineContent.apply {
            adapter = HomeCardAdapter(listOf("추천1", "추천2", "추천3", "추천4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvReviewContent.apply {
            adapter = ReviewCardAdapter(listOf("리뷰1", "리뷰2", "리뷰3", "리뷰4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewAuthorContent.apply {
            adapter = AuthorCardAdapter(listOf("작가1", "작가2", "작가3", "작가4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}