/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.ImpuestoPorEntidadDAO;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class ImpuestoPorEntidadRN implements Serializable {

    @EJB
    ImpuestoPorEntidadDAO impuestoPorEntidadDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ImpuestoPorEntidad guardar(ImpuestoPorEntidad i, boolean esNuevo) throws Exception {

        control(i, esNuevo);

        if (i.getId() == null) {
            impuestoPorEntidadDAO.crear(i);
        } else {
            i = (ImpuestoPorEntidad) impuestoPorEntidadDAO.editar(i);
        }

        return i;
    }

    public void control(ImpuestoPorEntidad i, boolean esNuevo) throws ExcepcionGeneralSistema {

        if (i != null) {

            if (i.getEntidadComercial() == null) {
                throw new ExcepcionGeneralSistema("El impuesto debe estar asociado a una entidad comercial");
            }

            if (i.getTipoImpuesto() == null) {
                throw new ExcepcionGeneralSistema("El impuesto no puede estar vacio");
            }

            if (i.getConceptoVenta() == null) {
                throw new ExcepcionGeneralSistema("El concepto no puede estar vacio");
            }
        }
    }

    public ImpuestoPorEntidad getImpuestoEntidad(Integer id) {

        return impuestoPorEntidadDAO.getObjeto(ImpuestoPorEntidad.class, id);
    }

    public void eliminar(ImpuestoPorEntidad categoria) throws Exception {

        impuestoPorEntidadDAO.eliminar(categoria);
    }

    public List<ImpuestoPorEntidad> getListaByBusqueda(String txtBusqueda, String nroCuenta, boolean mostrarDeBaja) {

        return impuestoPorEntidadDAO.getListaByBusqueda(null, txtBusqueda, nroCuenta, mostrarDeBaja, 15);
    }

    public List<ImpuestoPorEntidad> getListaByNroCuenta(String nroCuenta, boolean mostrarDeBaja) {

        return impuestoPorEntidadDAO.getListaByBusqueda(null, "", nroCuenta, mostrarDeBaja, 15);
    }

}
