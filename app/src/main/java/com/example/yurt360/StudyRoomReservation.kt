package com.example.yurt360

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yurt360.R
import java.util.*

data class Table(val id: Int, val name: String, var isBooked: Boolean = false)

class StudyRoomReservation : AppCompatActivity() {

    private lateinit var tableList: MutableList<Table>
    private lateinit var adapter: TableAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Masa listesini oluştur
        tableList = mutableListOf()
        for (i in 1..12) {
            tableList.add(Table(i, "Masa $i"))
        }

        // 2) RecyclerView & adapter kurulumu
        val rv = findViewById<RecyclerView>(R.id.recyclerTables)  // XML'deki id ile eşleşmeli
        rv.layoutManager = GridLayoutManager(this, 4)  // 4 sütun → 4x3 = 12 masa
        adapter = TableAdapter(tableList, ::onTableClicked)
        rv.adapter = adapter

        // 3) Başvuru / Rezervasyon butonu
        val btn = findViewById<Button>(R.id.recyclerTables)  // XML’de tanımlıysa
        btn.setOnClickListener {
            showDatePicker()
        }
    }

    private fun onTableClicked(table: Table) {
        // Masa'ya tıklandı —  burada seçili masa bilgisi saklanabilir
        Toast.makeText(this, "${table.name} seçildi", Toast.LENGTH_SHORT).show()
        // Örneğin: seçili masa = table → sonra rezervasyon yaparken kullanılır
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this,
            { _, y, m, d ->
                val dateStr = "${d}/${m+1}/$y"
                Toast.makeText(this, "Seçilen tarih: $dateStr", Toast.LENGTH_SHORT).show()
                // Burada: seçili masa + tarih ile rezervasyonu kaydet
                // Örneğin: bookTable(selectedTable, dateStr)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }
}
