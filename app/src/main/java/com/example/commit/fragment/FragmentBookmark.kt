package com.example.commit.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.adapter.bookmark.BookmarkAdapter
import com.example.commit.data.model.entities.BookmarkItem
import com.example.commit.databinding.FragmentBookmarkBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.commit.databinding.BottomSheetBookmarkBinding
import com.example.commit.R
import com.example.commit.activity.MainActivity

class FragmentBookmark : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookmarkAdapter
    private val fullList = mutableListOf<BookmarkItem>()
    private val filteredList = mutableListOf<BookmarkItem>()
    private var isEditMode = false
    private var excludeClosed = false
    private var sortOption = SortOption.LATEST

    enum class SortOption { LATEST, LOW_PRICE, HIGH_PRICE }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fullList.addAll(generateDummyData())
        applyFilterAndSort()
        setupToolbar()
        setupDeleteButton()
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter(filteredList, isEditMode) { _, _ ->
            updateDeleteButton()
        }
        binding.rvBookmarkList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvBookmarkList.adapter = adapter

        // dp → px 변환
        val outerPaddingPx = resources.getDimensionPixelSize(R.dimen.bookmark_outer_padding) // 28dp
        val spacingPx = resources.getDimensionPixelSize(R.dimen.bookmark_item_spacing) // 25dp

        if (binding.rvBookmarkList.itemDecorationCount == 0) {
            binding.rvBookmarkList.addItemDecoration(
                GridSpacingItemDecoration(2, outerPaddingPx, spacingPx)
            )
        }
    }

    private fun setupToolbar() {
        binding.btnEdit.setOnClickListener {
            isEditMode = !isEditMode

            if (isEditMode) {
                // 하단 바 숨김
                (requireActivity() as MainActivity).showBottomNav(false)
                if (::adapter.isInitialized) {
                    adapter.clearSelection()
                }
                updateDeleteButton()
            } else {
                // 하단 바 보이기
                (requireActivity() as MainActivity).showBottomNav(true)
            }
            // Edit 모드 상태에 따라 이미지 변경
            binding.btnEdit.setImageResource(
                if (isEditMode) R.drawable.ic_x else R.drawable.ic_bm_pencil
            )

            if (isEditMode) {
                if (::adapter.isInitialized) {
                    adapter.clearSelection()
                }
                updateDeleteButton()
            }

            binding.bookmarkDeletebar.visibility = if (isEditMode) View.VISIBLE else View.GONE
            setupRecyclerView()
        }

        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun setupDeleteButton() {
        binding.deleteButton.setOnClickListener {
            val selected = adapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                fullList.removeAll(selected.toSet())
                isEditMode = false
                binding.bookmarkDeletebar.visibility = View.GONE
                binding.btnEdit.setImageResource(R.drawable.ic_bm_pencil)
                applyFilterAndSort()
            }
        }
    }

    private fun updateDeleteButton() {
        val count = adapter.getSelectedItems().size
        binding.deleteButton.isEnabled = count > 0
        binding.deleteButton.text = "북마크 삭제 $count"
    }

    private fun applyFilterAndSort() {
        val sdf = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
        // 필터 적용
        // 필터 적용
        val list = if (excludeClosed) {
            // isClosed == true 이거나 remainingSlots == 0 인 경우 제외
            fullList.filter { !(it.isClosed || it.remainingSlots == 0) }
        } else {fullList}

        // 정렬 적용
        filteredList.clear()
        filteredList.addAll(
            when (sortOption) {
                SortOption.LATEST -> list.sortedByDescending { sdf.parse(it.date)?.time ?: 0L }
                SortOption.LOW_PRICE -> list.sortedBy { it.price } // 가격 오름차순
                SortOption.HIGH_PRICE -> list.sortedByDescending { it.price }   // 가격 내림차순
            }
        )
        setupRecyclerView()
    }

    private fun showFilterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetBookmarkBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        dialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0.6f)
        }

        // 바텀시트가 열릴 때 -> 아이콘을 ic_sort_mint로 변경
        binding.btnFilter.setImageResource(R.drawable.ic_sort_mint)

        // 현재 상태 반영
        sheetBinding.ivClosedFilter.setImageResource(
            if (excludeClosed) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
        )

        sheetBinding.ivClosedFilter.setOnClickListener {
            excludeClosed = !excludeClosed
            sheetBinding.ivClosedFilter.setImageResource(
                if (excludeClosed) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off
            )
        }

        // 정렬 옵션
        when (sortOption) {
            SortOption.LATEST -> sheetBinding.rgSortOptions.check(R.id.rb_latest)
            SortOption.LOW_PRICE -> sheetBinding.rgSortOptions.check(R.id.rb_low_price)
            SortOption.HIGH_PRICE -> sheetBinding.rgSortOptions.check(R.id.rb_high_price)
        }

        // 적용 버튼
        sheetBinding.btnApplyFilter.setOnClickListener {
            sortOption = when (sheetBinding.rgSortOptions.checkedRadioButtonId) {
                R.id.rb_latest -> SortOption.LATEST
                R.id.rb_low_price -> SortOption.LOW_PRICE
                R.id.rb_high_price -> SortOption.HIGH_PRICE
                else -> SortOption.LATEST
            }
            applyFilterAndSort()
            // 필터 아이콘 변경
            binding.btnFilter.setImageResource(R.drawable.ic_sort_mint)
            dialog.dismiss()
        }

        // 바텀시트 닫힐 때 -> 아이콘을 다시 ic_sort로 변경
        dialog.setOnDismissListener {
            binding.btnFilter.setImageResource(R.drawable.ic_sort)
        }

        dialog.show()
    }

    private fun generateDummyData(): List<BookmarkItem> {
        return listOf(
            BookmarkItem(1, "키르", "", "", "커미션 A", listOf("그림", "#LD", "#커플"), 1, false, "2025.07.28", 50000),
            BookmarkItem(2, "자두", "", "", "커미션 B", listOf("그림", "#LD", "#커플"), 0, true, "2025.06.14", 30000),
            BookmarkItem(3, "씨앗호떡", "", "", "커미션 C", listOf("그림", "#LD", "#커플"), 3, false, "2025.07.20", 70000),
            BookmarkItem(4, "토끼", "", "", "커미션 D", listOf("그림", "#LD", "#커플"), 2, true, "2025.05.10", 20000),
            BookmarkItem(5, "하늘", "", "", "커미션 E", listOf("그림", "#LD", "#커플"), 5, false, "2025.07.25", 80000),
            BookmarkItem(6, "모카", "", "", "커미션 F", listOf("그림", "#LD", "#커플"), 1, true, "2025.06.30", 60000),
            BookmarkItem(7, "별빛", "", "", "커미션 G", listOf("그림", "#LD", "#커플"), 4, false, "2025.07.10", 90000),
            BookmarkItem(8, "라떼", "", "", "커미션 H", listOf("그림", "#LD", "#커플"), 2, true, "2025.06.22", 40000),
            BookmarkItem(9, "단풍", "", "", "커미션 I", listOf("그림", "#LD", "#커플"), 0, false, "2025.05.28", 100000),
            BookmarkItem(10, "나비", "", "", "커미션 J", listOf("그림", "#LD", "#커플"), 3, true, "2025.06.05", 15000),
        )
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val outerPadding: Int,  // 바깥 여백 (28dp)
        private val spacing: Int        // 아이템 간 간격 (25dp 기준)
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // 아이템 위치
            val column = position % spanCount // 열 위치 (0, 1)

            // 좌우 간격 계산
            if (column == 0) { // 첫 번째 열
                outRect.left = outerPadding
                outRect.right = spacing / 2
            } else { // 두 번째 열
                outRect.left = spacing / 2
                outRect.right = outerPadding
            }

            // 상단 간격
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
