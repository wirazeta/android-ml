package com.example.wormdetector

import android.Manifest
import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier
){
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if(isPermanentlyDeclined){
                        "Grant Permission"
                    }else{
                        "Ok"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if(isPermanentlyDeclined){
                                onGoToAppSettingsClick()
                            }else{
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        modifier = modifier
    )
}

interface PermissionTextProvider{
    fun getDescription(isPermanentlyDeclined: Boolean):String
}

class WriteExternalStorageTextProvider:PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems you permanently declined camera permission." +
                    "You can go to the app setting to grant it."
        }else{
            "You need to give WRITE_EXTERNAL_PERMISSION to access gallery from this app."
        }
    }
}

class ReadExternalStorageTextProvider:PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems you permanently declined camera permission." +
                    "You can go to the app setting to grant it."
        }else{
            "You need to give READ_EXTERNAL_PERMISSION to access gallery from this app."
        }
    }
}