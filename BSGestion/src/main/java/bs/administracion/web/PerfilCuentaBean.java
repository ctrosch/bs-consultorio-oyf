/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.PerfilCuenta;
import bs.administracion.rn.PerfilCuentaRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class PerfilCuentaBean extends GenericBean implements Serializable {

    private PerfilCuenta perfilCuenta;
    private List<PerfilCuenta> lista;
    private Integer id;

    private String destinatarioPrueba;

    @EJB
    private PerfilCuentaRN perfilCuentaRN;
    @EJB
    private MailFactory mailFactory;

    public PerfilCuentaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (id != null && id > 0) {
                    seleccionar(perfilCuentaRN.getPerfilCuenta(id));
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
        perfilCuenta = new PerfilCuenta();
        modoVista = "D";
        perfilCuenta.setId(perfilCuentaRN.getProximoId());

    }

    public void guardar(boolean nuevo) {

        try {
            if (perfilCuenta != null) {

                perfilCuenta = perfilCuentaRN.guardar(perfilCuenta, esNuevo);
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }

    }

    public void duplicar() {

        try {
            if (perfilCuenta == null) {
                JsfUtil.addErrorMessage("No hay perfil activo");
                return;
            }

            perfilCuenta = perfilCuentaRN.duplicar(perfilCuenta);
            perfilCuenta.setId(perfilCuentaRN.getProximoId());
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            perfilCuenta.getAuditoria().setDebaja(habDes);
            perfilCuenta = perfilCuentaRN.guardar(perfilCuenta, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {

        try {
            perfilCuentaRN.eliminar(perfilCuenta);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = perfilCuentaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 0);
        modoVista = "B";
    }

    public List<PerfilCuenta> complete(String query) {

        try {
            lista = perfilCuentaRN.getListaByBusqueda(filtro, query, false, 0);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<PerfilCuenta>();
        }
    }

    public void onSelect(SelectEvent event) {

        perfilCuenta = (PerfilCuenta) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(PerfilCuenta d) {

        if (d == null) {
            return;
        }

        perfilCuenta = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (perfilCuenta == null) {
            JsfUtil.addErrorMessage("No hay Perfil de Cuenta Email seleccionada, nada para imprimir");
        }
    }

    public void test() {

    }

    public void envioMailPruebaHTML() {

        if (destinatarioPrueba == null || destinatarioPrueba.isEmpty()) {
            JsfUtil.addErrorMessage("Debe ingresar un correo para el destinatario de las pruebas");
        }

        try {
            mailFactory.envioPruebaHTML(perfilCuenta, destinatarioPrueba);
            JsfUtil.addInfoMessage("Se ha enviado un correo electrónico a la casilla confugarada");
        } catch (Exception e) {
            JsfUtil.addErrorMessage("No es posible enviar el correo de pruebas: " + e);
        }
    }

    public PerfilCuenta getPerfilCuenta() {
        return perfilCuenta;
    }

    public void setPerfilCuenta(PerfilCuenta perfilCuenta) {
        this.perfilCuenta = perfilCuenta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<PerfilCuenta> getLista() {
        return lista;
    }

    public void setLista(List<PerfilCuenta> lista) {
        this.lista = lista;
    }

    public String getDestinatarioPrueba() {
        return destinatarioPrueba;
    }

    public void setDestinatarioPrueba(String destinatarioPrueba) {
        this.destinatarioPrueba = destinatarioPrueba;
    }

}
