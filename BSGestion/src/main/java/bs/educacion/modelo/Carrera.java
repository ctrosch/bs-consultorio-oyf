/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.UnidadNegocio;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "ed_carrera")
@EntityListeners(AuditoriaListener.class)
public class Carrera implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;

    @Size(max = 200)
    @Column(name = "TITULO")
    private String titulo;

    @Lob
    @Size(max = 65535)
    @Column(name = "DESCRP")
    private String descripcion;

    @Lob
    @Size(max = 65535)
    @Column(name = "IMAGEN")
    private String imagen;

    @JoinColumn(name = "CODARE", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private AreaEducacion area;

    @JoinColumn(name = "TIPCAR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoCarrera tipoCarrera;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemCarreraSucursal> sucursales;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Arancel> aranceles;

    @Embedded
    private Auditoria auditoria;

    public Carrera() {
        this.auditoria = new Auditoria();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public AreaEducacion getArea() {
        return area;
    }

    public void setArea(AreaEducacion area) {
        this.area = area;
    }

    public TipoCarrera getTipoCarrera() {
        return tipoCarrera;
    }

    public void setTipoCarrera(TipoCarrera tipoCarrera) {
        this.tipoCarrera = tipoCarrera;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public List<ItemCarreraSucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<ItemCarreraSucursal> sucursales) {
        this.sucursales = sucursales;
    }

    public List<Arancel> getAranceles() {
        return aranceles;
    }

    public void setAranceles(List<Arancel> aranceles) {
        this.aranceles = aranceles;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public Carrera clone() throws CloneNotSupportedException {
        return (Carrera) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.codigo);
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
        final Carrera other = (Carrera) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.educacion.modelo.Carrera[ codigo=" + codigo + " ]";
    }

}
