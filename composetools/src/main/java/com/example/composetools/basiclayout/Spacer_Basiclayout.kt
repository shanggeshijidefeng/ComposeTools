package com.example.aalyzer.basiclayout

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.height(){
    Spacer(modifier = Modifier.height(this) )
}


@Composable
fun Dp.width() {
    Spacer(modifier = Modifier.width(this))
}