/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.TipoDocumentoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.TipoDocumento;
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
public class TipoDocumentoRN implements Serializable {

    @EJB
    private TipoDocumentoDAO tipoDocumentoDAO;

    public TipoDocumento getTipoDocumento(String value) {

        return tipoDocumentoDAO.getObjeto(TipoDocumento.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoDocumento t, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (tipoDocumentoDAO.getObjeto(TipoDocumento.class, t.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya exist un tipo de documento con el código" + t.getCodigo());
            }
            tipoDocumentoDAO.crear(t);
        } else {
            tipoDocumentoDAO.editar(t);
        }
    }

    public void eliminar(TipoDocumento tipoDocumento) throws Exception {

        tipoDocumentoDAO.eliminar(tipoDocumento);

    }

    public List<TipoDocumento> getLista(boolean mostrarDebaja) {

        return tipoDocumentoDAO.getListaByBusqueda(null, "", mostrarDebaja, 15);
    }

    public List<TipoDocumento> getLista(boolean mostrarDebaja, int cantMax) {

        return tipoDocumentoDAO.getListaByBusqueda(null, "", mostrarDebaja, cantMax);
    }

    public List<TipoDocumento> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tipoDocumentoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

}
