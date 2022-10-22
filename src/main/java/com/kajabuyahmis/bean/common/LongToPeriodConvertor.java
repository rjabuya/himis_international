/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.bean.common;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author safrin
 */
@FacesConverter("longToTime")
public class LongToPeriodConvertor implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Double seconds = (Double) value;
        if (seconds == null || seconds == 0.0) {
            return "";
        }

        long s =  seconds.longValue() % 60;
        long m = (seconds.longValue() / 60) % 60;
        long h = (seconds.longValue() / (60 * 60));
        return String.format("%d:%02d:%02d", h, m, s);
    }

}
