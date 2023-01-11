package com.example.eagchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbref = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView=findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager=LinearLayoutManager(this)
        userRecyclerView.adapter = adapter


        mDbref.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser=postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid !=currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.log_out){
            //write logic for Signout
            mAuth.signOut()
            val intent= Intent(this,Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        else if (item.itemId==R.id.delete){
            val user=Firebase.auth.currentUser?.uid
            val em=Firebase.auth.currentUser
            val u=user.toString()

            em?.delete()?.addOnCompleteListener{
                if(it.isSuccessful){
                    mDbref.child("user").child(u).removeValue().addOnSuccessListener {
                        Toast.makeText(this,"Account Succesfully Deleted",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this,"Error occured",Toast.LENGTH_SHORT).show()
                    }
                    mDbref.child("chats").child(u).removeValue().addOnSuccessListener {
                        Toast.makeText(this,"Account Succesfully Deleted",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this,"Error occured",Toast.LENGTH_SHORT).show()
                    }
                    val intent= Intent(this,Login::class.java)
                    finish()
                    startActivity(intent)
                }else{
                    Log.e("error",it.exception.toString())
                }
            }
            return true
        }

        else if (item.itemId == R.id.change){
            val intent= Intent(this,changepassword::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }

}
