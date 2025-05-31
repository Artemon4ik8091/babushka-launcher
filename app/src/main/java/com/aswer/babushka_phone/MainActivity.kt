package com.aswer.babushka_phone

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val contacts: Button = findViewById(R.id.contacts)
        val alo: Button = findViewById(R.id.alo)
        val logo: TextView = findViewById(R.id.logo)
        val podval: TextView = findViewById(R.id.podval)
        val timeText: TextView = findViewById(R.id.date)
        val dateText: TextView = findViewById(R.id.time)
        val updateHandler = android.os.Handler(Looper.getMainLooper())
        lateinit var updateRunnable: Runnable

        fun getTimeBasedGreeting(): String {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            return when (hour) {
                in 5..10 -> "Доброе утро!"
                in 11..16 -> "Добрый день!"
                in 17..22 -> "Добрый вечер!"
                else -> "Спокойной ночи!"
            }
        }
        var greetingText = logo

        fun updateGreeting() {
            greetingText.text = getTimeBasedGreeting()
        }

        fun onResume() {
            super.onResume()
            updateGreeting() // Обновляем при возвращении в приложение
        }

        fun updateTime() {
            // Форматируем время в формате ЧЧ:ММ
            val currentTime = java.util.Date()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeText.text = timeFormat.format(currentTime)
        }

        fun updateDate() {
            // Форматируем дату в красивом виде
            val currentDate = java.util.Date()
            val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ru"))
            val formattedDate = dateFormat.format(currentDate)

            // Делаем первую букву заглавной
            dateText.text = formattedDate.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString()
            }
        }

        fun updateDateTime() {
            // Обновление приветствия
            onResume()
            updateGreeting()

            // Обновление времени
            updateTime()

            // Обновление даты
            updateDate()
        }

        fun calculateDelayToNextMinute(): Long {
            // Рассчитываем задержку до следующей минуты
            val calendar = Calendar.getInstance()
            val seconds = calendar.get(Calendar.SECOND)
            return (60 - seconds) * 1000L
        }

        fun setupAutoUpdate() {
            // Обновляем каждую минуту для точности
            updateRunnable = object : Runnable {
                override fun run() {
                    updateDateTime()
                    updateHandler.postDelayed(this, calculateDelayToNextMinute())
                }
            }
            updateHandler.post(updateRunnable)
        }

        // Первоначальное обновление
        updateDateTime()

        // Настройка автоматического обновления
        setupAutoUpdate()


        alo.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:")
                }
                startActivity(intent)
            }
            catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Ошибка: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        contacts.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = ContactsContract.Contacts.CONTENT_URI
                }
                startActivity(intent)
            }
            catch (e: Exception) {
                // Если ничего не сработало
                Toast.makeText(
                    this,
                    "Приложение контактов не найдено",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        logo.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
            catch (e: Exception) {
                // Если ничего не сработало
                Toast.makeText(
                    this,
                    "Приложение настроек не найдено",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        podval.setOnClickListener {
            Toast.makeText(
                this,
                "Нажмите на верхнюю надпись для открытия настроек устройства.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}