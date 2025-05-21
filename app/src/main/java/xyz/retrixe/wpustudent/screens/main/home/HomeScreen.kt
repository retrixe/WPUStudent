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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
import xyz.retrixe.wpustudent.api.endpoints.fetchAsset
import xyz.retrixe.wpustudent.api.entities.StudentBasicInfo
import java.io.Serializable

sealed interface ProfilePictureState : Serializable {
    object Loading : ProfilePictureState {
        @Suppress("unused") private fun readResolve(): Any = Loading
    }

    object Error : ProfilePictureState {
        @Suppress("unused") private fun readResolve(): Any = Error
    }

    data class Loaded(val data: ByteArray) : ProfilePictureState {
        override fun equals(other: Any?) =
            this === other && javaClass == other.javaClass && data.contentEquals(other.data)

        override fun hashCode() = data.contentHashCode()
    }
}

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo
) {
    var profilePicture by rememberSaveable(studentBasicInfo.profilePictureInfo.filePath) {
        mutableStateOf<ProfilePictureState>(ProfilePictureState.Loading)
    }

    LaunchedEffect(studentBasicInfo.profilePictureInfo.filePath) {
        if (profilePicture != ProfilePictureState.Loading) return@LaunchedEffect
        try {
            val asset = fetchAsset(httpClient,
                "iemsfilecontainer",
                studentBasicInfo.profilePictureInfo.filePath,
                "profile-picture.png")
            profilePicture = ProfilePictureState.Loaded(asset)
        } catch (_: Exception) {
            profilePicture = ProfilePictureState.Error
        }
    }

    Column(
        Modifier.fillMaxSize().padding(paddingValues).padding(8.dp, 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (profilePicture) {
            is ProfilePictureState.Loading -> {
                CircularProgressIndicator(Modifier.size(192.dp).padding(48.dp))
            }

            is ProfilePictureState.Loaded -> {
                val imageData = (profilePicture as ProfilePictureState.Loaded).data
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
            if (studentBasicInfo.middleName == "") " "
            else " ${studentBasicInfo.middleName} "
        Text(studentBasicInfo.firstName + middleName + studentBasicInfo.lastName,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp)
        Text("PRN ${studentBasicInfo.globalId}",
            textAlign = TextAlign.Center,
            fontSize = 24.sp)
        Spacer(Modifier.height(8.dp))
        Text(studentBasicInfo.termName,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline)
        Text(studentBasicInfo.courseFamilyName,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline)
    }
}
