package dao

import br.com.lugh.dao.GenericDAO
import dao.vo.BhExemploTelaVO
import java.math.BigDecimal

class BhExemploTelaDAO : GenericDAO<BhExemploTelaVO> ("BhExemploTela", BhExemploTelaVO::class.java) {
    fun findByPk(sequencia: BigDecimal) = BhExemploTelaVO(
        requireNotNull(
            facadeW.findByFinder(entityName, " SEQUENCIA = ? ", sequencia).firstOrNull()
        ) {"Erro ao buscar o registro na tela de exmplo pela PK[Sequencia=$sequencia]"}
    )
}