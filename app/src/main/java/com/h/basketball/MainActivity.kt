package com.h.basketball

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(), MenuFragment.MenuListener, GameFragment.GameListener,
    BestScoreFragment.BestListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (savedInstanceState == null) {
            showFragmentMenu()
        }


    }

    private fun showFragmentMenu() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = MenuFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    override fun gameClicked() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = GameFragment()
        transaction.setCustomAnimations(
            R.anim.stack_left_in,
            R.anim.stack_right_out,
            R.anim.stack_left_in,
            R.anim.stack_right_out
        )
        transaction.replace(R.id.fragment_holder, fragment).addToBackStack(null)
        transaction.commit()
    }

    override fun toBest() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = BestScoreFragment()
        transaction.setCustomAnimations(
            R.anim.stack_left_in,
            R.anim.stack_right_out,
            R.anim.stack_left_in,
            R.anim.stack_right_out
        )
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    override fun toMenu() {
        val fm: FragmentManager = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        gameClicked()
    }
}