package com.example.yurt360.user.refectory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Menu
import com.example.yurt360.data.api.SupabaseClient // SupabaseClient importunu kendi paketine göre düzelt
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {

    // Menü listesi
    private val _menuList = MutableStateFlow<List<Menu>>(emptyList())
    val menuList = _menuList.asStateFlow()

    // Ekranda o an gösterilen seçili menü
    private val _selectedMenu = MutableStateFlow<Menu?>(null)
    val selectedMenu = _selectedMenu.asStateFlow()

    // Yükleniyor durumu
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchMenus()
    }

    private fun fetchMenus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                val menus = SupabaseClient.client
                    .from("menus")
                    .select()
                    .decodeList<Menu>()
                    .sortedBy { it.id }

                _menuList.value = menus


                if (menus.isNotEmpty()) {
                    _selectedMenu.value = menus[0]
                }
            } catch (e: Exception) {
                println("Veri çekme hatası: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectMenu(menu: Menu) {
        _selectedMenu.value = menu
    }
}