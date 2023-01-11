package com.example.eagchat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.Toast
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import com.example.eagchat.databinding.ActivitySignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class SignUp : AppCompatActivity() {
   private lateinit var binding: ActivitySignUpBinding
   private lateinit var firebaseAuth: FirebaseAuth
   private lateinit var fdata : DatabaseReference

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)

       binding = ActivitySignUpBinding.inflate(layoutInflater)
       setContentView(binding.root)

       supportActionBar?.hide()

       firebaseAuth = FirebaseAuth.getInstance()

       binding.textView.setOnClickListener {
           val intent = Intent(this,Login::class.java)
           finish()
           startActivity(intent)
       }

       binding.button.setOnClickListener {
           val name = binding.TxtName.text.toString()
           val email = binding.emailEt.text.toString()
           val pass = binding.passET.text.toString()
           val confirmPass = binding.confirmPassEt.text.toString()

           if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
               if(pass == confirmPass){

                   firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                       if(it.isSuccessful){
                           val user=firebaseAuth.currentUser
                           user?.sendEmailVerification()
                               ?.addOnCompleteListener { task ->
                                   Toast.makeText(this,"Email is sent",Toast.LENGTH_SHORT).show()
                                   if (task.isSuccessful){
                                       addUserToDatabase(name,email,firebaseAuth.currentUser?.uid!!)
                                       val intent = Intent(this,Login::class.java)
                                       finish()
                                       startActivity(intent)
                                   }
                               }
                       }else{
                           Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                       }
                   }
               }else{
                   Toast.makeText(this,"Password is not matching",Toast.LENGTH_SHORT).show()
               }
           }else{
               Toast.makeText(this, "Empty Fields are Not Allowed!!",Toast.LENGTH_SHORT).show()
           }
       }

   }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        fdata=FirebaseDatabase.getInstance().getReference()
        fdata.child("user").child(uid).setValue(User(name, email, uid))
    }
}