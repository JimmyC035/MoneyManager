package com.example.moneymanager.presentation.screens.expense

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.entity.Transaction
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.util.ImageHelper
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val imageHelper: ImageHelper,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()
    
    // 支出類別列表
    val categories = listOf("餐飲", "交通", "購物", "娛樂", "醫療", "住宿", "其他")
    
    private var tempImageUri: Uri? = null
    private val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    
    init {
        // 設置初始日期為今天
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        setDate(today)
    }
    
    fun setAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }
    
    fun setCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    
    fun setDate(date: String) {
        _uiState.update { it.copy(date = date) }
    }
    
    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }
    
    fun toggleCategoryDropdown() {
        _uiState.update { it.copy(
            showCategoryDropdown = !it.showCategoryDropdown
        ) }
    }
    
    fun checkCameraPermission(
        context: Context,
        permissionLauncher: ActivityResultLauncher<String>
    ) {
        Log.d("AddExpenseViewModel", "檢查相機權限")
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("AddExpenseViewModel", "相機權限已授予")
                tempImageUri = imageHelper.createImageUri(context)
                Log.d("AddExpenseViewModel", "創建臨時圖片 URI: $tempImageUri")
                _uiState.update { it.copy(imageUri = tempImageUri) }
                _uiState.update { it.copy(shouldLaunchCamera = true) }
                Log.d("AddExpenseViewModel", "設置 shouldLaunchCamera = true")
            }
            else -> {
                Log.d("AddExpenseViewModel", "請求相機權限")
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    fun startCamera(
        context: Context,
        cameraLauncher: ActivityResultLauncher<Uri>?
    ) {
        Log.d("AddExpenseViewModel", "啟動相機")
        tempImageUri = imageHelper.createImageUri(context)
        Log.d("AddExpenseViewModel", "創建臨時圖片 URI: $tempImageUri")
        _uiState.update { it.copy(
            imageUri = tempImageUri,
            showPermissionDeniedDialog = false,
            shouldLaunchCamera = false
        ) }
        Log.d("AddExpenseViewModel", "啟動相機啟動器: $cameraLauncher")
        cameraLauncher?.launch(tempImageUri)
    }
    
    fun processImage(context: Context) {
        viewModelScope.launch {
            tempImageUri?.let { uri ->
                try {
                    Log.d("AddExpenseViewModel", "開始處理圖片: $uri")
                    val image = imageHelper.getImageFromUri(context, uri)
                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            Log.d("AddExpenseViewModel", "文字識別成功: ${visionText.text}")
                            // 簡化處理：直接提取文字並更新 UI 狀態
                            val extractedText = visionText.text
                            
                            // 嘗試從文字中提取金額（尋找數字格式）
                            val amountRegex = Regex("\\d+([.,]\\d{1,2})?")
                            val amountMatches = amountRegex.findAll(extractedText)
                            
                            // 如果找到金額，使用第一個匹配項
                            if (amountMatches.any()) {
                                val amount = amountMatches.first().value.replace(",", ".")
                                setAmount(amount)
                            }
                            
                            // 將照片 URI 和識別的文字添加到備註中
                            val currentNote = _uiState.value.note
                            val newNote = if (currentNote.isBlank()) 
                                "OCR 識別文字: $extractedText" 
                            else 
                                "$currentNote\nOCR 識別文字: $extractedText"
                            setNote(newNote)
                            
                            // 更新 UI 狀態
                            _uiState.update { it.copy(
                                fullRecognizedText = extractedText
                            ) }
                        }
                        .addOnFailureListener { e ->
                            // 處理錯誤
                            Log.e("AddExpenseViewModel", "文字識別失敗", e)
                            e.printStackTrace()
                            _uiState.update { it.copy(
                                fullRecognizedText = ""
                            ) }
                        }
                } catch (e: Exception) {
                    // 處理錯誤
                    Log.e("AddExpenseViewModel", "處理圖片失敗", e)
                    e.printStackTrace()
                    _uiState.update { it.copy(
                        fullRecognizedText = ""
                    ) }
                }
            }
        }
    }
    
    fun handlePermissionDenied() {
        _uiState.update { it.copy(showPermissionDeniedDialog = true) }
    }
    
    fun dismissPermissionDeniedDialog() {
        _uiState.update { it.copy(showPermissionDeniedDialog = false) }
    }
    
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
    
    fun saveExpense(): Boolean {
        try {
            val amountStr = _uiState.value.amount
            if (amountStr.isBlank()) {
                return false
            }
            
            val amount = amountStr.toDoubleOrNull() ?: return false
            val category = _uiState.value.selectedCategory
            val dateStr = _uiState.value.date
            val note = _uiState.value.note
            val imageUri = _uiState.value.imageUri?.toString()
            
            // 解析日期
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dateStr) ?: Date()
            
            // 創建交易對象
            val transaction = Transaction(
                amount = amount,
                category = category,
                date = date,
                note = note,
                imageUri = imageUri
            )
            
            // 保存到資料庫
            viewModelScope.launch {
                transactionRepository.insertTransaction(transaction)
                Log.d("AddExpenseViewModel", "交易已保存到資料庫: $transaction")
            }
            
            return true
        } catch (e: Exception) {
            Log.e("AddExpenseViewModel", "保存交易失敗", e)
            return false
        }
    }
}

data class AddExpenseUiState(
    val amount: String = "",
    val selectedCategory: String = "餐飲",
    val date: String = "",
    val note: String = "",
    val showCategoryDropdown: Boolean = false,
    val imageUri: Uri? = null,
    val fullRecognizedText: String = "",
    val showPermissionDeniedDialog: Boolean = false,
    val shouldLaunchCamera: Boolean = false
) 