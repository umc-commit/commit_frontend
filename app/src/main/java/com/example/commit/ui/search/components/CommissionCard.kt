package com.example.commit.ui.request.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.search.components.Tag
import java.io.Serializable

data class Commission(
    val commissionId: Int,
    val nickname: String,
    val title: String,
    val tags: List<String>
) : Serializable

@Composable
fun CommissionCard(
    commission: Commission,
    modifier: Modifier = Modifier,
    // 북마크 초기값과 콜백(선택): 외부 상태로 올리고 싶으면 사용
    initialBookmarked: Boolean = false,
    onBookmarkToggle: (Boolean) -> Unit = {}
) {
    var isBookmarked by remember { mutableStateOf(initialBookmarked) }

    Card(
        modifier = modifier
            .width(160.dp)
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 프로필 + 닉네임
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "프로필",
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = commission.nickname,
                    style = CommitTypography.bodyMedium,
                    color = Color(0xFF2B2B2B)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 이미지 더미
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(160.dp)
                    .height(95.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F3F3))
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 제목 + 북마크
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = commission.title,
                    style = CommitTypography.bodyMedium,
                    color = Color(0xFF2B2B2B),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                // ▼ 북마크: 눌림/안눌림에 따라 아이콘 변경
                val bookmarkResId = if (isBookmarked) {
                    // 선택(눌림) 상태 아이콘
                    R.drawable.ic_select_bookmarket
                } else {
                    // 미선택(안눌림) 상태 아이콘
                    R.drawable.ic_unselect_bookmarket
                }

                Icon(
                    painter = painterResource(id = bookmarkResId),
                    contentDescription = if (isBookmarked) "북마크 해제" else "북마크",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            isBookmarked = !isBookmarked
                            onBookmarkToggle(isBookmarked)
                        },
                    // 아이콘 자체 색을 쓰도록 유지
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                commission.tags.forEachIndexed { index, tag ->
                    Tag(text = tag, isSelected = index == 0, fontSize = 8.sp)
                }
            }
        }
    }
}
