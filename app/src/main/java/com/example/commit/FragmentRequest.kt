package com.example.commit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.databinding.FragmentRequestBinding

class FragmentRequest : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedButton: Button
    private lateinit var adapter: RequestsAdapter
    private val allRequests = listOf(
        Request(1, "IN_PROGRESS", "낙서 타입 커미션", 16000, "", Artist(1, "사과")),
        Request(2, "DONE", "2인 캐릭터 세트", 16000, "", Artist(2, "감자")),
        Request(3, "REQUESTED", "라인 작업 커미션", 20000, "", Artist(3, "배"))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedButton = binding.btnAll
        selectedButton.isSelected = true

        adapter = RequestsAdapter(allRequests)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val buttons = listOf(binding.btnAll, binding.btnInProgress, binding.btnDone)
        buttons.forEach { button ->
            button.setOnClickListener {
                selectedButton.isSelected = false
                button.isSelected = true
                selectedButton = button
                filterRequests(button.id)
            }
        }
    }

    private fun filterRequests(buttonId: Int) {
        val filtered = when (buttonId) {
            R.id.btnInProgress -> allRequests.filter { it.status == "IN_PROGRESS" }
            R.id.btnDone -> allRequests.filter { it.status == "DONE" }
            else -> allRequests
        }
        adapter = RequestsAdapter(filtered)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
