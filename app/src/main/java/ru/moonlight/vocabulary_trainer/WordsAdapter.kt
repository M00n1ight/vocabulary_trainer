package ru.moonlight.vocabulary_trainer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import ru.moonlight.vocabulary_trainer.database.WordData

class WordsAdapter(var items: List<WordData>, val callback: Callback): RecyclerView.Adapter<WordsAdapter.WordsHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): WordsHolder {
        return WordsHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false))
    }

    inner class WordsHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val currentWord = itemView.findViewById<TextView>(R.id.word)
        private val currentTranslate = itemView.findViewById<TextView>(R.id.translate)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.delete_word)

        fun bind(item: WordData) {
            currentWord.text = item.word
            currentTranslate.text = item.translation
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            deleteButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onDeleteButtonClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: WordData)
        fun onDeleteButtonClicked(item: WordData)
    }

}