package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.models.LoginRequest
import com.example.myapplication.services.Helpers
import com.example.myapplication.viewmodels.LoginViewModel

import com.example.myapplication.classes.Result

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.main)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.loginBtn.setOnClickListener(this::loginClick)

        observeViewModel()
    }
    private fun loginClick(view:View){
        if (!checkViews())
            return;
        val login = binding.loginEt.text.toString()
        val password = binding.passwordEt.text.toString()

        viewModel.login(login,password)
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading-> {
                    Helpers.blurElements(
                        binding.blurContainer,
                        binding.blurView,
                        binding.root,
                        this
                    )
                    Helpers.showItem(binding.loadingElement.loadingProgress)
                    Helpers.pumpUpElement(binding.loadingElement.animateContainer, binding.root)
                }
                is Result.Success -> {
                    saveAuthToken(result.data.token)
                    navigateToMainScreen()
                    Helpers.hideItem(binding.loadingElement.loadingProgress)
                    Helpers.hideItem(binding.blurContainer)
                    Thread.sleep(200)
                }
                is Result.Error -> {
                    Helpers.createToast(this,result.exception.message ?: "Login failed")
                    Helpers.hideItem(binding.loadingElement.loadingProgress)
                    Helpers.hideItem(binding.blurContainer)
                    Thread.sleep(200)
                }
            }
        }
    }
    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun saveAuthToken(token: String) {
        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("token", token).apply()
    }
    private fun checkViews():Boolean{
        val login = binding.loginEt.text.toString()
        val password = binding.passwordEt.text.toString()

        if (login.isEmpty()){
            Helpers.createToast(this,R.string.fill_login_hint)
            return false
        }
        if (password.isEmpty()){
            Helpers.createToast(this,R.string.fill_password_hint)
            return false
        }
        return true;
    }
}