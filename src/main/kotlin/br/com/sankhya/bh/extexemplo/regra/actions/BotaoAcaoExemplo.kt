package br.com.sankhya.bh.extexemplo.regra.actions

import br.com.lughconsultoria.sankhyaw.services.boot.BotaoAcao
import br.com.lughconsultoria.sankhyaw.services.boot.enums.BotaoControlaAcesso
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava
import br.com.sankhya.extensions.actionbutton.ContextoAcao

@BotaoAcao(
    descricao = "Botão Ação exmeplo",
    instancia = "BhExemploTela",
    controlaAcesso = BotaoControlaAcesso.SIM
)
class BotaoAcaoExemplo: AcaoRotinaJava {
    override fun doAction(contexto: ContextoAcao) {
        contexto.mostraErro("Hello World!!!")
    }
}