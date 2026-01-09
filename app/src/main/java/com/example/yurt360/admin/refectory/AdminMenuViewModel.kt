package com.example.yurt360.user.refectory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Menu
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                val result = SupabaseClient.client.from("menus").select().decodeList<Menu>()
                _menuList.value = result.sortedBy { it.date }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // YENİ EKLENDİ: Insert yaparken ID göndermiyoruz, DB otomatik atıyor.
    @kotlinx.serialization.Serializable
    data class MenuInsertDto(
        val date: String,
        val foods: String
    )

    fun addMenu(date: String, foods: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // ID üretmiyoruz, veritabanı (auto-increment) halledecek
                val newMenu = MenuInsertDto(date = date, foods = foods)

                SupabaseClient.client.from("menus").insert(newMenu)

                fetchMenus()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // GÜNCELLENDİ: String yerine Long alıyor
    fun deleteMenu(menuId: Long) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("menus").delete {
                    filter {
                        eq("id", menuId) // Artık Long gönderiyoruz
                    }
                }
                fetchMenus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}