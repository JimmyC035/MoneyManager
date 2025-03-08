package com.example.moneymanager.presentation.screens.expense

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.util.ImageHelper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
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
    private val imageHelper: ImageHelper
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
                            // 解析發票內容並進入編輯模式
                            processRecognizedText(visionText)
                            // 使用公共方法進入圖片編輯模式
                            Log.d("AddExpenseViewModel", "準備進入圖片編輯模式")
                            
                            // 確保在主線程中更新 UI 狀態
                            viewModelScope.launch {
                                enterImageEditMode()
                                Log.d("AddExpenseViewModel", "圖片編輯模式狀態: ${_uiState.value.isInImageEditMode}")
                            }
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
    
    private fun processRecognizedText(visionText: Text) {
        // 將識別的文字轉換為可選擇的文字塊
        val textBlocks = visionText.textBlocks.flatMap { block ->
            block.lines.flatMap { line ->
                line.elements.map { element ->
                    TextBlock(
                        text = element.text,
                        boundingBox = element.boundingBox ?: Rect(),
                        isSelected = false,
                        selectionType = SelectionType.NONE
                    )
                }
            }
        }
        
        // 更新 UI 狀態
        _uiState.update { it.copy(
            textBlocks = textBlocks,
            fullRecognizedText = visionText.text
        ) }
    }
    
    // 進入圖片編輯模式 - 公共方法，可從 UI 層和內部調用
    fun enterImageEditMode() {
        Log.d("AddExpenseViewModel", "進入圖片編輯模式")
        
        // 重置選擇狀態
        val resetBlocks = _uiState.value.textBlocks.map { 
            it.copy(isSelected = false, selectionType = SelectionType.NONE) 
        }
        
        _uiState.update { it.copy(
            isInImageEditMode = true,
            textBlocks = resetBlocks
        ) }
        
        Log.d("AddExpenseViewModel", "圖片編輯模式已設置: ${_uiState.value.isInImageEditMode}")
        Log.d("AddExpenseViewModel", "文字塊數量: ${_uiState.value.textBlocks.size}")
    }
    
    // 退出圖片編輯模式
    fun exitImageEditMode() {
        Log.d("AddExpenseViewModel", "退出圖片編輯模式")
        _uiState.update { it.copy(
            isInImageEditMode = false
        ) }
        Log.d("AddExpenseViewModel", "圖片編輯模式已關閉: ${_uiState.value.isInImageEditMode}")
    }
    
    // 選擇文字塊
    fun selectTextBlock(index: Int, selectionType: SelectionType) {
        val blocks = _uiState.value.textBlocks.toMutableList()
        if (index >= 0 && index < blocks.size) {
            // 更新選擇狀態
            blocks[index] = blocks[index].copy(
                isSelected = true,
                selectionType = selectionType
            )
            
            // 根據選擇類型更新相應的字段
            val selectedText = blocks[index].text
            when (selectionType) {
                SelectionType.AMOUNT -> {
                    // 嘗試清理金額文字（移除非數字字符）
                    val cleanAmount = selectedText.replace(Regex("[^0-9.]"), "")
                    setAmount(cleanAmount)
                }
                SelectionType.NOTE -> {
                    // 將選擇的文字添加到備註中
                    val currentNote = _uiState.value.note
                    val newNote = if (currentNote.isBlank()) selectedText else "$currentNote $selectedText"
                    setNote(newNote)
                }
                else -> {}
            }
            
            _uiState.update { it.copy(textBlocks = blocks) }
        }
    }
    
    // 完成編輯
    fun finishEditing() {
        exitImageEditMode()
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
    
    fun saveExpense() {
        // TODO: 保存支出到資料庫
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
    val shouldLaunchCamera: Boolean = false,
    val isInImageEditMode: Boolean = false,
    val textBlocks: List<TextBlock> = emptyList()
)

data class TextBlock(
    val text: String,
    val boundingBox: Rect,
    val isSelected: Boolean = false,
    val selectionType: SelectionType = SelectionType.NONE
)

enum class SelectionType {
    AMOUNT, NOTE, NONE
} 