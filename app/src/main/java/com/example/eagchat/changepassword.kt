package com.example.eagchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.eagchat.databinding.ActivityChangepasswordBinding
import com.example.eagchat.databinding.ActivityLoginBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class changepassword : AppCompatActivity() {

    private lateinit var bd: ActivityChangepasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityChangepasswordBinding.inflate(layoutInflater)
        setContentView(bd.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()
        bd.chaPass.setOnClickListener{
            changePassword()
        }

    }

    private fun changePassword(){
        if (bd.etCurrentPassword.text.isNotEmpty() &&
            bd.etNewPassword.text.isNotEmpty() &&
            bd.etConfirmPassword.text.isNotEmpty()
        ){
            if(bd.etNewPassword.text.toString().equals(bd.etConfirmPassword.text.toString())){
                val user = firebaseAuth.currentUser
                if (user!=null && user.email!=null){
                    val credential = EmailAuthProvider.getCredential(user.email!!,bd.etCurrentPassword.text.toString())
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener{
                           if (it.isSuccessful){
                               Toast.makeText(this,"Re-Authentication Success",Toast.LENGTH_SHORT).show()
                               user?.updatePassword(bd.etNewPassword.text.toString())
                                   ?.addOnCompleteListener{ task ->
                                       if (task.isSuccessful){
                                           Toast.makeText(this,"Password Changed",Toast.LENGTH_SHORT).show()
                                           firebaseAuth.signOut()
                                           startActivity(Intent(this,Login::class.java))
                                           finish()
                                       }
                                   }
                           }else{
                               Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                           }
                        }
                }else{
                    startActivity(Intent(this,Login::class.java))
                    finish()
                }
            }else{
                Toast.makeText(this,"Password mismatching",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"Please Enter all the Fields",Toast.LENGTH_SHORT).show()
        }
    }
}