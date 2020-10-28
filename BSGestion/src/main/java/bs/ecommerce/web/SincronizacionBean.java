/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ecommerce.web;

import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Claudio
 */
@Named
@ViewScoped
public class SincronizacionBean implements Serializable {

//    OAuthConfig config;
//    WooCommerce wooCommerce;
//
//    @PostConstruct
//    private void init() {
//        // Setup client
//        config = new OAuthConfig("http://marijushoes.com.ar", "ck_fdacb52b1fa71bac41900a73dbe91f2464f84c85", "cs_fc36a9890440f128c8672f26fc7a21c678e71418");
//        wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);
//    }
    public void listarProductos() {

        try {
            Map<String, String> params = new HashMap<>();
            params.put("per_page", "100");
            params.put("offset", "0");
//            List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);

//            System.out.println(products.size());
            JsfUtil.addInfoMessage("Fin sincronización");
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para listar productos");
        }

    }

}
