/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.web.informes;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.produccion.web.MovimientoProduccionBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.tarea.modelo.TareaOperario;
import bs.tarea.rn.TareaOperarioRN;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ParteDiarioBean extends InformeBase implements Serializable {

    @EJB
    private TareaOperarioRN tareaOperarioRN;

    private TareaOperario tareaOperario;
    private List<TareaOperario> lista;

    @ManagedProperty(value = "#{usuarioSessionBean}")
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{movimientoProduccionBean}")
    protected MovimientoProduccionBean movimientoProduccionBean;

    public ParteDiarioBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        buscar();
    }

    public boolean validarParametros() {

//        if (formulario == null) {
//            JsfUtil.addWarningMessage("Seleccione un formulario");
//            return false;
//        }
        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }

        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (formulario != null) {
            filtro.put("formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (numeroFormularioDesde != null) {

            filtro.put("numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {

            filtro.put("numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        lista = tareaOperarioRN.getListaByBusqueda(filtro, mostrarDebaja, cantidadRegistros);

        if (lista == null || lista.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
    }

    public void onSelect(SelectEvent event) {
        tareaOperario = (TareaOperario) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(TareaOperario u) {

        if (u == null) {
            return;
        }

        tareaOperario = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (tareaOperario == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void onSelectArea() {

    }

    public void procesarProducto() {

    }

    public void procesarProyecto() {

    }

    public void resetParametros() {

        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        lista = null;

    }

    //--------------------------------------------------------------------------
    public TareaOperario getTareaOperario() {
        return tareaOperario;
    }

    public void setTareaOperario(TareaOperario tareaOperario) {
        this.tareaOperario = tareaOperario;
    }

    public List<TareaOperario> getLista() {
        return lista;
    }

    public void setLista(List<TareaOperario> lista) {
        this.lista = lista;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public MovimientoProduccionBean getMovimientoProduccionBean() {
        return movimientoProduccionBean;
    }

    public void setMovimientoProduccionBean(MovimientoProduccionBean movimientoProduccionBean) {
        this.movimientoProduccionBean = movimientoProduccionBean;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
