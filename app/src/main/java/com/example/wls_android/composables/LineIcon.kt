package com.example.wls_android.composables

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
import com.example.wls_android.R
import com.example.wls_android.data.Line

@Composable
fun LineIcon(line : Line, modifier : Modifier = Modifier) {
    var color = colorResource(id = R.color.line_bus)
    when (line.type) {
        0 -> color = colorResource(id = R.color.line_bus)
        1 -> color = colorResource(id = R.color.line_tram)
        2 -> {
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

        else -> color = colorResource(id = R.color.line_miscellaneous)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(5.dp),
        modifier = modifier.wrapContentSize()

    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = if (line.id.length <= 3) Modifier.wrapContentHeight().width(40.dp) else Modifier.wrapContentSize()
        ) {
            Text(
                text = line.id,
                modifier = Modifier.padding(2.dp),
                color = Color.White
            )
        }
    }
}