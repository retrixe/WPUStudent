package xyz.retrixe.wpustudent.screens.main.home

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import wpustudent.composeapp.generated.resources.Res
import wpustudent.composeapp.generated.resources.baseline_account_circle_24
import wpustudent.composeapp.generated.resources.baseline_warning_24
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.kmp.decodeBytesToImageBitmap
import kotlin.io.encoding.Base64

@Composable
fun HomeScreen(paddingValues: PaddingValues, studentBasicInfo: StudentBasicInfo) {
    val profilePictureB64Data = studentBasicInfo.profilePicture.substringAfter(",")
    val profilePicture =
        if (profilePictureB64Data.isEmpty()) null
        else try {
            Base64.decode(profilePictureB64Data)
        } catch (_: Exception) {
            null
        }

    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        if (profilePicture == null) {
            Box(
                Modifier
                    .size(192.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    painterResource(Res.drawable.baseline_warning_24),
                    contentDescription = "Error loading profile picture",
                    modifier = Modifier.size(96.dp).align(Alignment.Center)
                )
            }
        } else {
            if (studentBasicInfo.profilePicture.startsWith("data:image/svg+xml;base64,")) {
                Image(
                    painterResource(Res.drawable.baseline_account_circle_24),
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(192.dp).clip(CircleShape),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            } else {
                val bitmap = decodeBytesToImageBitmap(profilePicture)
                Image(
                    bitmap = bitmap,
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(192.dp).clip(CircleShape)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(studentBasicInfo.name,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp)
        Text("PRN ${studentBasicInfo.prn}",
            textAlign = TextAlign.Center,
            fontSize = 24.sp)
        Spacer(Modifier.height(8.dp))
        Text(studentBasicInfo.term,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline)
        Text(studentBasicInfo.section,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline)
    }
}
