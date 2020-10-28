/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.EmpresaDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Empresa;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import java.io.Serializable;
import java.util.ArrayList;
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
public class EmpresaRN implements Serializable {

    @EJB
    private EmpresaDAO empresaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Empresa guardar(Empresa empresa, boolean esNuevo) throws Exception {
        reorganizarNroItem(empresa);
        if (esNuevo) {

            if (empresaDAO.getObjeto(Empresa.class, empresa.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un empresa con ese id " + empresa.getCodigo());
            }

            empresaDAO.crear(empresa);

        } else {

            empresa = (Empresa) empresaDAO.editar(empresa);

        }

        return empresa;

    }

    public void eliminar(Empresa empresa) throws Exception {

        empresaDAO.eliminar(empresa);

    }

    public Empresa duplicar(Empresa a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Empresa nulo, nada para duplicar");
        }

        Empresa empresa = a.clone();
        empresa.setCodigo("");
        empresa.setNombreFantasia(a.getNombreFantasia() + "( Duplicado)");

        return empresa;
    }

    public Empresa getEmpresa(String id) {

        return empresaDAO.getEmpresa(id);
    }

    public List<Empresa> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return empresaDAO.getEmpresaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public String getProximoCodigo() {
        int nroCta = empresaDAO.getMaxCodigo();

        String nroCodigo = "00000000" + String.valueOf(nroCta);
        return nroCodigo.substring(nroCodigo.length() - 5, nroCodigo.length());

    }

    public void reorganizarNroItem(Empresa empresa) {

        //Reorganizamos los números de items
        int i;

    }

    public void nuevoSucursal(Empresa empresa) throws ExcepcionGeneralSistema {

        if (empresa == null) {
            throw new ExcepcionGeneralSistema("No existe un Empresa seleccionado para agregarle una sucursal");
        }

        if (empresa.getSucursales() == null) {
            empresa.setSucursales(new ArrayList<Sucursal>());
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setEmpresa(empresa);

        empresa.getSucursales().add(sucursal);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eliminarSucursal(Empresa empresa, Sucursal sucursal) throws Exception {

        empresaDAO.eliminar(Sucursal.class, sucursal.getCodigo());
        empresa.getSucursales().remove(sucursal);
    }

    public void nuevoUnidadNegocio(Empresa empresa) throws ExcepcionGeneralSistema {

        if (empresa == null) {
            throw new ExcepcionGeneralSistema("No existe un Empresa seleccionado para agregarle una unidad de negocio");
        }

        if (empresa.getUnidadesNegocios() == null) {
            empresa.setUnidadesNegocios(new ArrayList<UnidadNegocio>());
        }

        UnidadNegocio unidadNegocio = new UnidadNegocio();
        unidadNegocio.setEmpresa(empresa);

        empresa.getUnidadesNegocios().add(unidadNegocio);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eliminarUnidadNegocio(Empresa empresa, UnidadNegocio unidadNegocio) throws Exception {

        empresaDAO.eliminar(Sucursal.class, unidadNegocio.getCodigo());
        empresa.getSucursales().remove(unidadNegocio);
    }

}
