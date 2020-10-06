package com.h.basketball

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class ViewModel(app: Application): AndroidViewModel(app) {

    val showBestScore: Event<Unit> = Event()
    private val handler = Handler()
    private var timer = Timer()
    private var throwing = false
    private var ballX = 0f
    private var ballY = 0f
    private var ballGreyX = 0f
    private var ballGreyY = 0f
    val ball = MutableLiveData<Pair<Float, Float>>()
    fun getBallState(): LiveData<Pair<Float, Float>> = ball
    val greyBall = MutableLiveData<Pair<Float, Float>>()
    fun getGreyBallState(): LiveData<Pair<Float, Float>> = greyBall

    val score = MutableLiveData<Pair<Int, Int>>()
    fun getScore(): LiveData<Pair<Int, Int>> = score

    val basketCoordinates = MutableLiveData<Float>()
    fun getBasketCoordinates(): LiveData<Float> = basketCoordinates

    val rightSide = MutableLiveData<Boolean>()
    fun getSide(): LiveData<Boolean> = rightSide

    private var ballHeight = 0
    private var g = 0f
    var screenWidth = 0
    var screenHeight = 0
    private var xDirection = 0f
    private var yPower = 0f
    private var ballSize = 0
    private var basketX = 0f
    private var basketY = 0f
    private var basketHeight = 0
    private var basketWhiteX = 0f
    private var basketWhiteY = 0f
    private var basketWhiteHeight = 0
    private var basketWhiteWidth = 0
    private var goal = false
    private var yourScore = 0
    private var enemyScore = 0
    private var rebound = false
    private var basketPosition = 0f

    fun provideThrow() {
        if (!throwing) {
            throwing = true
            timer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Runnable { changePos() })
                }
            }, 0, 20)
        }
    }


    private fun changePos() {
        g += 0.98f
        ballY -= (yPower - g)
        ballX -= xDirection
        hitCheck()
        //pinkX += 4
        if (ballY < -200 || ballX < -200 || ballY > screenHeight || ballX > screenWidth) {
            timer.cancel()
            timer = Timer()
            rightSide.value = rightSide.value?.not()
            basketPosition = generateBasketCoordinats()
            basketCoordinates.value = basketPosition
            val newPositions = generateBallPosition()
            ballX = newPositions.first
            ballY = newPositions.second
            ballGreyX = ballX + ballSize / 2
            ballGreyY = ballY + ballSize / 2

            //generateBasketCoordinats()
            //pinkY = generateBallYCoordinats()

            //view.setBallGreyView(ballX, ballY)
            g = 0f
            rebound = false
            if (!goal) {
                enemyScore++


                if (enemyScore == 3) {
                    //yourScore = 0
                    //enemyScore = 0
                    showBestScore.value = Unit
                    onCleared()
                }
                score.value = Pair(enemyScore, yourScore)
            }
            goal = false
            throwing = false
            greyBall.value = Pair(ballGreyX, ballGreyY)


        }
        ball.value = Pair(ballX, ballY)
    }


    fun setPosition(x: Float, y: Float, height: Int) {
        if (!throwing) {
            Log.w("Printss", "ball  $x $y $height")
            ballX = x
            ballY = y
            ballHeight = height
            ball.value = Pair(ballX, ballY)
        }

    }

    fun setBasketPosition(x: Float, y: Float, height: Int) {
        if (!throwing) {
            Log.w("Printss", "$x $y $height")
            basketX = x
            basketY = y
            basketHeight = height
            if (rightSide.value == null) {
                rightSide.value = true
            }
        }
    }

    fun setBasketWhitePosition(x: Float, y: Float, height: Int, width: Int) {
        if (!throwing) {
            basketWhiteX = x
            basketWhiteY = y
            basketWhiteHeight = height
            basketWhiteWidth = width
        }
    }

    fun setScreenSize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        ballSize = 64 * (screenWidth / 411.42f).toInt()
        ballX = (screenWidth / 4).toFloat() //- ballSize / 2
        ballY = (7 * screenHeight / 10).toFloat()
        ball.value = Pair(ballX, ballY)
        ballGreyX = (screenWidth / 4).toFloat() + ballSize / 2
        ballGreyY = (7 * screenHeight / 10).toFloat() + ballSize / 2
        greyBall.value = Pair(ballGreyX, ballGreyY)

    }

    fun setPowerAndDirection(power: Float, direction: Float) {
        if (!throwing) {
            xDirection = direction
            yPower = power
        }
    }

    private fun hitCheck() {
        if (rightSide.value == true) {
            if (!rebound) {
                //щит кольца низ
                if (ballX + 10 * ballHeight / 8 >= screenWidth && (ballY < basketY + 3 * basketHeight / 4) && ballY > basketY) {
                    g = 0f
                    xDirection -= 1f * xDirection
                    yPower -= 1.5f * yPower
                    rebound = true
                }
                //щит кольца верх
                if (ballX + 10 * ballHeight / 8 >= screenWidth && (ballY < basketY) && ballY > basketY - basketHeight / 4) {
                    g = 0f
                    xDirection -= 2.5f * xDirection
                    yPower -= 0.5f * yPower
                    rebound = true
                }
                //кольцо снизу
                if ((yPower - g) > 0 && ballY <= basketWhiteY + basketWhiteHeight / 8 && ballY > basketWhiteY - basketWhiteHeight / 10 && ballX + ballHeight / 2 >= basketWhiteX) {
                    g = 0f
                    xDirection -= 0.5f * xDirection
                    yPower -= 1.5f * yPower
                    rebound = true
                }
                //Перядняя душка
                if ((ballY <= basketWhiteY && ballY > basketWhiteY - basketWhiteHeight / 2) && (ballX + 3 * ballHeight / 4 > basketWhiteX && ballX + ballHeight < basketWhiteX + basketWhiteWidth / 4)) {
                    xDirection -= 2.5f * xDirection
                    yPower -= 1.5f * yPower
                    g = 0f
                    rebound = true
                }
                //Перядняя душка зад мяча
                if ((ballY <= basketWhiteY && ballY > basketWhiteY - basketWhiteHeight / 2) && (ballX + ballHeight / 2 > basketWhiteX && ballX + ballHeight / 2 < basketWhiteX + basketWhiteWidth / 4)) {
                    xDirection -= 1.2f * xDirection
                    yPower += 0.01f * yPower
                    g = 0f
                    rebound = true
                }
            }
            //Goal
            if ((yPower - g) < 0 && ballX > basketWhiteX && ballY < basketWhiteY - basketWhiteHeight / 4 && ballY > basketWhiteY - basketWhiteHeight / 2) {
                if (!goal) {
                    goal = true
                    yourScore++
                    score.value = Pair(enemyScore, yourScore)
                }


            }
        } else {
            if (!rebound) {
                //щит кольца низ
                if (ballX - ballSize / 4 <= 0 && (ballY < basketY + 3 * basketHeight / 4) && ballY > basketY) {
                    g = 0f
                    xDirection -= 1f * xDirection
                    yPower -= 1.5f * yPower
                    rebound = true
                }

                //щит кольца верх
                if (ballX - ballSize / 4 <= 0 && (ballY < basketY) && ballY > basketY - basketHeight / 4) {
                    g = 0f
                    xDirection -= 2.5f * xDirection
                    yPower -= 0.5f * yPower
                    rebound = true
                }
                //кольцо снизу
                if ((yPower - g) > 0 && ballY <= basketWhiteY + basketWhiteHeight / 8 && ballY > basketWhiteY - basketWhiteHeight / 10 && ballX + ballSize / 2 <= basketWhiteX + basketWhiteHeight) {
                    g = 0f
                    xDirection -= 0.5f * xDirection
                    yPower -= 1.5f * yPower
                    rebound = true
                }

                //Перядняя душка
                if ((ballY <= basketWhiteY && ballY > basketWhiteY - basketWhiteHeight / 2) && (ballX < basketWhiteX + basketWhiteHeight && ballX > basketWhiteX + 2 * basketWhiteWidth / 4)) {
                    xDirection -= 2.5f * xDirection
                    yPower -= 1.5f * yPower
                    g = 0f
                    rebound = true
                }
                //Перядняя душка зад мяча
                if ((ballY <= basketWhiteY && ballY > basketWhiteY - basketWhiteHeight / 2) && (ballX < basketWhiteX + basketWhiteHeight && ballX > basketWhiteX + 2 * basketWhiteWidth / 4) && (yPower - g) < 0) {
                    xDirection -= 1.2f * xDirection
                    yPower += 0.01f * yPower
                    g = 0f
                    rebound = true
                }
            }

            //Goal
            if ((yPower - g) < 0 && ballX + ballSize / 2 < basketWhiteX + basketWhiteHeight && ballY < basketWhiteY - basketWhiteHeight / 4 && ballY > basketWhiteY - basketWhiteHeight / 2) {
                if (!goal) {
                    goal = true
                    yourScore++
                    score.value = Pair(enemyScore, yourScore)
                }


            }

        }


    }


    private fun generateBallPosition(): Pair<Float, Float> {
        val rndsY = ((basketY + 3*basketHeight/4).toInt()..7 * screenHeight / 10).random().toFloat()
        val rndsX = if (rightSide.value == true) {
            (2 * screenWidth / 10..4 * screenWidth / 10).random().toFloat()

        } else {
            (3 * screenWidth / 10..45 * screenWidth / 100).random().toFloat()
        }

        return Pair(rndsX, rndsY)

    }

    private fun generateBasketCoordinats(): Float {
        val rnds = (screenHeight / 10..4 * screenHeight / 10).random()
        return rnds.toFloat()
    }
}