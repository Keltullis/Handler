package com.bignerdranch.android.handler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.bignerdranch.android.handler.databinding.ActivityHandlerBinding
import kotlin.random.Random



// Здесь Handler сам обрабатывает действия

class HandlerLevel2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHandlerBinding

    // Handler с обработчиком сообщений
    private val handler = Handler(Looper.getMainLooper()) {
        Log.d(TAG, "Processing message: ${it.what}")
        //проверяем id сообщения и вызываем соответствующий метод
        when (it.what) {
            MSG_TOGGLE_BUTTON -> toggleTestButtonState()
            MSG_NEXT_RANDOM_COLOR -> nextRandomColor()
            MSG_SHOW_TOAST -> showToast()
        }
        return@Handler false
    }

    private val token = Any()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandlerBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.root.forEach {
            if (it is Button) it.setOnClickListener(universalButtonListener)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun toggleTestButtonState() {
        binding.testButton.isEnabled = !binding.testButton.isEnabled
    }

    private fun nextRandomColor() {
        val randomColor = -Random.nextInt(255 * 255 * 255)
        binding.colorView.setBackgroundColor(randomColor)
    }

    private fun showToast() {
        Toast.makeText(this, R.string.hello, Toast.LENGTH_SHORT).show()
    }

    private val universalButtonListener = View.OnClickListener {
        Thread {
            when (it.id) {
                // Формируем сообщение через obtainMessage()  и отправляем его
                R.id.enableDisableButton -> {
                    val message = handler.obtainMessage(MSG_TOGGLE_BUTTON)
                    handler.sendMessage(message)
                }
                // Другой способ создания сообщения
                R.id.randomColorButton -> {
                    val message = Message()
                    message.what = MSG_NEXT_RANDOM_COLOR
                    handler.sendMessage(message)
                }
                // Отправляем сообщение с задержкой
                R.id.enableDisableDelayedButton -> {
                    val message = Message.obtain(handler, MSG_TOGGLE_BUTTON)
                    handler.sendMessageDelayed(message, DELAY)
                }
                // Используем коллбэк(функцию используемую как аргумент другой функции),код обработчика событий не выполнится
                R.id.randomColorDelayedButton -> {
                    val message = Message.obtain(handler) {
                        Log.d(TAG, "Random color is called via CALLBACK")
                        nextRandomColor()
                    }
                    handler.sendMessageDelayed(message, DELAY)
                }
                // Токен записывается в obj(object)
                R.id.randomColorTokenDelayedButton -> {
                    val message = handler.obtainMessage(MSG_NEXT_RANDOM_COLOR)
                    message.obj = token
                    handler.sendMessageDelayed(message, DELAY)
                }
                R.id.showToastButton -> {
                    val message = handler.obtainMessage(MSG_SHOW_TOAST)
                    message.obj = token
                    handler.sendMessageDelayed(message, DELAY)
                }
                R.id.cancelButton -> handler.removeCallbacksAndMessages(token)

            }
        }.start()
    }

    companion object {
        @JvmStatic private val DELAY = 2000L // milliseconds
        @JvmStatic private val TAG = HandlerLevel2Activity::class.java.simpleName

        private const val MSG_TOGGLE_BUTTON = 1
        private const val MSG_NEXT_RANDOM_COLOR = 2
        private const val MSG_SHOW_TOAST = 3
    }
}