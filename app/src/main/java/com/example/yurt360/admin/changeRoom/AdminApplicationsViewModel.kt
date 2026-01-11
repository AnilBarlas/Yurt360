package com.example.yurt360.admin.changeRoom

import android.util.Log
import androidx.compose.runtime.getValue      // IMPORT REQUIRED
import androidx.compose.runtime.mutableStateOf // IMPORT REQUIRED
import androidx.compose.runtime.setValue      // IMPORT REQUIRED
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.model.ApplicationForm
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminApplicationsViewModel : ViewModel() {

    private val _applications = MutableStateFlow<List<ApplicationForm>>(emptyList())
    val applications: StateFlow<List<ApplicationForm>> = _applications
    var selectedApplication by mutableStateOf<ApplicationForm?>(null)

    init {
        fetchAllApplications()
    }

    fun fetchAllApplications() {
        viewModelScope.launch {
            try {
                val result = SupabaseClient.client
                    .from("application_forms")
                    .select(columns = Columns.raw("*, users(name, surname, studenNumber, roomNo, location)"))
                    .decodeList<ApplicationForm>()

                _applications.value = result
            } catch (e: Exception) {
                Log.e("AdminApplicationsVM", "Error: ${e.message}")
            }
        }
    }
}