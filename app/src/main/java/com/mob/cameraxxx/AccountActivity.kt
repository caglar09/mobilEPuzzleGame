package com.mob.cameraxxx

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import co.nedim.maildroidx.MaildroidX
import co.nedim.maildroidx.MaildroidXType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.mob.cameraxxx.data.User
import com.mob.cameraxxx.helpers.NetworkHelper
import com.mob.cameraxxx.service.DataAdapterService
import java.lang.Exception
import java.util.*

class AccountActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var currentUser: FirebaseUser? = null
    var dbRef = FirebaseDatabase.getInstance().getReference("users")
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var dataAdapterService: DataAdapterService
    lateinit var faceAuthButton: Button
    lateinit var googleAuthButton: Button
    lateinit var emailAuthButton: Button
    private var formValid = false;

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        dataAdapterService= DataAdapterService(this)
        if (NetworkHelper.isOnline(this@AccountActivity) === false) {
            var networkIntent = Intent(this@AccountActivity, NetworkErrorActivity::class.java)
            startActivity(networkIntent)
            finish()
        }

        currentUser = mAuth.currentUser
        if (currentUser != null) {
            var intent = Intent(this@AccountActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        faceAuthButton = findViewById(R.id.faceAuthButton)
        googleAuthButton = findViewById(R.id.googleAuthButton)
        emailAuthButton = findViewById(R.id.emailAuth)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleAuthButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, 10)
        }

        emailAuthButton.setOnClickListener {
            var registerLayout = LayoutInflater.from(this).inflate(R.layout.activity_email_dialog, null)
            var registerDialogBuilder = AlertDialog.Builder(this).setView(registerLayout).create()
            registerDialogBuilder.show()
            var emailLayout = registerLayout.findViewById<TextInputLayout>(R.id.txt_eposta_layout)
            var passwordLayout = registerLayout.findViewById<TextInputLayout>(R.id.txt_password_layout)
            var email = registerDialogBuilder.findViewById<TextInputEditText>(R.id.emailText)
            var password = registerDialogBuilder.findViewById<TextInputEditText>(R.id.passwordText)
            var registerButton = registerDialogBuilder.findViewById<Button>(R.id.btn_register)

            registerButton!!.setOnClickListener {
                email!!.addTextChangedListener(textChangeValidation(emailLayout))
                password!!.addTextChangedListener(textChangeValidation(passwordLayout))
                if (validateInputs(email, password, emailLayout, passwordLayout) && formValid) {
                    mAuth.fetchSignInMethodsForEmail(email.text.toString().trim()).addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.result!!.signInMethods!!.size == 0) {
                            mAuth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener(this, OnCompleteListener {
                                if (it.isSuccessful) {
                                    saveUserInfoForDb()
                                    registerDialogBuilder.hide()
                                } else {
                                    Toast.makeText(this@AccountActivity, it.exception!!.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                            })
                        } else {
                            mAuth.signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener(this, OnCompleteListener {
                                if (it.isSuccessful) {
                                    var i = Intent(this@AccountActivity, HomeActivity::class.java)
                                    startActivity(i)
                                    registerDialogBuilder.hide()
                                    finish()
                                } else {
                                    Toast.makeText(this@AccountActivity, it.exception!!.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                    })

                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this@AccountActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun textChangeValidation(layout: TextInputLayout) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            try {
                if (s!!.length > 0) {
                    layout.error = ""
                    formValid = true
                } else {
                    layout.error = "Bu alan boş olamaz."
                    formValid = false
                }
            } catch (ex: Exception) {
                Toast.makeText(this@AccountActivity, ex.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun validateInputs(txtEmail: TextInputEditText, txtPassword: TextInputEditText, emailLayout: TextInputLayout, passwordLayout: TextInputLayout): Boolean {
        var _validForm = formValid
        if (txtEmail.text.toString().isEmpty()) {
            emailLayout.error = "Bu alan boş geçilemez"
            _validForm = false
        }
        if (txtPassword.text.toString().isEmpty()) {
            _validForm = false
            passwordLayout.error = "Bu alan boş geçilemez"
        }
        if (!txtEmail.text.toString().isEmpty() && !txtPassword.text.toString().isEmpty())
            _validForm = true

        formValid = _validForm
        return formValid
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.additionalUserInfo!!.isNewUser) {
                            saveUserInfoForDb()
                        }
                        var homeIntent = Intent(this@AccountActivity, HomeActivity::class.java)
                        startActivity(homeIntent)
                        finish()
                    } else {
                        Toast.makeText(this@AccountActivity, "Giriş başarısız", Toast.LENGTH_LONG).show()
                    }

                }
    }

    fun saveUserInfoForDb() {
        var aUser = mAuth.currentUser!!
        var user = User(userId = aUser.uid, email = aUser.email!!.toString(), userName = "User_" + Date().getTime().toString(),sections = dataAdapterService.getSections().toMutableList())
        dbRef.child(aUser.uid).setValue(user).addOnCompleteListener(this) { _t ->
            if (_t.isSuccessful) {
               // sendMail(user.email, "Hoş geldiniz, Ebeveyn kullanıcı kodunuz : <b>1234</b> unutursanız uygulama içerisindeki profil kısmından güncelleyebilirsiniz.")
                Toast.makeText(this@AccountActivity, "Bilgileriniz kayıt edildi", Toast.LENGTH_LONG).show()
                var i = Intent(this@AccountActivity, HomeActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    //çalışmıyor
    /*fun sendMail(to: String, text: String) {
       try {
           MaildroidX.Builder()
                   .smtp("smtp.gmail.com")
                   .smtpUsername("cglryldrm.009@gmail.com")
                   .smtpPassword("98Cglr09.")
                   .port("465")
                   .type(MaildroidXType.HTML)
                   .to(to)
                   .from("cglryldrm.09@gmail.com")
                   .subject("Çek Yap Öğren Ebeveyn Bilgileriniz!!")
                   .body(text)
                   .onCompleteCallback(object : MaildroidX.onCompleteCallback {
                       override val timeout: Long = 5000
                       override fun onFail(errorMessage: String) {
                           Log.e("MailERROR",errorMessage)
                           Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                       }

                       override fun onSuccess() {
                           Toast.makeText(applicationContext, "Mail sent!", Toast.LENGTH_SHORT).show()
                       }
                   })
                   .mail()
       }
       catch (ex:Exception){
           Log.e("MailERROR",ex.message)
           Log.e("MailERROR2",ex.localizedMessage)
           Toast.makeText(this@AccountActivity,ex.localizedMessage,Toast.LENGTH_LONG).show()
       }
    }*/
}
