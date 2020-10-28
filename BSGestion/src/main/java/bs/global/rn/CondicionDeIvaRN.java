/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.CondicionDeIvaDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.CondicionDeIva;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author Claudio
 */
@Stateless
public class CondicionDeIvaRN implements Serializable {

    @EJB
    private CondicionDeIvaDAO condicionDeIvaDAO;

    public CondicionDeIva getCondicionDeIva(String codigo) {

        return condicionDeIvaDAO.getObjeto(CondicionDeIva.class, codigo);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CondicionDeIva guardar(CondicionDeIva c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (condicionDeIvaDAO.getObjeto(CondicionDeIva.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe condición de IVA con el código" + c.getCodigo());
            }
            condicionDeIvaDAO.crear(c);
        } else {
            c = (CondicionDeIva) condicionDeIvaDAO.editar(c);
        }
        return c;
    }

    public List<CondicionDeIva> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return condicionDeIvaDAO.getListaByBusqueda(null, txtBusqueda, false, 15);

    }

    public List<CondicionDeIva> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return condicionDeIvaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);

    }

    public void eliminar(CondicionDeIva c) throws Exception {

        condicionDeIvaDAO.eliminar(c);
    }
}
