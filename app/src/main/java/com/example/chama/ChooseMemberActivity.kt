package com.example.chama

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chama.databinding.ActivityChooseMemberBinding
import com.google.android.gms.fido.common.Transport
import kotlinx.coroutines.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.collections.ArrayList
import javax.mail.Message.RecipientType
import javax.mail.MessagingException
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication

class ChooseMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseMemberBinding
    private lateinit var profilePictureUri: Uri

    private val properties = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com") // Replace with the actual SMTP server hostname
        put("mail.smtp.port", "587") // Replace with the actual SMTP server port
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numberOfMembers = intent.getIntExtra("numberOfMembers", 0)
        val editTextIds = generateEditTextFields(numberOfMembers)

        val sendInvitesBtn = binding.btnSendInvites
        sendInvitesBtn.setOnClickListener {
            val emailAddresses = getEmailAddressesFromEditTexts(editTextIds)
            sendEmails(emailAddresses)

        }
    }

    private fun generateEditTextFields(numberOfMembers: Int): ArrayList<Int> {
        val linearLayout = findViewById<LinearLayout>(R.id.chooseMemberConstraintLayout)
        val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.edit_text_margin_top)
        val editTextIds = ArrayList<Int>()

        for (i in 0 until numberOfMembers) {
            val editText = EditText(this).apply {
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = topMarginInPixels
                }
                hint = "Enter Email" // Set the desired hint text
                id = View.generateViewId() // Generate a unique ID
                backgroundTintList = ContextCompat.getColorStateList(this@ChooseMemberActivity, R.color.maroon) // Set the backgroundTint color
                this.layoutParams = layoutParams
            }
            linearLayout.addView(editText)
            editTextIds.add(editText.id)
        }

        return editTextIds
    }

    private fun getEmailAddressesFromEditTexts(editTextIds: ArrayList<Int>): List<String> {
        val emailAddresses = mutableListOf<String>()
        for (editTextId in editTextIds) {
            val editText = findViewById<EditText>(editTextId)
            val emailAddress = editText.text.toString().trim()
            if (emailAddress.isNotEmpty()) {
                emailAddresses.add(emailAddress)
            }
        }
        return emailAddresses
    }

    // JavaMail API to send an email using an SMTP server.
    private fun sendEmails(emailAddresses: List<String>) {
        val session = javax.mail.Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                val username = "Winfred.Omondi@strathmore.edu" // Replace with your email username
                val password = "Wi25YiP22" // Replace with your email password
                return PasswordAuthentication(username, password)
            }
        })

        val coroutineScope = CoroutineScope(Dispatchers.IO)

        coroutineScope.launch {
            val sendEmailTasks = emailAddresses.map { emailAddress ->
                async {
                    try {
                        val userId = "123456" // Replace with the actual user ID
                        val token = "abcdef123456" // Replace with the actual token

                        val deepLinkUri = Uri.Builder()
                            .scheme("https") // Replace with your desired scheme (http or https)
                            .authority("example.com") // Replace with your domain or host
                            .path("/register") // Replace with your desired path
                            .appendQueryParameter("userId", userId)
                            .appendQueryParameter("token", token)
                            .build()

                        val registrationLink = deepLinkUri.toString()

                        val message = MimeMessage(session)
                        message.setFrom(InternetAddress("Winfred.Omondi@strathmore.edu")) // Replace with the sender's email address
                        message.setRecipients(RecipientType.TO, InternetAddress.parse(emailAddress)) // Use the actual recipient's email address
                        message.subject = " Invitation to Join Our Chama Group!"
                        message.setText("Hello [Recipient's Name],\n" +
                                "\n" +
                                "Join our Chama group, [Chama Group Name], and be part of an exciting financial community! We believe in empowering individuals like you to achieve financial growth and mutual support.\n" +
                                "\n" +
                                "Accept the invitation and join us by clicking here: $registrationLink. This link will lead you to the registration page, where you can become an official member and access exclusive benefits.\n" +
                                "\n" +
                                "If joining isn't the right fit for you at the moment, we completely understand. No pressure, we respect your decision.\n" +
                                "\n" +
                                "Looking forward to having you onboard,\n" +
                                "\n" +
                                "[Your Name]\n" +
                                "[Chama Group Name]")

                        // Send the email message
                        javax.mail.Transport.send(message)

                       // println("Email sent successfully to: $emailAddress")
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Invitation sent successfully to: $emailAddress", Toast.LENGTH_SHORT).show()

                            val chamaId = intent.getStringExtra("chamaId")
                            val chamaName = intent.getStringExtra("chamaName")
                            val description = intent.getStringExtra("description")
                            val goals = intent.getStringExtra("goals")
                            val targetPerMonth = intent.getStringExtra("targetPerMonth")
                            val numberOfMembers = intent.getStringExtra("numberOfMembers")
                            val firstName = intent.getStringExtra("firstName") ?: ""
                            val lastName = intent.getStringExtra("lastName") ?: ""

                            val userProfilePictureString = intent.getStringExtra("userProfilePicture")
                            profilePictureUri = Uri.parse(userProfilePictureString)

                            val intent = Intent(this@ChooseMemberActivity, ChamaCreatedActivity::class.java)
                            intent.putExtra("chamaId", chamaId)
                            intent.putExtra("chamaName", chamaName)
                            intent.putExtra("description", description)
                            intent.putExtra("goals", goals)
                            intent.putExtra("targetPerMonth", targetPerMonth)
                            intent.putExtra("numberOfMembers", numberOfMembers)
                            intent.putExtra("firstName", firstName)
                            intent.putExtra("lastName", lastName)
                            intent.putExtra("userProfilePicture", profilePictureUri?.toString())

                            startActivity(intent)
                            finish() // Optional: Finish the current activity if you don't want to navigate back to it
                        }


                    } catch (e: MessagingException) {
                        //println("Error sending email to: $emailAddress. Error: ${e.message}")
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Error sending invitation to: $emailAddress", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

            sendEmailTasks.awaitAll()
        }
    }
}
