package com.example.mylibrary.basiclayout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Row_Basiclayout(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {
     Row(modifier = modifier,
         horizontalArrangement = horizontalArrangement,
         verticalAlignment = verticalAlignment
         ) {
         content()
     }
}