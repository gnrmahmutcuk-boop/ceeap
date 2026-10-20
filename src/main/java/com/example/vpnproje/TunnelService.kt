package com.example.vpnproje

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream

class TunnelService : VpnService() {
    private var tun: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == DURDUR) {
            kapat()
            stopSelf()
            return START_NOT_STICKY
        }
        baslat()
        return START_STICKY
    }

    private fun baslat() {
        if (tun != null) return
        // Sadece test ağını yönlendiriyoruz; normal internet bozulmaz.
        val fd = Builder()
            .setSession("VpnProje")
            .addAddress("10.99.0.1", 24)
            .addRoute("10.99.0.0", 24)
            .establish() ?: return
        tun = fd
        calisiyor = true
        Thread {
            val girdi = FileInputStream(fd.fileDescriptor)
            val tampon = ByteArray(32767)
            var sayac = 0
            try {
                while (true) {
                    val n = girdi.read(tampon)
                    if (n < 0) break
                    if (n > 0) {
                        sayac++
                        Log.d("VpnProje", "paket #$sayac, $n bayt")
                    }
                }
            } catch (_: Exception) {
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    private fun kapat() {
        try {
            tun?.close()
        } catch (_: Exception) {
        }
        tun = null
        calisiyor = false
    }

    override fun onDestroy() {
        kapat()
        super.onDestroy()
    }

    companion object {
        const val DURDUR = "DURDUR"
        @Volatile
        var calisiyor = false
    }
}
