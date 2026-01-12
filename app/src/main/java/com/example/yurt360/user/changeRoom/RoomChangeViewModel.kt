package com.example.yurt360.user.changeRoom

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.common.model.ApplicationForm
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.yurt360.common.model.ApplicationType
class RoomChangeViewModel : ViewModel() {
    private val _submissionStatus = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionStatus = _submissionStatus.asStateFlow()

    fun submitForm(reason: String, formType: String) {
        viewModelScope.launch {
            _submissionStatus.value = SubmissionState.Loading
            try {
                // Get current user ID safely
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()

                if (currentUser == null) {
                    _submissionStatus.value = SubmissionState.Error("Kullanıcı girişi yapılmamış.")
                    return@launch
                }

                val form = ApplicationForm(
                    userId = currentUser.id,
                    type = formType, // Use the passed parameter here
                    message = reason,
                    isApproved = false
                )

                SupabaseClient.client.from("application_forms").insert(form)
                _submissionStatus.value = SubmissionState.Success

            } catch (e: Exception) {
                Log.e("Supabase", "Error: ${e.message}")
                _submissionStatus.value = SubmissionState.Error("Hata: ${e.message}")
            }
        }
    }

    fun resetState() {
        _submissionStatus.value = SubmissionState.Idle
    }
}

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Loading : SubmissionState()
    object Success : SubmissionState()
    data class Error(val message: String) : SubmissionState()
}
