package com.h.basketball

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment(R.layout.fragment_game) {

    private val gameViewModel: ViewModel by viewModels()
    private var right = true

    interface GameListener {
        fun toBest()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true


        view.setOnTouchListener { v, event ->
            //if (!throwing) {
            val x = event!!.x.toInt()
            val y = event.y.toInt()


            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ball_grey.x = -100f
                    ball_grey.y = -100f
                    gameViewModel.provideThrow()
                }
                MotionEvent.ACTION_DOWN -> {
                    if (y > ball.y) {
                        gameViewModel.setBasketPosition(basket.x, basket.y, basket.height)
                        gameViewModel.setBasketWhitePosition(
                            basket_white.x,
                            basket_white.y,
                            basket_white.height,
                            basket_white.width
                        )
                        gameViewModel.setPosition(
                            ball.x - ball.width / 2,
                            ball.y - ball.height / 2,
                            ball.height
                        )
                        val difx = ball.x - (ball_grey.x) //- ballSize / 4)
                        val dify = ball.y - (ball_grey.y)
                        gameViewModel.setPowerAndDirection(
                            ((dify / 10).toInt()).toFloat(),
                            ((difx / 10).toInt()).toFloat()
                        )
                    }
                }

                MotionEvent.ACTION_MOVE -> {

                    if (right) {
                        if (x < basket_white.x) {
                            gameViewModel.setPosition(
                                x.toFloat() - ball.width / 2,
                                y.toFloat() - ball.height / 2,
                                ball.height
                            )
                            val difx = ball.x - (ball_grey.x) //- ballSize / 4)
                            val dify = ball.y - (ball_grey.y)
                            gameViewModel.setPowerAndDirection(
                                ((dify / 6).toInt()).toFloat(),
                                ((difx / 10).toInt()).toFloat()
                            )
                            Log.w("Points", "X = ${ball.x}")
                            //Log.w("Points", "Y = $yPower")
                        }
                    } else {
                        if (x > basket_white.x + basket_white.width) {
                            gameViewModel.setPosition(
                                x.toFloat() - ball.width / 2,
                                y.toFloat() - ball.height / 2,
                                ball.height
                            )
                            val difx = ball.x - (ball_grey.x) //- ballSize / 4)
                            val dify = ball.y - (ball_grey.y)
                            gameViewModel.setPowerAndDirection(
                                ((dify / 6).toInt()).toFloat(),
                                ((difx / 10).toInt()).toFloat()
                            )
                            Log.w("Points", "X = ${ball.x}")
                            //Log.w("Points", "Y = $yPower")
                        }
                    }

                }
            }
            true
        }

        // Screen Size
        val windowManager = activity?.windowManager
        val display = windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        gameViewModel.setScreenSize(size.x, size.y)

        gameViewModel.getBallState().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            ball.x = it.first
            ball.y = it.second
        })

        gameViewModel.getGreyBallState().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            ball_grey.x = it.first
            ball_grey.y = it.second
        })
        gameViewModel.getScore().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            enemy_score.text = it.first.toString()
            your_score.text = it.second.toString()

        })
        gameViewModel.getBasketCoordinates()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                basket.y = it
                basket_white.y = it + 3 * basket.height / 4.toFloat()
                gameViewModel.setBasketPosition(basket.x, basket.y, basket.height)
            })


        gameViewModel.showBestScore.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            val bestScore = Prefs.read(Prefs.BEST_SCORE, "0")?.toInt() ?: 0
            val curPoint = your_score.text.toString().toInt()
            if (curPoint > bestScore) {
                Prefs.write(Prefs.BEST_SCORE, curPoint.toString())
            }
            Prefs.write(Prefs.CURRENT_SCORE, curPoint.toString())
            (activity as GameListener).toBest()
        })
        gameViewModel.getSide().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!=null){
                right = it
                if (it == true) {
                    basket.x = gameViewModel.screenWidth.toFloat()-basket.width
                    basket_white.x = gameViewModel.screenWidth.toFloat()-basket_white.width
                } else {
                    basket.x = 0.toFloat()
                    basket_white.x = 0.toFloat()
                }
            }

        })


    }
}