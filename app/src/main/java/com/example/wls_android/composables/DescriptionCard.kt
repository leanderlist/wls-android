package com.example.wls_android.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wls_android.R
import com.example.wls_android.data.Description
import com.example.wls_android.screens.stringToDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DescriptionCard(descriptions : List<Description>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Text(
                text = "Beschreibung:",
                fontWeight = FontWeight.Bold//TextStyle(fontWeight = FontWeight.Bold)
            )
            Text(
                text = descriptions[0].description,
            )

            if(descriptions.size > 1) {
                for(i in 1 until descriptions.size) {
                    var time : String? = null
                    stringToDateTime(
                        descriptions[i].time.substring(0, descriptions[i].time.indexOf('.')),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )?.let {
                        time = it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    }
                    if(time != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text(
                                    text = "Update: $time",
                                    style = TextStyle(fontSize = 17.sp),
                                    color = colorResource(id = R.color.main_color),
                                )
                                Text(
                                    text = descriptions[i].description,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}