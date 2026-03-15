package com.misistema.elahora.presentation.settings

import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.misistema.elahora.domain.repository.GithubRepository
import com.misistema.elahora.presentation.theme.Black
import com.misistema.elahora.presentation.theme.Typography
import com.misistema.elahora.presentation.theme.White
import io.noties.markwon.Markwon

import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownReaderScreen(
    fileName: String,
    viewModel: MarkdownViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileName.uppercase(), style = Typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = Black, 
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    Text(
                        text = state.error!!, 
                        style = Typography.bodyLarge, 
                        color = Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.content != null -> {
                    AndroidView(
                        factory = { ctx ->
                            TextView(ctx).apply {
                                setTextColor(android.graphics.Color.BLACK)
                            }
                        },
                        update = { textView ->
                            val markwon = Markwon.create(context)
                            markwon.setMarkdown(textView, state.content!!)
                        },
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}
