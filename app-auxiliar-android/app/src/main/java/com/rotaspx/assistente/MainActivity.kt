package com.rotaspx.assistente

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnAtivar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnAtivar = findViewById(R.id.btnAtivar)

        btnAtivar.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        // Se foi aberto via rota deep-link com dados da entrega
        intent?.data?.let { uri ->
            val codigoRastreio = uri.getQueryParameter("codigo")
            val nomeRecebedor = uri.getQueryParameter("recebedor")
            if (!codigoRastreio.isNullOrEmpty()) {
                RoboBaixaService.dadosPendentes = RoboBaixaService.DadosEntrega(
                    codigo = codigoRastreio,
                    recebedor = nomeRecebedor ?: ""
                )
                RoboBaixaService.iniciarFluxo(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarStatus()
    }

    private fun atualizarStatus() {
        if (isAcessibilidadeAtiva(this)) {
            tvStatus.text = "Status: Ativo e Pronto ✓"
            tvStatus.setTextColor(Color.parseColor("#388E3C"))
            btnAtivar.text = "Configurações de Acessibilidade"
        } else {
            tvStatus.text = "Status: Desativado ✕"
            tvStatus.setTextColor(Color.parseColor("#D32F2F"))
            btnAtivar.text = "Ativar no Sistema"
        }
    }

    private fun isAcessibilidadeAtiva(context: Context): Boolean {
        val servicoAlvo = "${context.packageName}/${RoboBaixaService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(servicoAlvo)
    }
}
