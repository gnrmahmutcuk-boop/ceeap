package com.example.vpnproje

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var durum: TextView
    private lateinit var dugme: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val kok = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.BLACK)
            setPadding(48, 48, 48, 48)
        }
        durum = TextView(this).apply {
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        dugme = Button(this).apply { setOnClickListener { tikla() } }
        kok.addView(durum)
        kok.addView(dugme)
        setContentView(kok)
    }

    override fun onResume() {
        super.onResume()
        guncelle()
    }

    private fun tikla() {
        if (TunnelService.calisiyor) {
            startService(Intent(this, TunnelService::class.java).setAction(TunnelService.DURDUR))
            durum.postDelayed({ guncelle() }, 300)
        } else {
            val izin = VpnService.prepare(this)
            if (izin != null) startActivityForResult(izin, 1) else baslat()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) baslat()
    }

    private fun baslat() {
        startService(Intent(this, TunnelService::class.java))
        durum.postDelayed({ guncelle() }, 300)
    }

    private fun guncelle() {
        val acik = TunnelService.calisiyor
        durum.text = if (acik) "Bağlı (test tüneli)" else "Bağlı değil"
        dugme.text = if (acik) "Kes" else "Bağlan"
    }
}
