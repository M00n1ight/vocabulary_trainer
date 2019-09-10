package ru.moonlight.vocabulary_trainer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.background_menu_layout.*
import kotlinx.android.synthetic.main.main_activity.*

const val SHARED_PREFERENCES_FILE_NAME = "ru.moonlight.vocabulary_trainer"

class MainFragment  : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val dictionaryFragment = DictionaryFragment()
        val trainingFragment = TrainingFragment()
        val wordOfTheDayFragment = WordOfTheDayFragment().apply {
            updateWord(applicationContext, getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE))
        }

        replaceFragment(dictionaryFragment)

        dictionary.setOnClickListener {
            replaceFragment(dictionaryFragment)
            container.close()
        }

        wordOfTheDay.setOnClickListener {
            replaceFragment(wordOfTheDayFragment)
            container.close()
        }

        training.setOnClickListener {
            replaceFragment(trainingFragment)
            container.close()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.includedFront, fragment)
            commit()
        }
    }
}

// TODO: режим тренировки
// TODO: "слово дня"
// TODO: настройка режима тренировки (несколько вариантов режима тренировки)