/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.wsafip.modelo.WebServices;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Claudio
 */
@Entity
@Table(name = "gr_punto_venta")
@EntityListeners(AuditoriaListener.class)
public class PuntoVenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Column(name = "IMPLFE", nullable = false, length = 1)
    private String implementaFE;

    @JoinColumn(name = "SOPELE", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private WebServices webservice;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @Embedded
    private Auditoria auditoria;

    public PuntoVenta() {

        this.auditoria = new Auditoria();
        this.implementaFE = "N";
    }

    public PuntoVenta(String sucurs) {
        this.codigo = sucurs;
        this.implementaFE = "N";
    }

    public PuntoVenta(String sucurs, String descrp) {
        this.codigo = sucurs;
        this.descripcion = descrp;
        this.implementaFE = "N";
    }

    public String getDescrp() {
        return descripcion;
    }

    public void setDescrp(String descrp) {
        this.descripcion = descrp;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public WebServices getWebservice() {
        return webservice;
    }

    public void setWebservice(WebServices webservice) {
        this.webservice = webservice;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getImplementaFE() {
        return implementaFE;
    }

    public void setImplementaFE(String implementaFE) {
        this.implementaFE = implementaFE;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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
        if (!(object instanceof PuntoVenta)) {
            return false;
        }
        PuntoVenta other = (PuntoVenta) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tv.global.modelo.PuntoVenta[codigo=" + codigo + "]";
    }

}
