package at.wls_android.app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import at.wls_android.app.R
import at.wls_android.app.data.Line
import at.wls_android.app.enums.LineType

@Composable
fun LineIcon(
    line: Line,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabledState: Boolean = false
) {
    var color = colorResource(id = R.color.line_miscellaneous)
    when (line.type) {
        LineType.Bus  -> color = colorResource(id = R.color.line_bus)
        LineType.Tram -> color = colorResource(id = R.color.line_tram)
        LineType.Metro -> {
            if (line.id.startsWith("U1", true))
                color = colorResource(id = R.color.line_u1)
            else if (line.id.startsWith("U2", true))
                color = colorResource(id = R.color.line_u2)
            else if (line.id.startsWith("U3", true))
                color = colorResource(id = R.color.line_u3)
            else if (line.id.startsWith("U4", true))
                color = colorResource(id = R.color.line_u4)
            else if (line.id.startsWith("U6", true))
                color = colorResource(id = R.color.line_u6)
        }
        LineType.Night -> color = colorResource(id = R.color.line_night)
         LineType.Misc -> color = colorResource(id = R.color.line_miscellaneous)
    }

    Card(
        colors = if (!enabledState) {
            CardDefaults.cardColors(
                containerColor = color
            )
        } else CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = RoundedCornerShape(5.dp),
        modifier = if (onClick != null) {
            modifier
                .wrapContentSize()
                .clickable {
                    onClick()
                }
        } else modifier.wrapContentSize()

    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = if (line.id.length <= 3) Modifier
                .wrapContentHeight()
                .width(40.dp) else Modifier.wrapContentSize()
        ) {
            Text(
                text = line.id,
                modifier = Modifier.padding(2.dp),
                color = Color.White
            )
        }
    }
}