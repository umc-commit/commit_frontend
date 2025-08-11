package com.example.commit.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
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
            // 정렬은 현재 latest만 – excludeFullSlots만 토글
            excludeClosed = !excludeClosed
            fetchBookmarks(reset = true)

            // 아이콘 효과만 유지
            binding.btnFilter.setImageResource(R.drawable.ic_sort_mint)
            binding.btnFilter.postDelayed({ binding.btnFilter.setImageResource(R.drawable.ic_sort) }, 400)
        }
    }

    private fun setupDeleteBar() {
        binding.deleteButton.setOnClickListener {
            val ids = adapter.getSelectedBookmarkIds()
            if (ids == null || ids.isEmpty()) {
                Log.d("FragmentBookmark", "서버에서 bookmarkId 필드 반영 후 이용 가능합니다.")
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
                        // UI 갱신: 삭제된 항목 제거
                        adapter.removeByBookmarkIds(body.deletedIds)
                        updateDeleteButton()

                        // 전체 새로고침(페이징/필터 고려) + 다른 화면과 동기화
                        fetchBookmarks(reset = true)
                        requireContext().sendBroadcast(
                            Intent("ACTION_BOOKMARK_CHANGED").setPackage(requireContext().packageName))

                        Log.d("FragmentBookmark", body.message)
                        if (body.notFoundIds.isNotEmpty()) {
                            Log.d("FragmentBookmark", "notFoundIds=${body.notFoundIds}")
                        }
                    } else {
                        Log.d("FragmentBookmark", "선택 삭제 실패: ${response.errorBody()?.string()}")
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
        service.getBookmarks(sort = "latest", page = page, limit = limit, excludeFullSlots = excludeClosed)
            .enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>,
                    response: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>
                ) {
                    val s = response.body()?.success ?: run {
                        Log.d(TAG, "응답 파싱 실패 또는 body=null")
                        return
                    }
                    if (reset) serverList.clear()
                    serverList.addAll(s.items)
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
        requireContext().unregisterReceiver(bookmarkChangedReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
