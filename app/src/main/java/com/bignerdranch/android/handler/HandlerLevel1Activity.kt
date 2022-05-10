package com.bignerdranch.android.handler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.bignerdranch.android.handler.databinding.ActivityHandlerBinding
import kotlin.random.Random


//Хэндлер привязывается к луперу и всё что пошлют в хэндлер будет обработано в том потом к которому привязан хэндлер

class HandlerLevel1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityHandlerBinding

    //создаём хэндлер привязанный к главному потоку(в котором будет обрабатываться интерфейс)
    private val handler = Handler(Looper.getMainLooper())

    //Это токен,он нужен для отмены действий отправленных в хэндлер
    private val token = Any()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandlerBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        //Проходимся по всем кнопкам и назначаем им один и тот же лисенер
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

    // весь этот код выполняется в другом потоке
    // метод пост позволяет выполнить код который передали в потоке к которому привязан хэндлер
    // метод постДелэй делает тоже самое что и пост,но через сколько-то мили секунд
    // токен(любой объект типа Any(любого типа)) группирует действия
    // removeCallbacksAndMessages по токену отменяет действия
    private val universalButtonListener = View.OnClickListener {
        Thread {
            when (it.id) {

                R.id.enableDisableButton ->
                    handler.post { toggleTestButtonState() }
                R.id.randomColorButton ->
                    handler.post { nextRandomColor() }

                R.id.enableDisableDelayedButton ->
                    handler.postDelayed({ toggleTestButtonState()}, DELAY)
                R.id.randomColorDelayedButton ->
                    handler.postDelayed({ nextRandomColor() }, DELAY)

                R.id.randomColorTokenDelayedButton ->
                    handler.postDelayed({ toggleTestButtonState()}, token, DELAY)
                R.id.showToastButton ->
                    handler.postDelayed({ showToast() }, token, DELAY)
                R.id.cancelButton -> handler.removeCallbacksAndMessages(token)

            }
        }.start()

    }

    companion object {
        @JvmStatic private val DELAY = 2000L // milliseconds
    }
}