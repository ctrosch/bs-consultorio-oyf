/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.TipoConceptoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.TipoConcepto;
import bs.global.modelo.TipoConceptoPK;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class TipoConceptoRN implements Serializable {

    @EJB
    private TipoConceptoDAO tipoConceptoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoConcepto t, boolean esNuevo) throws Exception {

        if (esNuevo) {

            TipoConceptoPK idPk = new TipoConceptoPK(t.getModulo(), t.getCodigo());

            if (tipoConceptoDAO.getObjeto(TipoConcepto.class, idPk) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un tipo de concepto con el código " + t.getCodigo());
            }
            tipoConceptoDAO.crear(t);
        } else {
            tipoConceptoDAO.editar(t);
        }
    }

    public TipoConcepto getTipoConcepto(String modulo, String codigo) {

        return tipoConceptoDAO.getTipoConcepto(modulo, codigo);
    }

    public TipoConcepto getTipoConcepto(TipoConceptoPK idPK) {

        return tipoConceptoDAO.getTipoConcepto(idPK);
    }

    public List<TipoConcepto> getLista(String modulo, boolean mostrarDebaja) {

        return tipoConceptoDAO.getListaByBusqueda(modulo, "", mostrarDebaja, 50);
    }

    public List<TipoConcepto> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja) {

        return tipoConceptoDAO.getListaByBusqueda(modulo, txtBusqueda, mostrarDebaja, 15);
    }

    public List<TipoConcepto> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoConceptoDAO.getListaByBusqueda(modulo, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoConcepto> getLista(boolean mostrarDebaja) {

        return tipoConceptoDAO.getListaByBusqueda(null, "", mostrarDebaja, 50);
    }

    public void eliminar(TipoConcepto t) throws Exception {

        tipoConceptoDAO.eliminar(t);
    }

}
