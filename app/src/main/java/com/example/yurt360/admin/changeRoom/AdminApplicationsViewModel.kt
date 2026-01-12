package com.example.yurt360.admin.changeRoom

import android.util.Log
import androidx.compose.runtime.getValue      // IMPORT REQUIRED
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf // IMPORT REQUIRED
import androidx.compose.runtime.setValue      // IMPORT REQUIRED
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.common.model.ApplicationForm
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.get

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

    fun swapStudentRooms() {
        viewModelScope.launch {
            try {
                // Ensure we have exactly 2 items
                if (selectedForMatching.size != 2) return@launch

                val app1 = selectedForMatching[0]
                val app2 = selectedForMatching[1]

                // 1. Get the User IDs (who are we swapping?)
                // Assuming your ApplicationForm model has a 'user_id' field.
                // If your model uses 'userId', change it below.
                val user1Id = app1.userId
                val user2Id = app2.userId

                // 2. Get the Room Info to SWAP
                // User 1 gets User 2's room
                val user1NewRoom = app2.profile?.roomNumber
                val user1NewLoc = app2.profile?.location

                // User 2 gets User 1's room
                val user2NewRoom = app1.profile?.roomNumber
                val user2NewLoc = app1.profile?.location

                if (user1Id != null && user2Id != null) {
                    // 3. Update User 1 in Database
                    SupabaseClient.client.from("users").update(
                        {
                            set("roomNo", user1NewRoom)
                            set("location", user1NewLoc)
                        }
                    ) {
                        filter { eq("id", user1Id) }
                    }

                    // 4. Update User 2 in Database
                    SupabaseClient.client.from("users").update(
                        {
                            set("roomNo", user2NewRoom)
                            set("location", user2NewLoc)
                        }
                    ) {
                        filter { eq("id", user2Id) }
                    }

                    // 5. Mark both applications as "Approved" (isApproved = true)
                    // This removes them from the pending list if your filter logic checks for null/false.
                    val appIds = listOf(app1.id, app2.id)
                    SupabaseClient.client.from("application_forms").update(
                        {
                            set("isApproved", true)
                        }
                    ) {
                        filter { isIn("id", appIds) }
                    }

                    // 6. Refresh the list and exit selection mode
                    fetchAllApplications()
                    toggleSelectionMode()
                }
            } catch (e: Exception) {
                Log.e("AdminVM", "Swap Error: ${e.message}")
            }
        }
    }
}
