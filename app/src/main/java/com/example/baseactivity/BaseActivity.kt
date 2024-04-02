package com.example.baseactivity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding


abstract class BaseActivity <B: ViewBinding>(val bindingFactory: (LayoutInflater) -> B) :
    AppCompatActivity() {


    val binding: B by lazy { bindingFactory(layoutInflater) }

    val mySharedPre : MyShared by lazy { MyShared(this) }

    open fun binding() {
        setContentView(binding.root)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding()

//        setStatusBarTransparent(this)
        changeColorStatusBar()
    }


    open fun openActivity(destination: Class<*>, canBack: Boolean = true, bundle: Bundle? = null) {
        val intent = Intent(this, destination)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.enter_from_right, R.anim.exit_to_left
        )
        if (!canBack) {
            finish()
        }
    }

     open fun closeActivity() {
        finish()
        this.overridePendingTransition(
            R.anim.enter_from_left, R.anim.exit_to_right
        )
    }

    open fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun changeColorStatusBar() {
        val window: Window = window
        val decorView = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        wic.isAppearanceLightStatusBars = true
        // And then you can set any background color to the status bar.
        window.statusBarColor = Color.parseColor("#FFFFFF")
    }



//    private fun setStatusBarTransparent(activity: Activity) {
//
//        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//        activity.window.decorView.systemUiVisibility = flags
//        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.transparent)

//        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

//    }



//    fun setupFullScreen() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            val controller = window.insetsController
//            if (controller != null) {
//                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//                controller.systemBarsBehavior =
//                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        } else {
//            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        }
//    }


}