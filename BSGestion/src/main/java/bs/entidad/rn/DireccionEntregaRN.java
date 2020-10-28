/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.DireccionEntregaDAO;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.DireccionEntregaEntidadPK;
import bs.entidad.modelo.EntidadComercial;
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
public class DireccionEntregaRN implements Serializable {

    @EJB
    DireccionEntregaDAO direccionEntregaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(DireccionEntregaEntidad d, boolean esNuevo) throws Exception {

        puedoGuardar(d, esNuevo);

        if (esNuevo) {

            DireccionEntregaEntidadPK idPk = new DireccionEntregaEntidadPK(d.getCodigo(), d.getNrocta());

            if (direccionEntregaDAO.getObjeto(DireccionEntregaEntidad.class, idPk) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una dirección de entrega con el código " + d.getCodigo());
            }
            direccionEntregaDAO.crear(d);
        } else {
            direccionEntregaDAO.editar(d);
        }
    }

    public void puedoGuardar(DireccionEntregaEntidad dirEntrega, boolean esNuevo) throws ExcepcionGeneralSistema {

        if (dirEntrega != null) {

            if (dirEntrega.getLocalidad() == null) {
                throw new ExcepcionGeneralSistema("La localidad no puede estar vacía");
            }
        }
    }

    public DireccionEntregaEntidad getDireccionEntregaEntidad(String codigo, String nrocta) {

        DireccionEntregaEntidadPK idPK = new DireccionEntregaEntidadPK(codigo, nrocta);
        return direccionEntregaDAO.getObjeto(DireccionEntregaEntidad.class, idPK);
    }

    public void eliminar(DireccionEntregaEntidad categoria) throws Exception {

        direccionEntregaDAO.eliminar(categoria);
    }

    public List<DireccionEntregaEntidad> getListaByBusqueda(String txtBusqueda, String nroCuenta, boolean mostrarDeBaja) {

        return direccionEntregaDAO.getListaByBusqueda(null, txtBusqueda, nroCuenta, mostrarDeBaja, 15);
    }

    public List<DireccionEntregaEntidad> getListaByNroCuenta(String nroCuenta, boolean mostrarDeBaja) {

        return direccionEntregaDAO.getListaByBusqueda(null, "", nroCuenta, mostrarDeBaja, 15);
    }

    public String generarCodigo(EntidadComercial entidad) {

        String codigo = "00" + String.valueOf(entidad.getDireccionesDeEntrega().size() + 1);
        String newCodigo = codigo.substring(codigo.length() - 2, codigo.length());

        return newCodigo;

    }

}
