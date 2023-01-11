package com.example.eagchat

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import com.example.eagchat.databinding.ActivityLoginBinding
import java.util.regex.Pattern
import java.util.zip.Inflater


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener{
            val intent = Intent(this,SignUp::class.java)
            finish()
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                    if (it.isSuccessful){
                        if (firebaseAuth.currentUser?.isEmailVerified == true) {
                            val intent = Intent(this, MainActivity::class.java)
                            finish()
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,"Please Verify the Email Address",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Empty Fields Are Not Allowed !!",Toast.LENGTH_SHORT).show()
            }
        }

        binding.forget.setOnClickListener{
            val builder=AlertDialog.Builder(this)
            builder.setTitle("Forget Password")
            val view=layoutInflater.inflate(R.layout.forget_password,null)
            builder.setView(view)
            builder.setPositiveButton("Reset",DialogInterface.OnClickListener{ _ , _ ->
                val username=view.findViewById<EditText>(R.id.et_username)
                forgotpassword(username)
            })
            builder.setNegativeButton("Close",DialogInterface.OnClickListener{ _ , _ -> })
            builder.show()
        }
    }

    private fun forgotpassword(username: EditText){
        if (username.text.toString().isEmpty()){
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()){
            return
        }
        firebaseAuth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Email Sent",Toast.LENGTH_SHORT).show()
                }
            }
    }

}
