package at.wls_android.app.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AcordionHeader(
    title: String,
    content: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            Modifier
                .padding(horizontal = 15.dp)
                .fillMaxSize()
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown Arrow",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(30.dp)
            )
        }
    }
}