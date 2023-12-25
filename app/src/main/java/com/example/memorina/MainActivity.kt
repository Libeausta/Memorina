package com.example.memorina

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val pairsNum: Int = 8
    private val rowNum: Int = 4
    private var openCard: View? = null
    private val openCardDelay: Long = 800
    private var openPairs: Int = 0
    private lateinit var cards: ArrayList<ImageView>
    private val cardsResources = hashMapOf(
        "var1" to R.drawable.var1,
        "var2" to R.drawable.var2,
        "var3" to R.drawable.var3,
        "var4" to R.drawable.var4,
        "var5" to R.drawable.var5,
        "var6" to R.drawable.var6,
        "var7" to R.drawable.var1,
        "var8" to R.drawable.var2,
        "face" to R.drawable.face
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1.toFloat()
        val colorListener = View.OnClickListener() {
            when (openCard) {
                null -> {
                    if (it.isClickable) {
                        openCard(cards[it.id])
                        openCard = it
                    } else Log.d("cardtag", "two cards are open already")
                }
                else -> {
                    if (openCard?.id == it.id) {
                        faceCardDown(cards[it.id])
                        Log.d("cardtag", "Clicked on the same one card ${it.id} -- ${openCard?.id}"
                        )
                    } else {
                        openCards(it)
                        if (openPairs == pairsNum) {
                            Toast
                                .makeText(applicationContext, "You found all pairs!", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    openCard = null
                }
            }
        }

        cards = getCards(
            numberOfPairs = pairsNum,
            params = params,
            onClickListener = colorListener,
            shuffle = true
        )
        fillLayoutWithCards(layout, cards, rowNum)
        setContentView(layout)
    }

    private fun openCards(it: View) {
        val nonNullPreviousCard = openCard!!
        GlobalScope.launch(Dispatchers.Main) {
            delay(openCardDelay)
            openCard(cards[it.id])
            delay(openCardDelay)
            if (nonNullPreviousCard.tag == it.tag) {
                it.visibility = View.INVISIBLE
                it.isClickable = false
                nonNullPreviousCard.visibility = View.INVISIBLE
                nonNullPreviousCard.isClickable = false
                openPairs++
            } else {
                faceCardDown(cards[it.id])
                faceCardDown(cards[nonNullPreviousCard.id])
            }
        }
    }

    private fun fillLayoutWithCards(
        layout: LinearLayout,
        cards: ArrayList<ImageView>,
        rows: Int
    ) {
        val rowLayouts = Array(rows) { LinearLayout(applicationContext) }
        cards.forEachIndexed { idx, card -> rowLayouts[idx / rows].addView(card) }
        rowLayouts.forEach { layout.addView(it) }
    }

    private fun getCards(
        numberOfPairs: Int,
        params: LinearLayout.LayoutParams,
        onClickListener: View.OnClickListener,
        shuffle: Boolean = true
    ): ArrayList<ImageView> {
        val cards = ArrayList<ImageView>()
        val tags = cardsResources.keys.toList()
        for (pairNumber in 0 until numberOfPairs) {
            for (i in 0..1) {
                val card = ImageView(applicationContext)
                    .apply {
                        setImageResource(cardsResources[tags[pairNumber]]!!)
                        layoutParams = params
                        setOnClickListener(onClickListener)
                        tag = tags[pairNumber]
                    }

                cards.add(card)
                faceCardDown(card)
            }
        }
        cards.shuffle()
        cards.forEachIndexed { index, it -> it.id = index }
        return cards
    }

    private fun faceCardDown(card: ImageView) {
        card.setImageResource(cardsResources["face"]!!)
    }

    private fun openCard(card: ImageView) {
        card.setImageResource(cardsResources[card.tag]!!)
    }
}