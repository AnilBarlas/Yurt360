package com.example.yurt360.user.refectory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Menu
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AdminMenuViewModel : ViewModel() {

    private val _menuList = MutableStateFlow<List<Menu>>(emptyList())
    val menuList: StateFlow<List<Menu>> = _menuList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchMenus()
    }

    fun fetchMenus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Verileri çek ve tarihe göre sırala
                val result = SupabaseClient.client
                    .from("menus")
                    .select()
                    .decodeList<Menu>()

                _menuList.value = result.sortedBy { it.date }
            } catch (e: Exception) {
                println("Hata (Fetch): ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- EKLEME İŞLEMİ DTO ---
    @Serializable
    data class MenuInsertDto(
        @SerialName("tarih")
        val date: String,

        @SerialName("yemekler")
        val foods: String
    )

    fun addMenu(date: String, foods: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newMenu = MenuInsertDto(date = date, foods = foods)
                SupabaseClient.client.from("menus").insert(newMenu)
                fetchMenus()
                onSuccess()
            } catch (e: Exception) {
                println("Hata (Add): ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- SİLME İŞLEMİ (Düzeltildi) ---
    fun deleteMenu(menuId: Long) {
        viewModelScope.launch {
            try {
                // DÜZELTME: delete bloğu doğrudan filtre parametrelerini kabul eder.
                // Eğer 'eq' hala kırmızı yanarsa: Build > Clean Project yap.
                SupabaseClient.client.from("menus").delete {
                    filter {
                        eq("id", menuId)
                    }
                }
                fetchMenus()
            } catch (e: Exception) {
                println("Hata (Delete): ${e.message}")
            }
        }
    }
}