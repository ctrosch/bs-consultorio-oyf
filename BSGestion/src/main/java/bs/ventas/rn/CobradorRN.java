/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.ventas.dao.CobradorDAO;
import bs.ventas.modelo.Cobrador;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class CobradorRN implements Serializable {

    @EJB
    private CobradorDAO cobradorDAO;

    public Cobrador getCobrador(String value) {

        return cobradorDAO.getObjeto(Cobrador.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Cobrador c, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (cobradorDAO.getObjeto(Cobrador.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un cobrador con el código" + c.getCodigo());
            }
            cobradorDAO.crear(c);
        } else {
            cobradorDAO.editar(c);
        }
    }

    public void eliminar(Cobrador cobrador) throws Exception {

        cobradorDAO.eliminar(cobrador);
    }

    public List<Cobrador> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return cobradorDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    public List<Cobrador> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return cobradorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

}
