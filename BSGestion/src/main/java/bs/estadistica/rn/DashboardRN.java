/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.estadistica.rn;

import bs.estadistica.dao.DashboardDAO;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class DashboardRN implements Serializable {

    @EJB
    private DashboardDAO dashboardDAO;

    public List<Object[]> executeSQL(String sql) {

        return dashboardDAO.executeSQL(sql);
    }
}
