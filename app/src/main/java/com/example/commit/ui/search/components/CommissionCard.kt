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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.search.components.Tag
import java.io.Serializable

data class Commission(
    val commissionId: Int,
    val nickname: String,
    val title: String,
    val tags: List<String>,
    val thumbnailImageUrl: String,
    val isBookmarked: Boolean
) : Serializable

@Composable
fun CommissionCard(
    commission: Commission,
    modifier: Modifier = Modifier,
    onBookmarkToggle: (Int, Boolean) -> Unit = { _, _ -> }
) {
    var bookmarked by rememberSaveable(commission.commissionId) { mutableStateOf(commission.isBookmarked) }

    LaunchedEffect(commission.isBookmarked) {
        bookmarked = commission.isBookmarked
    }

    Card(
        modifier = modifier.width(160.dp).height(180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                Image(painter = painterResource(id = R.drawable.ic_profile), contentDescription = "프로필",
                    modifier = Modifier.size(16.dp).clip(CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = commission.nickname, style = CommitTypography.bodyMedium, color = Color(0xFF2B2B2B))
            }

            Spacer(modifier = Modifier.height(6.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(commission.thumbnailImageUrl).crossfade(true).build(),
                contentDescription = "커미션 썸네일",
                modifier = Modifier.fillMaxWidth().width(160.dp).height(95.dp).padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F3F3)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()) {
                Text(text = commission.title, style = CommitTypography.bodyMedium, color = Color(0xFF2B2B2B),
                    maxLines = 1, modifier = Modifier.weight(1f))

                val bookmarkResId = if (bookmarked) R.drawable.ic_select_bookmarket else R.drawable.ic_unselect_bookmarket
                Icon(
                    painter = painterResource(id = bookmarkResId),
                    contentDescription = if (bookmarked) "북마크 해제" else "북마크",
                    modifier = Modifier.size(16.dp).clickable {
                        val newState = !bookmarked
                        bookmarked = newState
                        onBookmarkToggle(commission.commissionId, newState)
                    },
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(start = 8.dp)) {
                commission.tags.forEachIndexed { index, tag ->
                    Tag(text = tag, isSelected = index == 0, fontSize = 8.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommissionCardPreview() {
    CommissionCard(
        commission = Commission(
            commissionId = 1,
            nickname = "작가1",
            title = "낙서 타입 커미션",
            tags = listOf("#일러스트", "#캐릭터"),
            thumbnailImageUrl = "https://picsum.photos/400/300",
            isBookmarked = true
        )
    )
}
