package com.example.finance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.finance.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var myAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myAuth = FirebaseAuth.getInstance()

        setTextYouEnterAs()

        val onButtonClickListener = View.OnClickListener {view ->
            when (view.id) {
                R.id.buttonLoginActivitySignUp -> {
                    val email = binding.editTextLoginActivityEmail.text.toString()
                    val password = binding.editTextLoginActivityPassword.text.toString()

                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if(it.isSuccessful){
                                setEmailVerification()
                                setTextYouEnterAs()
                                Toast.makeText(this@LoginActivity, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show()
                            }
                            else{ Toast.makeText(this@LoginActivity, "Ошибка", Toast.LENGTH_SHORT).show() }
                        }
                    }
                    else { Toast.makeText(this@LoginActivity, "Введите почту и пароль", Toast.LENGTH_SHORT).show() }
                    Log.d("LoginActivity", "click Sign Up")
                }
                R.id.buttonLoginActivitySignIn -> {
                    val email = binding.editTextLoginActivityEmail.text.toString()
                    val password = binding.editTextLoginActivityPassword.text.toString()

                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                            if(it.isSuccessful){
                                setTextYouEnterAs()
                                Toast.makeText(this@LoginActivity, "Вход прошёл успешно", Toast.LENGTH_SHORT).show()
                            }
                            else Toast.makeText(this@LoginActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else Toast.makeText(this@LoginActivity, "Введите пароль и почту", Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity", "click Sign In")
                }
                R.id.buttonLoginActivitySignOut -> {
                    FirebaseAuth.getInstance().signOut()
                    setTextYouEnterAs()
                    Log.d("LoginActivity", "click Sign Out")
                }
            }
        }
        binding.buttonLoginActivitySignUp.setOnClickListener(onButtonClickListener)
        binding.buttonLoginActivitySignIn.setOnClickListener(onButtonClickListener)
        binding.buttonLoginActivitySignOut.setOnClickListener(onButtonClickListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setEmailVerification(){
        myAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) Toast.makeText(this@LoginActivity, "Зайдите в вашу почту/n и подтвердите аккаунт", Toast.LENGTH_LONG).show()
            else Toast.makeText(this@LoginActivity, "Подтверждение не пришло", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTextYouEnterAs(){
        val yourText : String = if (myAuth.currentUser != null) "${resources.getString(R.string.youEnterAs)} ${myAuth.currentUser!!.email}"
        else resources.getString(R.string.youDoNotEnterAccount)
        binding.textViewLoginActivityYouEnterAs.text = yourText
    }
}