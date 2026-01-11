package com.example.yurt360.user.changeRoom

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.ApplicationForm
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationsViewModel : ViewModel() {

    // Holds the list of all applications
    private val _applications = MutableStateFlow<List<ApplicationForm>>(emptyList())
    val applications = _applications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchUserApplications()
    }

    fun fetchUserApplications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()

                if (currentUser != null) {
                    val result = SupabaseClient.client
                        .from("application_forms") // Supabase table name
                        .select {
                            filter {
                                eq("user_id", currentUser.id)
                            }
                        }
                        .decodeList<ApplicationForm>()

                    _applications.value = result
                }
            } catch (e: Exception) {
                Log.e("ApplicationsVM", "Error fetching applications: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}