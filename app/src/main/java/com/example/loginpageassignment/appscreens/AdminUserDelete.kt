package com.example.loginpageassignment.appscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminUserDelete : LoggedInPageAdmin()
{

    private lateinit var editTextEmail: EditText
    private lateinit var buttonDelete: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = FirebaseFirestore.getInstance().collection("Users")
    private val queueRef = FirebaseFirestore.getInstance().collection("Queues")

    override fun refresh()
    {
        val go = Intent(this, AdminUserDelete::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminuserdelete)

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonDelete.setOnClickListener { handleDeleteUser() }
    }

    private fun initializeView()
    {
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonDelete = findViewById(R.id.buttonDelete)
    }

    private fun handleDeleteUser()
    {
        val iemail = editTextEmail.text.toString()

        //Check all users to look for a match
        userRef.whereEqualTo("email", iemail).get().addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty)
            {
                showToast("No account under that email.", this)
            }
            else
            {
                deleteUser(documents)
            }
        }
    }

    private fun deleteUser(documents : QuerySnapshot)
    {
        //Check all users to look for a match
        queueRef.whereEqualTo("user", documents.documents[0].getString("username")).get().addOnSuccessListener{ documentsTwo ->
            if (documentsTwo.isEmpty)
            {
                showToast("No queues under this account.", this)
            }
            else
            {
                documentsTwo.documents[0].reference.delete().addOnSuccessListener {
                    showToast("Queues under this account were deleted.", this)
                }
            }
        }

        documents.documents[0].reference.delete().addOnSuccessListener {
            showToast("The user under this email has been deleted.", this)
        }
    }
}