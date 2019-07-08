package ru.moonlight.vocabulary_trainer

import android.app.Activity
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import kotlinx.android.synthetic.main.dictionary_layout.*
import ru.moonlight.vocabulary_trainer.database.VocabularyDB
import ru.moonlight.vocabulary_trainer.database.WordData
import ru.moonlight.vocabulary_trainer.database.WordDataDao
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

const val testHost = "8.8.8.8"
const val testPost = 53
const val timeLimit = 2500

class MainActivity : Activity() {

    lateinit var recyclerViewAdapter: WordsAdapter
    lateinit var wordDataDao: WordDataDao
    lateinit var wordsList: MutableList<WordData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onStart() {
        super.onStart()

        wordDataDao = VocabularyDB.getInstance(applicationContext)!!.WordDataDao()
        wordsList = wordDataDao.getAll()

        addWord.setOnClickListener {
            newWordDialog()
        }

        recyclerViewAdapter = WordsAdapter(wordsList, object : WordsAdapter.Callback {
            override fun onItemClicked(item: WordData) {
                changeWordDialog(item)
            }

            override fun onDeleteButtonClicked(item: WordData) {
                wordDataDao.deleteWord(item)
                wordsList.remove(item)
                recyclerViewAdapter.notifyDataSetChanged()
            }

        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            this.adapter = recyclerViewAdapter
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerViewAdapter.itemCount < 8)
                    addWord.show()
                else {
                    if (dy > 0 && addWord.visibility == View.VISIBLE) {
                        addWord.hide()
                    } else if (dy < 0 && addWord.visibility != View.VISIBLE)
                        addWord.show()
                }
            }
        })
    }

    private fun changeWordDialog(item: WordData) {
        lateinit var dialog: AlertDialog
        val dialogBuilder = AlertDialog.Builder(this)
        val changeWordDialogView = this.layoutInflater.inflate(R.layout.word_change_dialog, null)

        dialogBuilder.setView(changeWordDialogView)
        dialogBuilder.setTitle(getString(R.string.change_word))

        dialogBuilder.setCancelable(true)
        dialogBuilder.setOnCancelListener { it.cancel() }

        val etWord = changeWordDialogView.findViewById<TextInputLayout>(R.id.changeWord)
        etWord.editText?.setText(item.word)

        val etTranslation = changeWordDialogView.findViewById<TextInputLayout>(R.id.changeTranslation)
        etTranslation.editText?.setText(item.translation)

        val cancelButton = changeWordDialogView.findViewById<Button>(R.id.cancelChanges)
        cancelButton.setOnClickListener { dialog.cancel() }

        val confirmButton = changeWordDialogView.findViewById<Button>(R.id.confirmChanges)
        confirmButton.setOnClickListener {
            val word = etWord.editText?.text.toString()
            val translation = etTranslation.editText?.text.toString()

            if (!word.isEmpty() && !translation.isEmpty()) {
                item.word = word
                item.translation = translation

                wordDataDao.updateWord(item)
                recyclerViewAdapter.notifyDataSetChanged()

                etWord.editText?.text?.clear()
                etWord.error = null
                etTranslation.editText?.text?.clear()
                etTranslation.error = null
                dialog.cancel()
            } else {
                if (word.isEmpty())
                    etWord.error = getString(R.string.need_to_enter_a_word)
                else
                    etWord.error = null

                if (translation.isEmpty())
                    etTranslation.error = getString(R.string.need_to_enter_a_translation)
                else
                    etTranslation.error = null
            }
        }

        dialog = dialogBuilder.create().apply {
            show()
        }

    }

    private fun newWordDialog() {
        lateinit var dialog: AlertDialog
        val dialogBuilder = AlertDialog.Builder(this)
        val addWordDialogView = this.layoutInflater.inflate(R.layout.new_word_dialog, null)

        dialogBuilder.setView(addWordDialogView)
        dialogBuilder.setTitle(R.string.add_word)

        dialogBuilder.setCancelable(true)
        dialogBuilder.setOnCancelListener { it.cancel() }

        val etWord = addWordDialogView.findViewById<TextInputLayout>(R.id.newWord)
        val etTranslation = addWordDialogView.findViewById<TextInputLayout>(R.id.newTranslation)
        etTranslation.isEnabled = !isOnline()
        val autoTranslation = addWordDialogView.findViewById<CheckBox>(R.id.autoTranslate)
        autoTranslation.isChecked = !etTranslation.isEnabled

        autoTranslation.setOnCheckedChangeListener { buttonView, isChecked ->
            autoTranslation.isChecked = isChecked
            etTranslation.isEnabled = !isChecked
            if (!isOnline()) {
                Toast.makeText(
                    this,
                    "Автоматический перевод не доступен. Отсутствует соединение с интернетом.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val addNewWord = addWordDialogView.findViewById<Button>(R.id.addNewWord)
        addNewWord.setOnClickListener {
            val newWord = etWord.editText?.text.toString()
            var newTranslate: String? = null
            if (!autoTranslation.isChecked)
                newTranslate = etTranslation.editText?.text.toString()
            else {
                if (!newWord.isEmpty()) {
                    newTranslate = Translator.translate(newWord, "en-ru")
                }
                if (newTranslate == null) {
                    Toast.makeText(this, "Автоматический перевод недоступен", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (!newWord.isEmpty() && !newTranslate.isEmpty()) {
                val item = WordData(null, newWord, newTranslate)
                wordsList.add(0, item)
                wordDataDao.insert(item)
                recyclerViewAdapter.notifyDataSetChanged()

                etWord.editText?.text?.clear()
                etWord.error = null
                etTranslation.editText?.text?.clear()
                etTranslation.error = null
                dialog.cancel()
            } else {
                if (newWord.isEmpty())
                    etWord.error = getString(R.string.need_to_enter_a_word)
                else
                    etWord.error = null

                if (newTranslate.isEmpty())
                    etTranslation.error = getString(R.string.need_to_enter_a_translation)
                else
                    etTranslation.error = null
            }

        }

        dialog = dialogBuilder.create().apply {
            show()
        }
    }

    private fun isOnline(): Boolean {
        var connection = false
        val thread = Thread(Runnable {
            connection = try {
                val socket = Socket()
                socket.connect(InetSocketAddress(testHost, testPost), timeLimit)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        })
        thread.start()
        thread.join()
        return connection
    }
}

// TODO: режим тренировки
// TODO: "слово дня"
// TODO: настройка режима тренировки (несколько вариантов режима тренировки)