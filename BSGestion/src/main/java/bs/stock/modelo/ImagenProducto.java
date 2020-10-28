/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * @author ctrosch
 */
@Entity
@Table(name = "st_producto_imagen")
@EntityListeners(AuditoriaListener.class)
public class ImagenProducto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Column(name = "NOMBRE", length = 60)
    private String nombre;

    @Column(name = "LINEXT", length = 1)
    private String linkExterno;

    @Column(name = "URLBAS", length = 200)
    private String urlBase;

    @Column(name = "ARCHIV", length = 200)
    private String archivo;

    @NotNull
    @Column(name = "CLASIF", length = 100)
    private String clasificacion;

    @Transient
    private boolean conError;

    @Transient
    private String urlImagen;

    @Embedded
    private Auditoria auditoria;

    public ImagenProducto() {
        this.auditoria = new Auditoria();
        this.linkExterno = "N";
        this.clasificacion = "";
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public String getLinkExterno() {
        return linkExterno;
    }

    public void setLinkExterno(String linkExterno) {
        this.linkExterno = linkExterno;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificación) {
        this.clasificacion = clasificación;
    }

    public String getUrlImagen() {

        this.urlImagen = (urlBase != null ? urlBase : "") + (archivo != null ? archivo : "");
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImagenProducto other = (ImagenProducto) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ImagenProducto{" + "id=" + id + '}';
    }

}
