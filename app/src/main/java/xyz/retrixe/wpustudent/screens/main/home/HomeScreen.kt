package xyz.retrixe.wpustudent.screens.main.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import xyz.retrixe.wpustudent.api.erp.entities.StudentInfo
import xyz.retrixe.wpustudent.models.main.home.HomeViewModel

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentInfo: StudentInfo
) {
    val homeViewModelFactory = HomeViewModel.Factory(httpClient, studentInfo)
    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
    val data by homeViewModel.data.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        when (data) {
            is HomeViewModel.Data.Loading -> {
                CircularProgressIndicator(Modifier.size(192.dp).padding(48.dp))
            }

            is HomeViewModel.Data.Loaded -> {
                val imageData = (data as HomeViewModel.Data.Loaded).profilePicture
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(192.dp).clip(CircleShape)
                )
            }

            else -> {
                Box(
                    Modifier
                        .size(192.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error loading profile picture",
                        modifier = Modifier.size(96.dp).align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        val middleName =
            if (studentInfo.middleName == "") " "
            else " ${studentInfo.middleName} "
        Text(studentInfo.firstName + middleName + studentInfo.lastName,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp)
        Text("PRN ${studentInfo.globalId}",
            textAlign = TextAlign.Center,
            fontSize = 24.sp)
        Spacer(Modifier.height(8.dp))
        Text(studentInfo.termName,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline)
        Text(studentInfo.courseFamilyName,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline)
    }
}
