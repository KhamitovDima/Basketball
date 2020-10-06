package com.h.basketball

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_best_points.*


class BestScoreFragment : Fragment(R.layout.fragment_best_points) {

    interface BestListener {
        fun toMenu()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bestScorePoints = "Best Score ${Prefs.read(Prefs.BEST_SCORE, "0")}"
        val currentScorePoints = "Current Score ${Prefs.read(Prefs.CURRENT_SCORE, "0")}"
        bestScore.text = bestScorePoints
        currentScore.text = currentScorePoints

        toMenu.setOnClickListener {
            (activity as BestListener).toMenu()
        }
    }
}