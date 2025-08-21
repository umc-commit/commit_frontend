package com.example.commit.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commit.R
import com.example.commit.connection.dto.RequestItem
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.request.components.FilterRow
import com.example.commit.ui.request.components.RequestCard
import com.example.commit.viewmodel.RequestViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.commit.databinding.BottomSheetCommissionBinding

class FragmentRequest : Fragment() {

    private val requestViewModel: RequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val requestList by requestViewModel.requestList.collectAsState(initial = emptyList())

                LaunchedEffect(Unit) {
                    requestViewModel.loadRequests(requireContext())
                }

                RequestScreen(
                    requests = requestList,
                    onCardClick = { item ->
                        val detailFragment = FragmentRequestDetail().apply {
                            arguments = Bundle().apply {
                                putInt("requestId", item.requestId)
                            }
                        }

                        (requireActivity() as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.Nav_Frame, detailFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                )
            }
        }
    }
}

@Composable
fun RequestScreen(
    requests: List<RequestItem>,
    onCardClick: (RequestItem) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("전체") }
    var sortOption by remember { mutableStateOf(SortOption.LATEST) }
    val context = LocalContext.current

    // 필터링
    val filteredRequests = when (selectedStatus) {
        "진행 중" -> requests.filter { it.status == "IN_PROGRESS" || it.status == "APPROVED" }
        "작업 완료" -> requests.filter { it.status == "COMPLETED" || it.status == "SUBMITTED" }
        else -> requests
    }

    // 정렬
    val sortedRequests = when (sortOption) {
        SortOption.LATEST -> filteredRequests.sortedByDescending { it.requestId }
        SortOption.OLDEST -> filteredRequests.sortedBy { it.requestId }
        SortOption.LOW_PRICE -> filteredRequests.sortedBy { it.price }
        SortOption.HIGH_PRICE -> filteredRequests.sortedByDescending { it.price }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .background(Color.White)
        ) {
            Text(
                text = "신청함",
                style = CommitTypography.headlineSmall.copy(fontSize = 18.sp),
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilterRow(
            selected = selectedStatus,
            onSelect = { selectedStatus = it },
            onSortClick = {
                val bottomSheetDialog = BottomSheetDialog(context)
                val sheetBinding = BottomSheetCommissionBinding.inflate(LayoutInflater.from(context))
                bottomSheetDialog.setContentView(sheetBinding.root)

                // 배경 투명 & 그림자 효과
                bottomSheetDialog.window?.apply {
                    setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
                    setDimAmount(0.6f)
                }

                // 현재 선택된 정렬 옵션 반영
                when (sortOption) {
                    SortOption.LATEST -> sheetBinding.rgSortOptions.check(R.id.rb_latest)
                    SortOption.OLDEST -> sheetBinding.rgSortOptions.check(R.id.rb_oldest)
                    SortOption.LOW_PRICE -> sheetBinding.rgSortOptions.check(R.id.rb_low_price)
                    SortOption.HIGH_PRICE -> sheetBinding.rgSortOptions.check(R.id.rb_high_price)
                }

                // 선택 이벤트 처리
                sheetBinding.rgSortOptions.setOnCheckedChangeListener { _, checkedId ->
                    sortOption = when (checkedId) {
                        R.id.rb_latest -> SortOption.LATEST
                        R.id.rb_oldest -> SortOption.OLDEST
                        R.id.rb_low_price -> SortOption.LOW_PRICE
                        R.id.rb_high_price -> SortOption.HIGH_PRICE
                        else -> sortOption
                    }
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(sortedRequests) { item ->
                Column {
                    RequestCard(item = item, onClick = { onCardClick(item) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


enum class SortOption {
    LATEST, OLDEST, LOW_PRICE, HIGH_PRICE
}
