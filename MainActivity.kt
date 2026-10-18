
package com.shanks.ai

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.provider.MediaStore
import android.provider.Settings
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.graphics.Color
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var status: TextView
    private val blue = Color.rgb(45, 125, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(7, 11, 22)
        window.navigationBarColor = Color.rgb(7, 11, 22)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(Color.rgb(7, 11, 22))
        }

        val title = TextView(this).apply {
            text = "SHANKS AI"
            textSize = 34f
            setTextColor(blue)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "YOUR PERSONAL PHONE ASSISTANT"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "Ready when you are."
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 32, 8, 24)
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)

        addButton(layout, "🎙  TALK TO SHANKS") {
            listen()
        }

        addButton(layout, "WhatsApp") {
            openApp("com.whatsapp")
        }

        addButton(layout, "Camera") {
            openCamera()
        }

        addButton(layout, "Gallery") {
            openGallery()
        }

        addButton(layout, "Settings") {
            launchIntent(Intent(Settings.ACTION_SETTINGS))
        }

        addButton(layout, "Free Fire") {
            if (!openApp("com.dts.freefireth")) {
                openApp("com.dts.freefiremax")
            }
        }

        setContentView(layout)
    }

    private fun addButton(
        layout: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(25, 45, 78))
            isAllCaps = false
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 7, 0, 7)
        layout.addView(button, params)
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speak to Shanks"
        )

        try {
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            status.text = "Voice recognition isn't available on this phone."
        }
    }

    @Deprecated("Legacy speech result callback")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val words = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )?.firstOrNull() ?: return

            status.text = "You said: $words"
            handleCommand(words.lowercase(Locale.ROOT))
        }
    }

    private fun handleCommand(command: String) {
        when {
            "whatsapp" in command -> openApp("com.whatsapp")
            "camera" in command -> openCamera()
            "gallery" in command || "photos" in command ->
                openGallery()
            "settings" in command -> launchIntent(
                Intent(Settings.ACTION_SETTINGS)
            )
            "free fire max" in command ->
                if (!openApp("com.dts.freefiremax")) {
                    status.text = "Free Fire MAX wasn't found."
                }
            "free fire" in command ->
                if (!openApp("com.dts.freefireth") &&
                    !openApp("com.dts.freefiremax")) {
                    status.text = "Free Fire wasn't found."
                }
            "hello" in command || "who are you" in command ->
                status.text = "I'm SHANKS AI, your personal assistant."
            else -> status.text =
                "I'm still learning that command: $command"
        }
    }

    private fun openApp(packageName: String): Boolean {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
            return true
        }
        status.text = "That app wasn't found on your phone."
        return false
    }

    private fun openCamera() {
        launchIntent(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        launchIntent(intent)
    }

    private fun launchIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            status.text = "Couldn't open that feature on this phone."
        }
    }
}
