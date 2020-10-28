/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.modelo;

import bs.administracion.modelo.Modulo;
import bs.administracion.modelo.Reporte;
import bs.administracion.modelo.Vista;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "sg_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CODIGO", nullable = false)
    private String codigo;
    @Column(name = "NOMBRE", length = 45)
    private String nombre;
    @Column(name = "ORDEN")
    private Integer orden;
    @Column(name = "TIPO")
    private Integer tipo;
    @Column(name = "ICONO", length = 80)
    private String icono;
    @Column(name = "ACCION", length = 80)
    private String accion;
    @Column(name = "URL", length = 80)
    private String url;
    @Column(name = "ACTUALIZA", length = 80)
    private String update;
    @Column(name = "ACTIVO")
    private String activo;
    @Column(name = "ONCOMPLETE", length = 45)
    private String oncomplete;
    @Column(name = "NIVEL")
    private Integer nivel;

    @Column(name = "ORIGEN")
    private String origen;

    @Lob
    @Column(name = "AYUDA", length = 2147483647)
    private String ayuda;

    @JoinColumn(name = "MODULO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Modulo modulo;

    @JoinColumn(name = "COD_VISTA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Vista vista;

    @JoinColumn(name = "codrep", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporte;

    @JoinColumn(name = "COD_MENU", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.EAGER)
    private Menu menuPrincipal;

    @OneToMany(mappedBy = "menuPrincipal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("orden ASC")
    private List<Menu> menuItem;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MenuParametro> parametros;

    @Transient
    private List<ItemMenuUsuario> itemMenuUsuario;

    @Transient
    private List<ItemMenuGrupo> itemMenuGrupo;

    @Transient
    private boolean seleccionado;

    public Menu() {

        menuItem = new ArrayList<Menu>();
        this.origen = "USR";
    }

    public Menu(String codigo) {
        this.codigo = codigo;
        menuItem = new ArrayList<Menu>();
        this.origen = "USR";
    }

    public Menu(Menu m) {

        codigo = m.getCodigo();
        nombre = m.getNombre();
        icono = m.getIcono();
        accion = m.getAccion();
        url = m.getUrl();
        update = m.getUpdate();
        activo = m.getActivo();
        oncomplete = m.getOncomplete();
        nivel = m.getNivel();
        orden = m.getOrden();
        tipo = m.getTipo();
        menuItem = new ArrayList<Menu>();
        parametros = m.getParametros();
        origen = m.getOrigen();
        this.origen = "USR";
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {

        if (icono == null) {
            icono = "";
        }
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<Menu> getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(List<Menu> menuItem) {
        this.menuItem = menuItem;
    }

    public Menu getMenuPrincipal() {
        return menuPrincipal;
    }

    public void setMenuPrincipal(Menu menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public List<MenuParametro> getParametros() {
        return parametros;
    }

    public void setParametros(List<MenuParametro> parametros) {
        this.parametros = parametros;
    }

    public String getUrlCompleta() {

        String urlC = url;
        String param = "?TITULO=" + nombre
                + "&ID_MENU=" + codigo
                + (vista != null ? "&CODVIS=" + vista.getCodigo() : "")
                + (reporte != null ? "&CODREP=" + reporte.getCodigo() + "&NOMARC=" + reporte.getNombre().replace(" ", "_") : "");

        if (parametros != null) {

            for (MenuParametro mp : parametros) {

                param += "&" + mp.getNombre() + "=" + mp.getValor();
            }
        }

        return urlC + ".jsf" + param;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public String getAyuda() {
        return ayuda;
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    public Vista getVista() {
        return vista;
    }

    public void setVista(Vista vista) {
        this.vista = vista;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public List<ItemMenuUsuario> getItemMenuUsuario() {
        return itemMenuUsuario;
    }

    public void setItemMenuUsuario(List<ItemMenuUsuario> itemMenuUsuario) {
        this.itemMenuUsuario = itemMenuUsuario;
    }

    public List<ItemMenuGrupo> getItemMenuGrupo() {
        return itemMenuGrupo;
    }

    public void setItemMenuGrupo(List<ItemMenuGrupo> itemMenuGrupo) {
        this.itemMenuGrupo = itemMenuGrupo;
    }

    @Override
    public Menu clone() throws CloneNotSupportedException {
        return (Menu) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menu)) {
            return false;
        }
        Menu other = (Menu) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
