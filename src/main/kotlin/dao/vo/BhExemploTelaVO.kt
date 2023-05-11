package dao.vo

import br.com.lugh.dao.WrapperVO
import br.com.lugh.dsl.metadados.pojo.Delegate
import br.com.lugh.dsl.metadados.pojo.DelegateNotNull
import br.com.lugh.dsl.metadados.pojo.Pojo
import java.sql.Timestamp

class BhExemploTelaVO(vo: WrapperVO) : Pojo(vo) {
    var sequencia: Int by DelegateNotNull()
    var codemp: Int? by Delegate()
    var descricao: String? by Delegate()
    var ativo: String? by Delegate()
    var dhteste: Timestamp? by Delegate()
}