package com.example.aalyzer.basiclayout

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow_Basiclayout(content:@Composable ()->Unit){
    FlowRow(modifier = Modifier.fillMaxWidth()) {
        content()
    }
}