/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.CorreoElectronicoDAO;
import bs.administracion.modelo.CorreoElectronico;
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
public class CorreoElectronicoRN implements Serializable {

    @EJB
    private CorreoElectronicoDAO correoElectronicoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CorreoElectronico guardar(CorreoElectronico ce) throws Exception {

        if (ce.getId() == null) {
            correoElectronicoDAO.crear(ce);
        } else {
            ce = (CorreoElectronico) correoElectronicoDAO.editar(ce);
        }

        return ce;
    }

    public List<CorreoElectronico> getListaByBusqueda(String txtBusqueda, String enviado, int cantMax) {

        return correoElectronicoDAO.getListaByBusqueda(txtBusqueda, enviado, cantMax);
    }

    public List<CorreoElectronico> getCorreosNoEnviados() {

        return correoElectronicoDAO.queryList(CorreoElectronico.class, "SELECT c FROM CorreoElectronico c WHERE c.enviado = 'N' ORDER BY c.fechaEnvio DESC ");
    }
}
