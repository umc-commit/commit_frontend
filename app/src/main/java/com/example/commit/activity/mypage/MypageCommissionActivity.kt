package com.example.commit.activity.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.R
import com.example.commit.adapter.mypage.CommissionListAdapter
import com.example.commit.data.model.entities.CommissionListItem
import com.example.commit.databinding.ActivityMypageCommissionBinding
import com.example.commit.databinding.BottomSheetCommissionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class MyPageCommissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageCommissionBinding
    private lateinit var commissionListAdapter: CommissionListAdapter
    private var originalCommissionList: List<CommissionListItem> = listOf() // 원본 데이터 리스트
    private var currentSortOption: SortOption = SortOption.LATEST // 현재 정렬 기준

    // 정렬 옵션을 정의하는 enum 클래스
    enum class SortOption {
        LATEST, // 최신순
        OLDEST, // 오래된 순
        LOW_PRICE, // 저가순
        HIGH_PRICE // 고가순
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageCommissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        // 더미 데이터 생성
        originalCommissionList = listOf(
            CommissionListItem(
                date = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 4) }.time, // 2025년 6월 4일
                thumbnailResId = R.drawable.commission_complete_1,
                title = "포짚",
                description = "낙서 타입 커미션",
                price = 10000,
                status = "거래완료"
            ),
            CommissionListItem(
                date = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 4) }.time, // 2025년 6월 4일
                thumbnailResId = R.drawable.commission_complete_2,
                title = "연어맛나쵸",
                description = "2인 커플 커미션",
                price = 15000,
                status = "거래완료"
            ),
            CommissionListItem(
                date = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 3) }.time, // 2025년 6월 3일
                thumbnailResId = R.drawable.commission_complete_3,
                title = "위시",
                description = "표지 일러스트 커미션",
                price = 40000,
                status = "거래완료"
            ),
            CommissionListItem(
                date = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 5) }.time, // 2025년 6월 5일
                thumbnailResId = R.drawable.commission_complete_4,
                title = "주현",
                description = "LD 풀채색 타입",
                price = 30000,
                status = "거래완료"
            )
        )


        // RecyclerView 설정
        commissionListAdapter = CommissionListAdapter(originalCommissionList.sortedByDescending { it.date }) // 기본 정렬: 최신순
        binding.rvCommissionList.apply {
            layoutManager = LinearLayoutManager(this@MyPageCommissionActivity)
            adapter = commissionListAdapter
        }

        // 정렬 옵션 클릭 리스너
        binding.tvSortOption.setOnClickListener {
            showSortBottomSheet()
        }
    }

    // 정렬 BottomSheetDialog 표시 함수
    private fun showSortBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetBinding = BottomSheetCommissionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        // 배경 투명 & 그림자 효과
        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0.6f)
        }

        // 현재 선택된 정렬 옵션에 체크
        when (currentSortOption) {
            SortOption.LATEST -> sheetBinding.rgSortOptions.check(R.id.rb_latest)
            SortOption.OLDEST -> sheetBinding.rgSortOptions.check(R.id.rb_oldest)
            SortOption.LOW_PRICE -> sheetBinding.rgSortOptions.check(R.id.rb_low_price)
            SortOption.HIGH_PRICE -> sheetBinding.rgSortOptions.check(R.id.rb_high_price)
        }

        // 정렬 옵션 선택 시 처리
        sheetBinding.rgSortOptions.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.rb_latest -> SortOption.LATEST
                R.id.rb_oldest -> SortOption.OLDEST
                R.id.rb_low_price -> SortOption.LOW_PRICE
                R.id.rb_high_price -> SortOption.HIGH_PRICE
                else -> currentSortOption
            }
            if (selectedOption != currentSortOption) {
                currentSortOption = selectedOption
                sortCommissionList(currentSortOption)
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }


    // 리스트 정렬 및 업데이트 함수
    private fun sortCommissionList(sortOption: SortOption) {
        val sortedList = when (sortOption) {
            SortOption.LATEST -> originalCommissionList.sortedByDescending { it.date }
            SortOption.OLDEST -> originalCommissionList.sortedBy { it.date }
            SortOption.LOW_PRICE -> originalCommissionList.sortedBy { it.price }
            SortOption.HIGH_PRICE -> originalCommissionList.sortedByDescending { it.price }
        }
        commissionListAdapter.updateList(sortedList) // 어댑터에 데이터 업데이트 요청
    }
}
