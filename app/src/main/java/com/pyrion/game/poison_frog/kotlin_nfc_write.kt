package com.pyrion.game.poison_frog

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pyrion.game.poison_frog.R
import java.io.IOException

class kotlin_nfc_write : AppCompatActivity() {
    private lateinit var nfcPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_nfc_write)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPendingIntent = PendingIntent.getActivity(
                this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this);
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val detectedTag : Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        val writeValue : String = "http://www.naver.com";
        val message: NdefMessage = createTagMessage(writeValue);

        if (detectedTag != null) {
            writeTag(message, detectedTag)
        }
    }

    private fun createTagMessage(msg: String): NdefMessage {
        return NdefMessage(NdefRecord.createUri(msg))
    }

    fun writeTag(message: NdefMessage, tag: Tag) {
        val size = message.toByteArray().size
        try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    Toast.makeText(applicationContext, "can not write NFC tag", Toast.LENGTH_SHORT).show()
                }
                if (ndef.maxSize < size) {
                    Toast.makeText(applicationContext, "NFC tag size too large", Toast.LENGTH_SHORT).show()
                }
                ndef.writeNdefMessage(message)
                Toast.makeText(applicationContext, "NFC tag is writted", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.message?.let { Log.i("NFC!!", it) };
        }
    }
}