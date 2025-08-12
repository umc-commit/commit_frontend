package com.example.commit.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.adapter.bookmark.BookmarkAdapter
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.FragmentBookmarkBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentBookmark : Fragment() {

    companion object { private const val TAG = "FragmentBookmark" }

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookmarkAdapter
    private val serverList = mutableListOf<RetrofitClient.BookmarkCommissionItem>()
    private var isEditMode = false
    private var excludeClosed = false
    private var page = 1
    private val limit = 12
    private var sort = "latest"

    private val bookmarkChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            fetchBookmarks(reset = true) // 카드에서 추가/삭제하면 자동 새로고침
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupToolbar()
        setupDeleteBar()
        fetchBookmarks(reset = true)
    }

    private fun setupRecycler() {
        adapter = BookmarkAdapter(serverList, isEditMode) { updateDeleteButton() }
        binding.rvBookmarkList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvBookmarkList.adapter = adapter

        val outerPaddingPx = resources.getDimensionPixelSize(R.dimen.bookmark_outer_padding)
        val spacingPx = resources.getDimensionPixelSize(R.dimen.bookmark_item_spacing)
        if (binding.rvBookmarkList.itemDecorationCount == 0) {
            binding.rvBookmarkList.addItemDecoration(GridSpacingItemDecoration(2, outerPaddingPx, spacingPx))
        }
    }

    private fun setupToolbar() {
        binding.btnEdit.setOnClickListener {
            isEditMode = !isEditMode
            (requireActivity() as MainActivity).showBottomNav(!isEditMode)
            binding.btnEdit.setImageResource(if (isEditMode) R.drawable.ic_x else R.drawable.ic_bm_pencil)
            binding.bookmarkDeletebar.visibility = if (isEditMode) View.VISIBLE else View.GONE
            adapter.setEditMode(isEditMode)
            updateDeleteButton()
        }

        binding.btnFilter.setOnClickListener {
            binding.btnFilter.setImageResource(R.drawable.ic_sort_mint)
            showFilterBottomSheet() // 바텀시트 열기
        }
    }

    private fun setupDeleteBar() {
        binding.deleteButton.setOnClickListener {
            val ids = adapter.getSelectedBookmarkIds()
            if (ids.isEmpty()) {
                // 선택된 항목이 없으면 종료
                return@setOnClickListener
            }

            val service = RetrofitObject.getRetrofitService(requireContext())
            binding.deleteButton.isEnabled = false

            service.deleteBookmarksBulk(
                RetrofitClient.BookmarkBulkDeleteRequest(ids)
            ).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkBulkDeleteSuccess>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkBulkDeleteSuccess>>,
                    response: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkBulkDeleteSuccess>>
                ) {
                    binding.deleteButton.isEnabled = true
                    val body = response.body()?.success
                    if (response.isSuccessful && body != null) {
                        val bookmarkIds = body.bookmarkIds.orEmpty()
                        adapter.removeByBookmarkIds(bookmarkIds)
                        updateDeleteButton()

                        fetchBookmarks(reset = true)
                        requireContext().sendBroadcast(
                            Intent("ACTION_BOOKMARK_CHANGED").setPackage(requireContext().packageName)
                        )

                        Log.d(TAG, body.message ?: "선택 삭제 완료")
                    } else {
                        Log.d(TAG, "선택 삭제 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkBulkDeleteSuccess>>,
                    t: Throwable
                ) {
                    binding.deleteButton.isEnabled = true
                    Log.d("FragmentBookmark", "네트워크 오류(선택 삭제): ${t.message}")
                }
            })
        }
    }

    private fun updateDeleteButton() {
        val count = adapter.getSelected().size
        binding.deleteButton.isEnabled = count > 0
        binding.deleteButton.text = "북마크 삭제 $count"
    }

    private fun fetchBookmarks(reset: Boolean) {
        if (reset) page = 1
        val service = RetrofitObject.getRetrofitService(requireContext())
        service.getBookmarks(sort = sort, page = page, limit = limit, excludeFullSlots = excludeClosed)
            .enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>,
                    response: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>
                ) {
                    val s = response.body()?.success ?: return
                    val items = s.items.toMutableList()

                    when (sort) {
                        "lowPrice"  -> items.sortBy { it.minPrice }          // 가격 낮은 순
                        "highPrice" -> items.sortByDescending { it.minPrice } // 가격 높은 순
                        else -> { }
                    }

                    if (reset) serverList.clear()
                    serverList.addAll(items)
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>,
                    t: Throwable
                ) {
                    Log.d(TAG, "네트워크 오류(목록): ${t.message}")
                }
            })
    }

    private fun showFilterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_bookmark, null)
        dialog.setContentView(view)

        // 뷰 바인딩
        val ivToggle = view.findViewById<ImageView>(R.id.iv_closed_filter)
        val rgSort = view.findViewById<RadioGroup>(R.id.rg_sort_options)
        val rbLatest = view.findViewById<RadioButton>(R.id.rb_latest)
        val rbLow = view.findViewById<RadioButton>(R.id.rb_low_price)
        val rbHigh = view.findViewById<RadioButton>(R.id.rb_high_price)
        val btnApply = view.findViewById<AppCompatButton>(R.id.btn_apply_filter)

        // 현재 값으로 초기 상태 세팅
        var tempExclude = excludeClosed
        fun updateToggle() {
            ivToggle.setImageResource(if (tempExclude) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off)
        }
        updateToggle()

        when (sort) {
            "latest" -> rbLatest.isChecked = true
            "lowPrice" -> rbLow.isChecked = true
            "highPrice" -> rbHigh.isChecked = true
        }

        // 토글 클릭 처리
        ivToggle.setOnClickListener {
            tempExclude = !tempExclude
            updateToggle()
        }

        // 적용 버튼
        btnApply.setOnClickListener {
            // 정렬 선택값 매핑
            sort = when (rgSort.checkedRadioButtonId) {
                R.id.rb_low_price -> "lowPrice"
                R.id.rb_high_price -> "highPrice"
                else -> "latest"
            }
            excludeClosed = tempExclude

            fetchBookmarks(reset = true)
            dialog.dismiss()
            updateFilterIcon()
        }

        dialog.setOnDismissListener { updateFilterIcon() }
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    private fun updateFilterIcon() {
        // 기본값에서 벗어나 있으면 민트, 기본이면 기본 아이콘
        val isFiltered = excludeClosed || sort != "latest"
        binding.btnFilter.setImageResource(
            if (isFiltered) R.drawable.ic_sort_mint else R.drawable.ic_sort
        )
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val outerPadding: Int,
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            if (column == 0) { outRect.left = outerPadding; outRect.right = spacing / 2 }
            else { outRect.left = spacing / 2; outRect.right = outerPadding }
            if (position < spanCount) outRect.top = spacing
            outRect.bottom = spacing
        }
    }

    override fun onResume() {
        super.onResume()
        updateFilterIcon()
        // 돌아올 때마다 최신화
        fetchBookmarks(reset = true)
        val filter = IntentFilter("ACTION_BOOKMARK_CHANGED")

        // OS 버전에 상관없이 안전하게 등록 (API 33+에서는 NOT_EXPORTED로 등록)
        ContextCompat.registerReceiver(
            requireContext(),
            bookmarkChangedReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        // 필터 초기화
        sort = "latest"
        excludeClosed = false
        updateFilterIcon()
        fetchBookmarks(reset = true)
        try {
            requireContext().unregisterReceiver(bookmarkChangedReceiver)
        } catch (_: Exception) { }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
