package com.rotaspx.assistente

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class RoboBaixaService : AccessibilityService() {

    data class DadosEntrega(
        val codigo: String,
        val recebedor: String
    )

    companion object {
        var dadosPendentes: DadosEntrega? = null

        fun iniciarFluxo(context: Context) {
            // Tenta abrir o app oficial de entregas
            val intent = context.packageManager.getLaunchIntentForPackage("com.shopee.express.driver")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val dados = dadosPendentes ?: return
        val rootNode = rootInActiveWindow ?: return

        // 1. Tentar encontrar campo de busca ou inserção de código
        val campos = rootNode.findAccessibilityNodeInfosByText("pesquisar")
        if (campos.isNotEmpty()) {
            val campo = campos[0]
            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, dados.codigo)
            }
            campo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }

        // 2. Tentar clicar em botões de ação comuns como "Entregar" ou "Concluir"
        val botoesEntregar = rootNode.findAccessibilityNodeInfosByText("Entregar")
        if (botoesEntregar.isNotEmpty()) {
            botoesEntregar[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    override fun onInterrupt() {
        // Interrompido pelo sistema
    }
}
