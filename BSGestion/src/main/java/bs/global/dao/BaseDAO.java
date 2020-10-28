/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

/**
 *
 * @author ctrosch
 */
import bs.administracion.dao.ParametroDAO;
import bs.global.auditoria.AuditoriaBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class BaseDAO implements Serializable {

    @EJB
    protected ParametroDAO parametroDAO;

    @Inject
    private AuditoriaBean datosAuditoriaBean;

    protected Logger log = Logger.getLogger(this.getClass().getName());
    protected Map<String, String> filtro;

    @PersistenceContext(unitName = "BS_PU")
    private EntityManager em;

//    @PersistenceContext(unitName = "BS_CORE")
//    private EntityManager emCore;
    public EntityManager getEm() {

//        if(unitName.equals("BS_PU"))  return this.em;
//        if(unitName.equals("BS_001")) return this.em001;
//        if(unitName.equals("BS_001")) return this.em002;
//        if(unitName.equals("BS_001")) return this.em003;
//        if(unitName.equals("BS_001")) return this.em004;
//        if(unitName.equals("BS_001")) return this.em005;
//        if(unitName.equals("BS_001")) return this.em006;
//        if(unitName.equals("BS_001")) return this.em007;
//        if(unitName.equals("BS_001")) return this.em008;
//        if(unitName.equals("BS_001")) return this.em009;
//        if(unitName.equals("BS_001")) return this.em010;
        return this.em;
    }

    public void setEm(EntityManager em) {

        this.em = em;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public void setFiltro(Map<String, String> filtro) {
        this.filtro = filtro;
    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void crear(Object objeto) {

        getEm().persist(objeto);
        em.flush();
    }

    public Object editar(Object objeto) {

//        System.err.println("usuarioSessionBean " + usuarioSessionBean.getUsuario());
        Object objetoNew = getEm().merge(objeto);
        em.flush();
        return objetoNew;
    }

    public void refresh(Object objeto) {

        em.refresh(objeto);

    }

    /**
     * Eliminar objeto con clave primaria Integer
     *
     * @param entityClass Clase del objeto a eliminar
     * @param id Valor de la clave primaria
     */
    public void eliminar(Class entityClass, Integer id) {

        em.flush();

        Object object = getEm().find(entityClass, id);

        if (object != null) {
            em.remove(object);
        }
    }

    /**
     * Eliminar objeto pasando el objeto persistente
     *
     * @param objeto Objeto a eliminar
     * @throws java.lang.Exception
     *
     */
    public void eliminar(Object objeto) throws Exception {

        try {
            em.flush();
            em.remove(objeto);
        } catch (Exception e) {

            System.out.println("No se puede eliminar: " + e);
        }
    }

    /**
     * Eliminar objeto con clave primaria Short
     *
     * @param entityClass Clase del objeto a eliminar
     * @param id Valor de la clave primaria
     */
    public void eliminar(Class entityClass, Short id) {
        try {
            em.flush();
            em.remove(getEm().find(entityClass, id));
        } catch (Exception e) {
            System.out.println("No se puede eliminar: " + entityClass.getName());
        }
    }

    /**
     * Eliminar objeto con clave primaria String
     *
     * @param entityClass Clase del objeto a eliminar
     * @param cod
     * @param id Valor de la clave primaria
     */
    public void eliminar(Class entityClass, String cod) {
        try {
            em.remove(getEm().find(entityClass, cod));
        } catch (Exception e) {
            System.out.println("No se puede eliminar: " + entityClass.getName());
        }
    }

    /**
     * Eliminar objeto con clave primaria object
     *
     * @param entityClass Clase del objeto a eliminar
     * @param id Valor de la clave primaria
     */
    public void eliminar(Class entityClass, Object id) throws Exception {
        try {
            em.remove(getEm().find(entityClass, id));
            em.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se puede eliminar: " + entityClass.getName());

        }
    }

    public <T extends Object> T getObjeto(Class<T> entityClass, Integer id) {
        try {

            return (T) getEm().find(entityClass, id);
        } catch (NoResultException nre) {

            return null;

        } catch (Exception e) {
            System.out.println("No se puede obtener objeto: " + entityClass.getSimpleName() + " - id " + id + " - " + e.getMessage());
            return null;
        }
    }

    public <T extends Object> T getObjeto(Class<T> entityClass, Object id) {
        try {
            return (T) getEm().find(entityClass, id);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("No se puede obtener objeto: " + entityClass.getSimpleName() + " - id " + id + " - " + e.getMessage());
            return null;
        }
    }

    public <T extends Object> T getObjeto(Class<T> entityClass, String cod) {
        try {

            if (cod == null || cod.isEmpty()) {
                return null;
            }

            return (T) getEm().find(entityClass, cod);

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            System.out.println("No se puede obtener objeto: " + entityClass.getSimpleName() + " - cod " + cod + " - " + e.getMessage());
            return null;
        }
    }

    public <T extends Object> T getObjeto(Class<T> entityClass, BigDecimal cod) {
        try {
            return (T) getEm().find(entityClass, cod);
        } catch (Exception e) {
            System.out.println("No se puede obtener objeto: " + entityClass.getSimpleName() + " - cod " + cod + " - " + e.getMessage());
            return null;
        }
    }

    public <T extends Object> T getObjeto(Class<T> entityClass, Short id) {
        try {
            return (T) getEm().find(entityClass, id);
        } catch (Exception e) {
            System.out.println("No se puede obtener objeto: " + entityClass.getSimpleName() + " - id " + id + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Se obtiene una entidad de acuerdo a un tipo de valor específico para un
     * campo determinado
     *
     * @param <T>
     * @param entityClass: Clase de objeto a obtener
     * @param nombreCampo: Nombre del campo por el cual se buscarán
     * coincidencias
     * @param valorCampo: Valor del campo a obtener
     * @return un objeto del tipo entityClass
     */
    public <T extends Object> T getObjeto(Class<T> entityClass, String nombreCampo, String valorCampo) {

        try {

            Query q = getEm().createQuery("SELECT o FROM " + entityClass.getSimpleName() + " o WHERE o." + nombreCampo + " ='" + valorCampo + "'");
            q.setMaxResults(1);
            return (T) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("No se puede obtener objeto " + entityClass.getSimpleName() + " | " + e.getMessage());
            System.err.println("Verificar parametros - Clase - nombre del campo:" + nombreCampo + " | valor buscado:" + valorCampo);
            e.printStackTrace();
            return null;
        }
    }

    public <T extends Object> T getObjeto(Class<T> entityClass, Map<String, String> filtro) {

        try {
            String sQuery = "SELECT o FROM " + entityClass.getSimpleName() + " o "
                    + generarStringFiltro(filtro, "o", true);

            Query q = getEm().createQuery(sQuery);

            q.setMaxResults(1);

            return (T) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;

        } catch (Exception e) {
            System.err.println("No se puede obtener objeto " + entityClass.getSimpleName() + " - " + e);
            System.err.println("Verificar parametros - " + filtro);
            return null;
        }
    }

    /**
     * Consulta todos los registro de una entidad particular
     *
     * @param <T>
     * @param entityClass: Clase de la entidad a consultar
     * @param all: indica si se devuelven todos los registro
     * @param maxResults: cantidad de registros a devolver
     * @param maxResults: posición en la lista a partir del cual comienza a
     * devolver datos
     * @return lista de Entidad del tipo entityClass
     */
    public List getLista(Class entityClass, boolean all, int maxResults, int firstResult) {
        try {
            Query q = (Query) getEm().createQuery("SELECT o FROM " + entityClass.getSimpleName() + " o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {

            log.log(Level.SEVERE, "getLista", "No se puede obtener la lista de " + entityClass.getName());
            log.log(Level.SEVERE, "getLista", e);
            return null;
        }
    }

    protected List getLista(Class entityClass, boolean all, int maxResults, int firstResult, String campoOrdena) {
        try {
            Query q = (Query) getEm().createQuery("SELECT o FROM " + entityClass.getSimpleName() + " o ORDER BY o." + campoOrdena);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {

            log.log(Level.SEVERE, "getLista", "No se puede obtener la lista de " + entityClass.getName());
            log.log(Level.SEVERE, "getLista", e);
            return null;
        }
    }

    /**
     * Consulta todos los registro de una entidad particular de acuerdo a un
     * filtro determinado
     *
     * @param <T>
     * @param entityClass: Clase de la entidad a consultar
     * @param all: indica si se devuelven todos los registro
     * @param maxResults: cantidad de registros a devolver
     * @param maxResults: posición en la lista a partir del cual comienza a
     * devolver datos
     * @param filtro aplicado a la consulta
     * @return lista de Entidad del tipo entityClass
     */
    /**
     * Consulta todos los registro de una entidad particular de acuerdo a un
     * filtro determinado
     *
     * @param <T>
     * @param entityClass : Clase de la entidad a consultar
     * @param all : indica si se devuelven todos los registro
     * @param maxResults : cantidad de registros a devolver
     * @param firstResult
     * @param filtro aplicado a la consulta
     * @return lista de Entidad del tipo entityClass
     */
    public <T extends Object> T getLista(Class entityClass, boolean all, int maxResults, int firstResult, Map<String, String> filtro) {

        try {
            String sQuery = "SELECT o FROM " + entityClass.getSimpleName() + " o "
                    + generarStringFiltro(filtro, "o", true);

            Query q = getEm().createQuery(sQuery);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }

            return (T) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;

        } catch (Exception e) {

            log.log(Level.SEVERE, "getLista", "No se puede obtener entidad " + entityClass.getSimpleName());
            log.log(Level.SEVERE, "getLista", e);
            return null;
        }
    }

    /**
     * Consulta todos los registro de una entidad particular de acuerdo a un
     * filtro determinado
     *
     * @param <T>
     * @param entityClass: Clase de la entidad a consultar
     * @param all: indica si se devuelven todos los registro
     * @param maxResults: cantidad de registros a devolver
     * @param firstResults: posición en la lista a partir del cual comienza a
     * devolver datos
     * @param campoOrdena : nombre del campo por el cual queremos ordenar los
     * datos
     * @param ordena: indica si se ordena o no
     * @param filtro aplicado a la consulta
     * @return lista de Entidad del tipo entityClass
     */
    public List getLista(Class entityClass, boolean all, int maxResults, int firstResult,
            String campoOrdena, boolean ordena,
            Map<String, String> filtro) {

        try {
            String sQuery = "SELECT o FROM " + entityClass.getSimpleName() + " o ";
            sQuery.concat(generarStringFiltro(filtro, "o", true));

            if (ordena) {
                sQuery.concat(" ORDER BY " + campoOrdena);
            }

            Query q = getEm().createQuery(sQuery);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }

            return q.getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("No se puede obtener objeto " + entityClass.getSimpleName() + " - " + e.getCause());
            System.err.println("Verificar parametros - Clase - nombre del campo - valor buscado");
            return null;
        }
    }

    /**
     * Consulta en la cual se devuelve un solo objeto
     *
     * @param <T>
     * @param entityClass: Clase de la entidad devuelta
     * @param sQuery: consulta a ejecutar
     * @return Entidad del tipo entityClass
     */
    public <T extends Object> T queryObject(Class<T> entityClass, String sQuery) {
        try {
            return (T) getEm().createQuery(sQuery).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.print("No se ejecuto queryObject: " + sQuery + e.getMessage());
            return null;
        }
    }

    /**
     * Consulta en la cual se devuelven mas de un objeto
     *
     * @param entityClass : Clase de la entidad contenida en la lista devuelta
     * @param sQuery : Consulta a ejecutar
     * @return lista de Entidad del tipo entityClass
     */
    public List queryList(Class entityClass, String sQuery) {
        try {
            return getEm().createQuery(sQuery).getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.print("No se ejecuto queryList: " + sQuery);
            System.err.print("ERROR: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Consulta de actualización * @param sQuery : Consulta a ejecutar
     *
     * @return void
     */
    public void queryUpdate(String query) {
        try {
            getEm().createQuery(query).executeUpdate();
        } catch (Exception e) {
            System.err.print(e.getMessage() + "No ejecutó consulta: " + query);
        }
    }

    public List<Object[]> executeSQL(String sql) {

        try {
            Query q = getEm().createNativeQuery(sql);
            return q.getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("Error ejecutando SQL: " + e);
            return null;
        }
    }

    public ResultSet getResultSet(String consultaGrupo) throws SQLException {

        em.getTransaction().begin();
        java.sql.Connection connection = getEm().unwrap(java.sql.Connection.class); // unwraps the Connection class.

        PreparedStatement stmt = connection.prepareStatement(consultaGrupo);
        ResultSet resultSet = stmt.executeQuery();

        em.getTransaction().commit();

        return resultSet;
    }

    public int getCantidadRegistros(Class entityClass) {
        return getCantidadRegistros(null, entityClass);
    }

    /**
     * Consulta la cantidad de registros de una entidad
     *
     * @param entityClass : Tipo de objetos contenidos en la lista devuelta
     *
     * @return Cantidad de registros
     */
    public int getCantidadRegistros(Map<String, String> filtro, Class entityClass) {

        String sQuery = "SELECT COUNT(o) FROM " + entityClass.getSimpleName() + " o "
                + " WHERE o.auditoria.debaja = 'N' "
                + generarStringFiltro(filtro, "o", false);

        return ((Long) getEm().createQuery(sQuery).getSingleResult()).intValue();
    }

    /**
     * Genera un string con la estructura JPQL para utilizarlo en una consulta
     *
     * @param filtro a aplicar a la consulta
     * @return
     */
    /**
     * Genera un string con la estructura JPQL para utilizarlo en una consulta
     *
     * @param filtro a aplicar a la consulta
     * @param aliasEntidad
     * @param cWhere
     * @return
     */
    protected String generarStringFiltro(Map<String, String> filtro, String aliasEntidad, boolean cWhere) {

        if (filtro == null) {
            return "";
        }

        if (filtro.isEmpty()) {
            return "";
        }

        String sFiltro = "";
        //Si el filtro no está vacio lo aplicamos
        if (!filtro.isEmpty()) {

            Iterator it = filtro.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry ent = (Map.Entry) it.next();

                if (cWhere) {

                    if (aliasEntidad == null || aliasEntidad.isEmpty()) {
                        sFiltro += " WHERE ( " + ent.getKey() + ent.getValue() + ") ";
                    } else {
                        sFiltro += " WHERE ( " + aliasEntidad + "." + ent.getKey() + ent.getValue() + ") ";
                    }

                    cWhere = false;
                } else if (aliasEntidad == null || aliasEntidad.isEmpty()) {
                    sFiltro += " AND ( " + ent.getKey() + " " + ent.getValue() + ") ";
                } else {
                    sFiltro += " AND ( " + aliasEntidad + "." + ent.getKey() + " " + ent.getValue() + ") ";
                }
            }
        }

        return sFiltro;

        /**
         * //Si el filtro no está vacio lo aplicamos if (!filtro.isEmpty()){
         *
         * //Agrego WHERE al string? if(cWhere) sFiltro = sFiltro + " WHERE ";
         * //Si no agrego el WHERE, tengo que agregar el AND al principio //Ya
         * que viene de otra consulta con WHERE else cAND = true; Iterator it =
         * filtro.entrySet().iterator(); while(it.hasNext()){
         *
         * Map.Entry ent = (Map.Entry)it.next();
         *
         * if (cAND) sFiltro = sFiltro + " AND " ; sFiltro = sFiltro + "( o."+
         * ent.getKey() +" LIKE '%" + ent.getValue() + "%' )"; cAND = true; } }
         */
    }

    protected String generarStringOrden(List<String> orden, String aliasEntidad) {

        if (orden == null) {
            return "";
        }

        if (orden.isEmpty()) {
            return "";
        }

        String sOrden = "ORDER BY ";
        boolean agregaComa = false;

        //Si el filtro no está vacio lo aplicamos
        if (!orden.isEmpty()) {
            for (String campoOrden : orden) {

                if (agregaComa) {
                    sOrden += ",";
                }
                sOrden += aliasEntidad + "." + campoOrden;
                agregaComa = true;
            }
        }

        return sOrden;
    }

    protected void sincronizacionTemporal(String vista) {

        //Solución que encontré para que me sincronice los datos
        String nQuery = "select * from " + vista + " limit 1 ";
        List<Object[]> resultado = new ArrayList<Object[]>();
        resultado = getEm().createNativeQuery(nQuery).getResultList();
    }

    public Map<String, String> getFiltro() {
        return new LinkedHashMap<String, String>();
    }

    public void setDeBajaAll(Class entityClass) {

        try {
            String sQuery = "UPDATE " + entityClass.getSimpleName() + " e SET e.auditoria.debaja = 'S' ";

            int count = getEm().createQuery(sQuery).executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "setDeBajaAll", e.getMessage());

        }
    }

}
