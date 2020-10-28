/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.Modulo;
import bs.administracion.modelo.Parametro;
import bs.administracion.modelo.Reporte;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ReporteBean extends GenericBean implements Serializable {

    private List<Reporte> lista;
    private Parametro parametro;
    private String origen;
    private String codigo;
    private String grupo;
    private Modulo modulo;

    private boolean seleccionaTodoVisible;
    private boolean seleccionaTodoSoloLectura;
    private boolean seleccionaTodoRequerido;

    public ReporteBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                parametro = parametrosRN.getParametro("01");

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(reporteRN.getReporte(codigo));
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
        modoVista = "D";
        reporte = new Reporte();
        reporte.setOrigen(parametro.getOrigenPorDefecto());
        obtenerCodigo();
    }

    public void guardar(boolean nuevo) {

        try {
            if (reporte != null) {

                reporte = reporteRN.guardar(reporte, esNuevo);
                esNuevo = false;
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
            if (reporte == null) {
                JsfUtil.addErrorMessage("No hay reporte activo o la lista de parámetros es nula");
                return;
            }

            reporte = reporteRN.duplicar(reporte);
            esNuevo = true;
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            reporte.getAuditoria().setDebaja(habDes);
            reporteRN.guardar(reporte, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            reporteRN.eliminar(reporte);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = reporteRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (modulo != null) {

            filtro.put("modulo.codigo = ", "'" + modulo.getCodigo() + "'");
        }

        if (origen != null && !origen.isEmpty()) {
            filtro.put("origen = ", "'" + origen + "'");
        }

        if (grupo != null && !grupo.isEmpty()) {
            filtro.put("grupo = ", "'" + grupo + "'");
        }
    }

    public void limpiarFiltroBusqueda() {

        txtBusqueda = "";
        mostrarDebaja = false;
        modulo = null;
        origen = null;
        grupo = null;

        buscar();

    }

    public List<Reporte> complete(String query) {
        try {
            lista = reporteRN.getListaByBusqueda(null, query, false, 5);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Reporte>();
        }
    }

    public void onSelect(SelectEvent event) {
        reporte = (Reporte) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Reporte d) {

        if (d == null) {
            return;
        }

        reporte = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (reporte == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void obtenerCodigo() {

        if (reporte == null || reporte.getOrigen() == null) {
            return;
        }

        String codigo = reporteRN.obtenerSiguienteCogido(reporte.getOrigen());
        reporte.setCodigo(codigo);

    }

    @Override
    public void upload(FileUploadEvent event) {

        if (reporte.getModulo() == null) {
            JsfUtil.addErrorMessage("Primero debe seleccionar el módulo");
        }

        try {
            copiarArchivoReporte(event.getFile().getFileName(), event.getFile().getInputstream());
//            JsfUtil.addInfoMessage("El reporte ha sido procesado con éxito");
        } catch (IOException e) {
            System.err.println("Error subiendo archivo " + e);
            JsfUtil.addErrorMessage("No es posible procesar el archivo");
        }
    }

    /**
     * Copiar un archivo a la carpeta temporales de la organizacion
     *
     * @param fileName
     * @param in
     */
    public void copiarArchivoReporte(String fileName, InputStream in) {
        try {

            //String[] split = fileName.split("\\.");
            //String extension = split[split.length - 1].toLowerCase();
            String archivo = reporte.getModulo().getCodigo()
                    + (reporte.getGrupo() == null || reporte.getGrupo().isEmpty() ? "" : File.separator + reporte.getGrupo())
                    + File.separator + fileName;
            File folder = new File(parametro.getPathCarpetaReportes()
                    + reporte.getModulo().getCodigo()
                    + (reporte.getGrupo() == null || reporte.getGrupo().isEmpty() ? "" : File.separator + reporte.getGrupo()));

            //Verificamos carpeta
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }

            File file = new File(parametro.getPathCarpetaReportes() + archivo);

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            reporte.setPath(archivo);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, "Error cargando archivo: " + e);
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public Reporte getReporte() {
        return reporte;
    }

    @Override
    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public List<Reporte> getLista() {
        return lista;
    }

    public void setLista(List<Reporte> lista) {
        this.lista = lista;
    }

    public Parametro getParametro() {
        return parametro;
    }

    public void setParametro(Parametro parametro) {
        this.parametro = parametro;
    }

    public boolean isSeleccionaTodoVisible() {
        return seleccionaTodoVisible;
    }

    public void setSeleccionaTodoVisible(boolean seleccionaTodoVisible) {
        this.seleccionaTodoVisible = seleccionaTodoVisible;
    }

    public boolean isSeleccionaTodoSoloLectura() {
        return seleccionaTodoSoloLectura;
    }

    public void setSeleccionaTodoSoloLectura(boolean seleccionaTodoSoloLectura) {
        this.seleccionaTodoSoloLectura = seleccionaTodoSoloLectura;
    }

    public boolean isSeleccionaTodoRequerido() {
        return seleccionaTodoRequerido;
    }

    public void setSeleccionaTodoRequerido(boolean seleccionaTodoRequerido) {
        this.seleccionaTodoRequerido = seleccionaTodoRequerido;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

}
