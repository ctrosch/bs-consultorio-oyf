/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.global.auditoria.AuditoriaBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.EmpresaRN;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.global.web.PrincipalBean;
import bs.seguridad.modelo.ItemSucursalUsuario;
import bs.seguridad.modelo.ItemUnidadNegocioUsuario;
import bs.seguridad.modelo.Usuario;
import bs.seguridad.rn.UsuarioRN;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.menu.MenuItem;

/**
 *
 * @author Caio
 */
//@ManagedBean
//@SessionScoped
@Named
@SessionScoped
public class UsuarioSessionBean extends GenericBean implements Serializable {

    @EJB
    private UsuarioRN usuarioRN;
    @EJB
    private EmpresaRN empresaRN;
    @EJB
    private MailFactory mailFactoryBean;

    @Inject
    AuditoriaBean auditoriaBean;

    private String nombreUsuario;
    private String claveUsuario;

    boolean estaRegistrado;
    private String txtRecupero;
    private Usuario usuario;
    private List<MenuItem> menuUsuario;
    private int cantIntentos;
    private boolean muestroMensajeExpirado;

    @Inject
    protected PrincipalBean principalBean;

    @Inject
    protected AplicacionBean aplicacionBean;

    public UsuarioSessionBean() {

    }

    @PostConstruct
    public void init() {

        setUsuario(new Usuario());
        estaRegistrado = false;
        cantIntentos = 0;
    }

    @PreDestroy
    public void destroy() {

        if (usuario == null) {
            return;
        }

        if (aplicacionBean.getUsuarioLogueados().contains(usuario)) {
            aplicacionBean.getUsuarioLogueados().remove(usuario);
        }
    }

