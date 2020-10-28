/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.global.modelo.Sucursal;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.TipoCuentaTesoreria;
import bs.tesoreria.rn.CuentaTesoreriaRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CuentaTesoreriaBean extends GenericBean implements Serializable {

    private CuentaTesoreria cuentaTesoreria;
    private List<CuentaTesoreria> lista;
    private TipoCuentaTesoreria tipoCuenta;
    private String CODIGO;

    @EJB
    private CuentaTesoreriaRN cuentaTesoreriaRN;

    @ManagedProperty(value = "#{tipoCuentaTesoreriaBean}")
    private TipoCuentaTesoreriaBean tipoCuentaTesoreriaBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    // Variables para filtros
    private Sucursal sucursal;

    /**
     * Creates a new instance of BancoBean
     */
    public CuentaTesoreriaBean() {

    }

    /* @PostConstruct
    public void init() {

        tipoCuentaTesoreriaBean.buscar();
        txtBusqueda = "";
        mostrarDebaja = false;

        buscar();
    }
     */
    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(cuentaTesoreriaRN.getCuentaTesoreria(CODIGO));
                } else {
                    buscar();
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        modoVista = "D";
        cuentaTesoreria = new CuentaTesoreria();
    }

    public void guardar(boolean nuevo) {

        try {
            if (cuentaTesoreria != null) {

                cuentaTesoreriaRN.guardar(cuentaTesoreria, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            cuentaTesoreria.getAuditoria().setDebaja(habDes);
            cuentaTesoreriaRN.guardar(cuentaTesoreria, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            cuentaTesoreriaRN.eliminar(cuentaTesoreria);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        String codTipo = "";

        if (tipoCuenta != null) {
            codTipo = tipoCuenta.getCodigo();
        }

        lista = cuentaTesoreriaRN.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        // Filtramos solo alumnos por unidad de negocio a la que pertenece el usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
        }

        if (sucursal != null) {

            filtro.put("sucursal.codigo = ", "'" + sucursal.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        tipoCuenta = null;
        sucursal = null;

        buscar();

    }

    public List<CuentaTesoreria> complete(String query) {

        cargarFiltroBusqueda();

        String codTipo = "";

        if (tipoCuenta != null) {
            codTipo = tipoCuenta.getCodigo();
        }

        try {
            lista = cuentaTesoreriaRN.getListaByBusqueda(codTipo, filtro, query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CuentaTesoreria>();
        }
    }

    public void onSelect(SelectEvent event) {
        cuentaTesoreria = (CuentaTesoreria) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(CuentaTesoreria o) {

        if (o == null) {
            return;
        }

        cuentaTesoreria = o;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (cuentaTesoreria == null) {
                JsfUtil.addErrorMessage("No hay Cuenta Tesoreria activa");
                return;
            }

            cuentaTesoreria = cuentaTesoreriaRN.duplicar(cuentaTesoreria);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (cuentaTesoreria == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public List<CuentaTesoreria> getLista() {
        return lista;
    }

    public void setLista(List<CuentaTesoreria> lista) {
        this.lista = lista;
    }

    public TipoCuentaTesoreria getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuentaTesoreria tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public TipoCuentaTesoreriaBean getTipoCuentaTesoreriaBean() {
        return tipoCuentaTesoreriaBean;
    }

    public void setTipoCuentaTesoreriaBean(TipoCuentaTesoreriaBean tipoCuentaTesoreriaBean) {
        this.tipoCuentaTesoreriaBean = tipoCuentaTesoreriaBean;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

}
