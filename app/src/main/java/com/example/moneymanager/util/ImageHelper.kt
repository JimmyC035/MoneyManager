package com.example.moneymanager.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageHelper @Inject constructor() {
    
    fun createImageUri(@ApplicationContext context: Context): Uri {
        try {
            val photoFile = File(
                context.getExternalFilesDir(null),
                "Receipt_${System.currentTimeMillis()}.jpg"
            )
            
            Log.d("ImageHelper", "創建圖片文件: ${photoFile.absolutePath}")
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            
            Log.d("ImageHelper", "創建圖片 URI: $uri")
            return uri
        } catch (e: Exception) {
            Log.e("ImageHelper", "創建圖片 URI 失敗", e)
            throw e
        }
    }
    
    fun getImageFromUri(@ApplicationContext context: Context, uri: Uri): InputImage {
        try {
            Log.d("ImageHelper", "從 URI 獲取圖片: $uri")
            return InputImage.fromFilePath(context, uri)
        } catch (e: Exception) {
            Log.e("ImageHelper", "從 URI 獲取圖片失敗", e)
            throw e
        }
    }
} 