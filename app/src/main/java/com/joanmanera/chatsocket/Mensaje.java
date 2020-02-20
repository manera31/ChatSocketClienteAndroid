package com.joanmanera.chatsocket;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private String de;
    private String para;
    private String mensaje;
    private boolean isPrivate;

    public Mensaje(String de, String para, String mensaje, boolean isPrivate) {
        this.de = de;
        this.para = para;
        this.mensaje = mensaje;
        this.isPrivate = isPrivate;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