    public void guardar() {

        try {
            if (usuario != null) {

                usuario = usuarioRN.guardar(usuario);
                esNuevo = false;

                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public Usuario getUsuario() {

        if (usuario == null) {
            usuario = new Usuario();
        }
        return usuario;
    }

    /**
     *
     * @throws IOException Valida que la sesión esté iniciada, de lo contrario
     * abre un dialogo para informar, luego redirecciona al inicio
     */
    public void checkExpiredView() throws IOException {

        if (this.usuario == null || this.usuario.getUsuario() == null) {
            //ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            //context.redirect(context.getRequestContextPath() + "/inicio");
            PrimeFaces.current().executeScript("PF('expiredDialog').show()");
        }
    }

    /**
     * @throws IOException Valida que la sesión esté iniciada, de lo contrario
     * abre un dialogo para informar, luego se cierra la ventana.
     */
    public void checkExpiredAndClose() throws IOException {

        if (this.usuario == null || this.usuario.getUsuario() == null) {
            //ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            //context.redirect(context.getRequestContextPath() + "/inicio");
            PrimeFaces.current().executeScript("PF('expiredDialogAndClose').show()");
        }
    }

    /**
     *
     * @throws IOException Se ejecuta en global/inicio.xhtml. Si la sesión está
     * iniciada redirecciona al principal.
     */
    public void checkInicio() throws IOException {

        if (this.usuario != null && this.usuario.getUsuario() != null) {

            if (estaRegistrado) {
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                context.redirect(context.getRequestContextPath() + "/principal");
            }
        }
    }

    public void eliminarImage() {

        usuario.setImagen(null);
        guardar();

    }

    public void oncapture(CaptureEvent captureEvent) {
        //filename = getRandomImageName();
        byte[] data = captureEvent.getData();

        String archivo = usuario.getUsuario() + ".jpg";
        File file = new File(aplicacionBean.getParametro().getPathCarpetaUsuarios() + archivo);

        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(file);
            imageOutput.write(data, 0, data.length);
            imageOutput.close();

            usuario.setImagen(archivo);
            guardar();
        } catch (IOException e) {
            throw new FacesException("Errro al obtener imagen", e);
        }
    }

    @Override
    public void upload(FileUploadEvent event) {

        try {
            copiarArchivoImagen(event.getFile().getFileName(), event.getFile().getInputstream());
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
    public void copiarArchivoImagen(String fileName, InputStream in) {
        try {

            String[] split = fileName.split("\\.");
            String extension = split[split.length - 1].toLowerCase();
            String archivo = usuario.getUsuario() + "." + extension;

            File file = new File(aplicacionBean.getParametro().getPathCarpetaUsuarios() + archivo);

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

            usuario.setImagen(archivo);
            guardar();

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Error cargando imagen archivo: " + e);
        }
    }

    public String getStringInUnidadesNegocio() {

        String sFiltro = "";

        if (usuario.getUnidadesNegocio() != null) {

            for (ItemUnidadNegocioUsuario iu : usuario.getUnidadesNegocio()) {

                if (sFiltro.isEmpty()) {
                    sFiltro = "'" + iu.getUnidadNegocio().getCodigo() + "'";
                } else {
                    sFiltro = sFiltro + ",'" + iu.getUnidadNegocio().getCodigo() + "'";
                }
            }
        }

        return sFiltro;
    }

    public String getStringInSucursal() {

        String sFiltro = "";

        if (usuario.getSucursales() != null) {

            for (ItemSucursalUsuario iu : usuario.getSucursales()) {

                if (sFiltro.isEmpty()) {
                    sFiltro = "'" + iu.getSucursal().getCodigo() + "'";
                } else {
                    sFiltro = sFiltro + ",'" + iu.getSucursal().getCodigo() + "'";
                }
            }
        }

        return sFiltro;
    }

    //----------------------------------------------------------------------
    public void login() throws IOException {

        Usuario usuAux = null;
        try {
            //usuAux = usuarioDAO.getUsuarioByEmail(usuario.getEmail());
            usuAux = usuarioRN.getUsuarioByNombre(nombreUsuario);

        } catch (Exception e) {
        }

        //Valida que exista el usuario
        if (usuAux == null) {
            JsfUtil.addErrorMessage("El usuario '" + nombreUsuario + "' no se encuentra registrado");
            return;
        }

        cantIntentos++;

        if (cantIntentos == 7) {

            recuperarClave();

            JsfUtil.addErrorMessage("Se ha enviado información de acceso a la cuenta de correo asociada al usario.");
            JsfUtil.addErrorMessage("Verifique su bandeja de entrada y vuelva a intentar");
            cantIntentos = 0;
            return;
        }

        //Valida que no se encuentre inactivo
        if (usuAux.getEstado().getDescripcion().equals("Inactivo")) {
            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' se encuentra " + usuAux.getEstado().getDescripcion());
            return;
        }

        //Valida que no se encuentre bloqueado
        if (usuAux.getEstado().getDescripcion().equals("Bloqueado")) {
            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' se encuentra " + usuAux.getEstado().getDescripcion());
            return;
        }

        //Valida que no se encuentre desabilitado
        if (usuAux.getAuditoria().getDebaja().equals("S")) {
            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' se encuentra desabilitado");
            return;
        }

        //Valida que no se encuentre desabilitado
        if (usuAux.getTipo().getId() != 1 && (usuAux.getSucursales() == null || usuAux.getSucursales().isEmpty())) {
            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' debe tener asignada al menos una sucursal");
            return;
        }

//        //Valida que no se encuentre logueado
//        if (aplicacionBean.getUsuarioLogueados().contains(usuAux)) {
//            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' ya se encuentra logueado en la plataforma");
//            return "";
//        }
        //Validamos la contraseña
        if (aplicacionBean.getParametro().getTipoLogin().equals("N")
                && claveUsuario != null
                && !claveUsuario.equals(usuAux.getPassword())) {

            JsfUtil.addErrorMessage("Password incorrecto");
            getUsuario().setPassword("");
            return;
        }

        try {
            principalBean.cargarMenu(usuAux);
            principalBean.setEmpresa(empresaRN.getEmpresa("01"));

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No puede ingresar en este momento " + ex);
            return;
        }

        usuario = usuAux;
        estaRegistrado = true;
        cantIntentos = 0;

        auditoriaBean.setearDatos("Organizacion", usuario.getUsuario());

        FacesContext contexta = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) contexta.getExternalContext().getSession(false);
        //System.err.println("session " + session.getMaxInactiveInterval());
        session.setMaxInactiveInterval(480 * 60);
        //System.err.println("session " + session.getMaxInactiveInterval());

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + "/principal");

    }

    public void logout() throws IOException {

        setUsuario(null);
        setUsuario(new Usuario());
        estaRegistrado = false;

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + "/inicio");

//        session.invalidate();
//        return "../global/inicio.xhtml";
    }

    public void invalidarSession() {

        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        System.out.println("Usuario logout: " + usuario.getUsuario() + " Hora: " + new Date());

        if (aplicacionBean.getUsuarioLogueados().contains(usuario)) {
            aplicacionBean.getUsuarioLogueados().remove(usuario);
        }
        usuario = null;
        usuario = new Usuario();
        estaRegistrado = false;
        session.invalidate();
        HttpSession sessionNew = (HttpSession) context.getExternalContext().getSession(true);
    }

    //--------------------------------------------------------------------------
//    public void identificacion() {
//
//        Usuario usuAux = null;
//        try {
//            //usuAux = usuarioDAO.getUsuarioByEmail(usuario.getEmail());
//            usuAux = usuarioRN.getUsuarioByNombre(nombreUsuario);
//
//        } catch (Exception e) {
//        }
//
//        //Valida que exista el usuario
//        if (usuAux == null) {
//            JsfUtil.addErrorMessage("El usuario '" + nombreUsuario + "' no se encuentra registrado");
//            return;
//        }
//
//        cantIntentos++;
//
//        //Valida que no se encuentre inactivo
//        if (usuAux.getEstado().getDescripcion().equals("Inactivo")) {
//            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' se encuentra " + usuAux.getEstado().getDescripcion());
//            System.out.println("Usuario inactivo");
//            return;
//        }
//
//        //Valida que no se encuentre bloqueado
//        if (usuAux.getEstado().getDescripcion().equals("Bloqueado")) {
//            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' se encuentra " + usuAux.getEstado().getDescripcion());
//            return;
//        }
//
//        //Valida que no se encuentre desabilitado
//        if (usuAux.getAuditoria().getDebaja().equals("S")) {
//            JsfUtil.addErrorMessage("El usuario '" + usuAux.getUsuario() + "' se encuentra desabilitado");
//            return;
//        }
//
//        usuario = usuAux;
//        estaRegistrado = true;
//
//        principalBean.cargarMenu(usuario);
//
//
//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//        if (this.usuario.getUsuario() == null) {
//            try {
//                context.redirect("/");
//            } catch (IOException ex) {
//                Logger.getLogger(UsuarioSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
//    public void desIdentificar() {
//
//        if (aplicacionBean.getUsuarioLogueados().contains(usuario)) {
//            aplicacionBean.getUsuarioLogueados().remove(usuario);
//        }
//
//        setUsuario(null);
//        setUsuario(new Usuario());
//        estaRegistrado = false;
//
//        principalBean.getModelo().getElements().clear();
//        principalBean.limpiarUsuario();
//
//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//        if (this.usuario.getUsuario() == null) {
//            try {
//                context.redirect("/bs-hormitec" );
//            } catch (IOException ex) {
//                Logger.getLogger(UsuarioSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    public void recuperarClave() throws IOException {

        Usuario usuAux = null;

        try {
            usuAux = usuarioRN.getUsuarioByNombre(txtRecupero);

            if (usuAux == null) {
                usuAux = usuarioRN.getUsuarioByEmail(txtRecupero);
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para buscar usuario " + e);
        }

        //Valida que exista el usuario
        if (usuAux == null) {
            JsfUtil.addErrorMessage("El usuario o email no se encuentra registrado");
            return;
        }

        usuario = usuAux;

        if (usuAux.getEmail() == null || usuAux.getEmail().isEmpty()) {
            JsfUtil.addInfoMessage("El usuario no tiene asociada una cuenta de e-mail");
            return;
        }

        //Si todo está bien comienza el proceso recupero de clave
        //renovarClave();
        //Enviamos el e-mail.
        informeEnvioClaveUsuario();

        cantIntentos = 0;

    }

    public void informeEnvioClaveUsuario() {

        List<String> parrafos = new ArrayList<String>();
        parrafos.add("Estimado usuario:");
        parrafos.add("De acuerdo a su solicitud, enviamos su clave de acceso.");

        parrafos.add("Sus datos de acceso:");
        parrafos.add("Nombre de usuario: " + usuario.getUsuario());
        parrafos.add("Nueva clave: " + usuario.getPassword());
        parrafos.add("");

        CorreoElectronico ce = new CorreoElectronico(
                "Solicitud de clave",
                usuario.getEmail(),
                mailFactoryBean.generarMensaje("Solicitud de clave", parrafos));

        if (mailFactoryBean.enviarCorreoElectronico(ce)) {
            JsfUtil.addInfoMessage("Se ha enviado un correo electrónico a la casilla de e-mail asociada al usuario", "");
        } else {
            JsfUtil.addWarningMessage("Disculpe las molestias, actualmente no podemos procesar su solicitud", "");
        }
    }

    public void toggleFavoritos() {

        if (usuario.getFavoritosVisible().equals("S")) {
            usuario.setFavoritosVisible("N");
        } else {
            usuario.setFavoritosVisible("S");
        }

        try {
            usuario = usuarioRN.guardar(usuario);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void toggleHistorial() {

        if (usuario.getHistorialVisible().equals("S")) {
            usuario.setHistorialVisible("N");
        } else {
            usuario.setHistorialVisible("S");
        }

        try {
            usuario = usuarioRN.guardar(usuario);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void enviarInformeError() {

    }

    //------------------------------------------------------------------------
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<MenuItem> getMenuUsuario() {
        return menuUsuario;
    }

    public void setMenuUsuario(List<MenuItem> menuUsuario) {
        this.menuUsuario = menuUsuario;
    }

    public boolean isEstaRegistrado() {
        return estaRegistrado;
    }

    public void setEstaRegistrado(boolean estaRegistrado) {
        this.estaRegistrado = estaRegistrado;
    }

    public boolean isMuestroMensajeExpirado() {
        return muestroMensajeExpirado;
    }

    public void setMuestroMensajeExpirado(boolean muestroMensajeExpirado) {
        this.muestroMensajeExpirado = muestroMensajeExpirado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getTxtRecupero() {
        return txtRecupero;
    }

    public void setTxtRecupero(String txtRecupero) {
        this.txtRecupero = txtRecupero;
    }

    public boolean esAdministrador() {
        try {
            return estaRegistrado
                    && usuario != null
                    && usuario.getTipo().getId() == 1;
        } catch (Exception e) {
            return false;
        }
    }

}
