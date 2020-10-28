/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.modelo;

import bs.administracion.modelo.PerfilCuenta;
import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.prestamo.modelo.Promotor;
import bs.produccion.modelo.Operario;
import bs.taller.modelo.Tecnico;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "sg_usuario")
@EntityListeners(AuditoriaListener.class)
public class Usuario implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "USUARIO", length = 15)
    private String usuario;
    @Column(name = "PASSWORD", length = 15)
    private String password;
    @Column(name = "NOMBRE", length = 80)
    private String nombre;
    @Column(name = "EMAIL", length = 80)
    private String email;

    @Column(name = "IMAGEN", length = 80)
    private String imagen;

    @Column(name = "HISVIS", length = 1)
    private String historialVisible;

    @Column(name = "FAVVIS", length = 1)
    private String favoritosVisible;

    @JoinColumn(name = "ID_ESTADO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EstadoUsuario estado;
    @JoinColumn(name = "ID_TIPO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoUsuario tipo;

    @JoinColumn(name = "ID_TECNICO", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tecnico tecnico;

    @JoinColumn(name = "cod_operario", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne
    private Operario operario;

    @JoinColumn(name = "PERCTA", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PerfilCuenta perfilCuentaEmail;

    @Column(name = "ID_ENTIDAD")
    private int idEntidad;

    @JoinColumn(name = "CODPRO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Promotor promotor;

    @JoinColumn(name = "CODPRF", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial profesionalMedico;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemGrupoUsuario> grupos;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemUnidadNegocioUsuario> unidadesNegocio;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemSucursalUsuario> sucursales;

    @Embedded
    private Auditoria auditoria;

    /**
     * @JoinTable(name = "sec_usuario_menu", joinColumns = {
     * @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")},
     * inverseJoinColumns = {
     * @JoinColumn(name = "ID_MENU", referencedColumnName = "ID")})
     * @ManyToMany(cascade = {CascadeType.DETACH,
     * CascadeType.MERGE},fetch=FetchType.EAGER)
     *
     * private Set<Menu> menu = new HashSet<Menu>();
     *
     */
//
//    @Transient
//    private List<ItemMenuUsuario> menu;
    public Usuario() {
        this.auditoria = new Auditoria();
    }

    public Usuario(Integer id) {
        this.id = id;
        this.historialVisible = "N";
        this.favoritosVisible = "N";
        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public List<ItemSucursalUsuario> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<ItemSucursalUsuario> sucursales) {
        this.sucursales = sucursales;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public PerfilCuenta getPerfilCuentaEmail() {
        return perfilCuentaEmail;
    }

    public void setPerfilCuentaEmail(PerfilCuenta perfilCuentaEmail) {
        this.perfilCuentaEmail = perfilCuentaEmail;
    }

    public int getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(int idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemGrupoUsuario> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<ItemGrupoUsuario> grupo) {
        this.grupos = grupo;
    }

    public Operario getOperario() {
        return operario;
    }

    public void setOperario(Operario operario) {
        this.operario = operario;
    }

    public List<ItemUnidadNegocioUsuario> getUnidadesNegocio() {
        return unidadesNegocio;
    }

    public void setUnidadesNegocio(List<ItemUnidadNegocioUsuario> unidadesNegocio) {
        this.unidadesNegocio = unidadesNegocio;
    }

    public Promotor getPromotor() {
        return promotor;
    }

    public void setPromotor(Promotor promotor) {
        this.promotor = promotor;
    }

    public String getHistorialVisible() {
        return historialVisible;
    }

    public void setHistorialVisible(String historialVisible) {
        this.historialVisible = historialVisible;
    }

    public String getFavoritosVisible() {
        return favoritosVisible;
    }

    public void setFavoritosVisible(String favoritosVisible) {
        this.favoritosVisible = favoritosVisible;
    }

    public EntidadComercial getProfesionalMedico() {
        return profesionalMedico;
    }

    public void setProfesionalMedico(EntidadComercial profesionalMedico) {
        this.profesionalMedico = profesionalMedico;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + '}';
    }

}
