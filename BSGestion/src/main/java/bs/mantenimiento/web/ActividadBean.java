/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.mantenimiento.modelo.Actividad;
import bs.mantenimiento.modelo.ActividadActivador;
import bs.mantenimiento.modelo.ActividadAdjunto;
import bs.mantenimiento.modelo.ActividadRecurso;
import bs.mantenimiento.modelo.Subactividad;
import bs.mantenimiento.rn.ActividadRN;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class ActividadBean extends GenericBean implements Serializable {

    private String codigo;
    private Actividad actividad;
    private List<Actividad> lista;

    @EJB
    private ActividadRN actividadRN;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    public ActividadBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(actividadRN.getActividad(codigo));
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
        actividad = new Actividad();
    }

    public void guardar(boolean nuevo) {

        try {
            if (actividad != null) {

                actividad = actividadRN.guardar(actividad, esNuevo);
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

    public void habilitaDeshabilita(String habDes) {

        try {
            actividad.getAuditoria().setDebaja(habDes);
            actividadRN.guardar(actividad, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            actividadRN.eliminar(actividad);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = actividadRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        buscar();

    }

    public List<Actividad> complete(String query) {
        try {

            lista = actividadRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Actividad>();
        }
    }

    public void onSelect(SelectEvent event) {
        actividad = (Actividad) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Actividad a) {

        if (a == null) {
            return;
        }

        actividad = a;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (actividad == null) {
                JsfUtil.addErrorMessage("No hay Actividad activa");
                return;
            }

            actividad = actividadRN.duplicar(actividad);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (actividad == null) {
            JsfUtil.addErrorMessage("No hay Actividad seleccionada, nada para imprimir");
        }
    }

    public void nuevoSubActividad() {

        try {
            actividadRN.nuevoSubActividad(actividad);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void eliminarSubActividad(Subactividad subactividad) {

        try {
            actividadRN.eliminarSubActividad(actividad, subactividad);
            JsfUtil.addWarningMessage("Se ha borrado la SubActividad " + (subactividad.getDescripcion() != null ? subactividad.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (subactividad.getDescripcion() != null ? subactividad.getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoRecurso() {

        try {
            actividadRN.nuevoRecurso(actividad);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void eliminarRecurso(ActividadRecurso actividadRecurso) {

        try {
            actividadRN.eliminarRecurso(actividad, actividadRecurso);
            JsfUtil.addWarningMessage("Se ha borrado el Recurso " + (actividadRecurso.getDescripcion() != null ? actividadRecurso.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (actividadRecurso.getDescripcion() != null ? actividadRecurso.getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoAdjunto() {

        try {
            actividadRN.nuevoAdjunto(actividad);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item: " + ex);
        }
    }

    public void eliminarAdjunto(ActividadAdjunto actividadAdjunto) {

        try {
            actividadRN.eliminarAdjunto(actividad, actividadAdjunto);
            JsfUtil.addWarningMessage("Se ha borrado el Adjunto " + (actividadAdjunto.getDescripcion() != null ? actividadAdjunto.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (actividadAdjunto.getDescripcion() != null ? actividadAdjunto.getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoActivador() {

        try {
            actividadRN.nuevoActivador(actividad);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item: " + ex);
        }
    }

    public void eliminarActivador(ActividadActivador activador) {

        try {
            actividadRN.eliminarActivador(actividad, activador);
            JsfUtil.addWarningMessage("Se ha borrado el activador " + (activador.getDescripcion() != null ? activador.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (activador.getDescripcion() != null ? activador.getDescripcion() : "") + " " + ex);
        }
    }

    public void procesarProducto(ActividadRecurso actividadRecurso) {

        if (actividadRecurso != null && actividadRecurso.getProducto() != null) {

            try {
                actividadRN.asignarProducto(actividadRecurso, actividadRecurso.getProducto());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar producto " + ex);
            }
        }
    }

    @Override
    public void upload(FileUploadEvent event) {

        try {
            copiarArchivoAdjunto(event.getFile().getFileName(), event.getFile().getInputstream());
            JsfUtil.addInfoMessage("La imagen ha sido procesada con éxito");
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
     * @return Path del archivo generado
     */
    public void copiarArchivoAdjunto(String fileName, InputStream in) {
        try {

//            if (imagenProducto == null) {
//                JsfUtil.addErrorMessage("No hay ningún item de imagen seleccionado");
//                return;
//            }
            //nuevoItemImagen();
            nuevoAdjunto();

            //ImagenProducto imagenProducto = producto.getImagenes().get(producto.getImagenes().size() - 1);
            ActividadAdjunto actividadAdjunto = actividad.getAdjuntos().get(actividad.getAdjuntos().size() - 1);

            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");

            String[] split = fileName.split("\\.");
            String extension = split[split.length - 1].toLowerCase();
            String archivo = actividad.getCodigo() + "_" + sdf.format(new Date()) + "_" + JsfUtil.getCadenaAlfanumAleatoria(5) + "." + extension;

            File file = new File(aplicacionBean.getParametro().getPathCarpetaProductos() + archivo);

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

            actividadAdjunto.setUrlBase(aplicacionBean.getParametro().getUrlCarpetaProductos());
            actividadAdjunto.setArchivo(archivo);

            if (!esNuevo) {
                actividadRN.guardar(actividad, esNuevo);
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Error cargando archivo: " + e);
        }
    }

    /*
    //---------------------------------------------------------------------------
    //IMAGENES
    //---------------------------------------------------------------------------
    public void nuevoItemImagen() {

        try {

            productoRN.nuevoItemImagen(producto);

        } catch (ExcepcionGeneralSistema ex) {

            JsfUtil.addErrorMessage("No es posible agregar item: " + ex);
        }
    }

    public void eliminarItemImagen(ImagenProducto itemImagen) {

        try {
            productoRN.eliminarItemImagen(producto, itemImagen);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (itemImagen != null ? itemImagen.getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemImagen != null ? itemImagen.getNombre() : "") + " " + ex);
        }
    }
     */
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public List<Actividad> getLista() {
        return lista;
    }

    public void setLista(List<Actividad> lista) {
        this.lista = lista;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

}
