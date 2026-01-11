package com.example.yurt360.admin.changeRoom

import android.util.Log
import androidx.compose.runtime.getValue      // IMPORT REQUIRED
import androidx.compose.runtime.mutableStateListOf
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

    // 1. Holds the list of data from Database
    private val _applications = MutableStateFlow<List<ApplicationForm>>(emptyList())
    val applications: StateFlow<List<ApplicationForm>> = _applications

    // 2. Holds the currently selected application for Detail View
    var selectedApplication by mutableStateOf<ApplicationForm?>(null)

    // 3. Selection Mode Variables (The new stuff)
    var isSelectionMode by mutableStateOf(false)
    var selectedForMatching = mutableStateListOf<ApplicationForm>()

    // --- CRITICAL: THIS BLOCK MUST EXIST TO LOAD DATA ---
    init {
        fetchAllApplications()
    }
    // ----------------------------------------------------

    fun fetchAllApplications() {
        viewModelScope.launch {
            try {
                // Ensure "studentNumber" matches your DB column name exactly!
                val result = SupabaseClient.client
                    .from("application_forms")
                    .select(columns = Columns.raw("*, users(name, surname, studentNumber, roomNo, location, email, phone)"))
                    .decodeList<ApplicationForm>()

                _applications.value = result
            } catch (e: Exception) {
                Log.e("AdminApplicationsVM", "Error: ${e.message}")
            }
        }
    }

    // Toggle logic for Selection Mode
    fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (!isSelectionMode) {
            selectedForMatching.clear()
        }
    }

    fun toggleSelection(app: ApplicationForm) {
        if (selectedForMatching.contains(app)) {
            selectedForMatching.remove(app)
        } else {
            if (selectedForMatching.size < 2) {
                selectedForMatching.add(app)
            }
        }
    }
}