package com.pyrion.game.poison_frog

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
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
import com.pyrion.poison_frog.R
import java.io.IOException
import java.util.*

class kotlin_nfc_read : AppCompatActivity() {
    private var TAG: String = "MainActivity"
    private lateinit var nfcPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_nfc_read)

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

        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED){
            val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (messages == null){
                Toast.makeText(applicationContext, "NO NFC MESSAGE", Toast.LENGTH_SHORT).show()
                return
            }

            for (i in messages.indices) showMsg(messages[i] as NdefMessage)
        }
    }

    fun showMsg(mMessage: NdefMessage) {
        val recs = mMessage.records
        for (i in recs.indices) {
            val record = recs[i]
            if (Arrays.equals(record.type, NdefRecord.RTD_URI)) {
                val u: Uri = record.toUri()
                val j = Intent(Intent.ACTION_VIEW)
                j.data = u
                startActivity(j)
                finish()
            }
        }
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
    }
}