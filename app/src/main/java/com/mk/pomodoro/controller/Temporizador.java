package com.mk.pomodoro.controller;

import android.os.CountDownTimer;

public class Temporizador {
    private long milisegundosFuturos;
    private long intervaloCuentaRegresiva;
    private long milisegundosRestantes;
    private TemporizadorInterno temporizador;
    private boolean estaCorriendo = false;
    private EscuchadorTick escuchadorTick = null;
    private EscuchadorFinalizacion escuchadorFinalizacion = null;

    public Temporizador(long milisegundosFuturos, long intervaloCuentaRegresiva) {
        this.milisegundosFuturos = milisegundosFuturos;
        this.intervaloCuentaRegresiva = intervaloCuentaRegresiva;
        this.milisegundosRestantes = milisegundosFuturos;
        this.temporizador = new TemporizadorInterno(this, milisegundosFuturos, intervaloCuentaRegresiva);
    }

    public interface EscuchadorTick {
        void enTick(long milisegundosRestantes);
    }

    public interface EscuchadorFinalizacion {
        void enFinalizacion();
    }

    public void setEscuchadorTick(EscuchadorTick escuchadorTick) {
        this.escuchadorTick = escuchadorTick;
    }

    public void setEscuchadorFinalizacion(EscuchadorFinalizacion escuchadorFinalizacion) {
        this.escuchadorFinalizacion = escuchadorFinalizacion;
    }

    private class TemporizadorInterno extends CountDownTimer {
        private Temporizador padre;

        public TemporizadorInterno(Temporizador padre, long milisegundosFuturos, long intervaloCuentaRegresiva) {
            super(milisegundosFuturos, intervaloCuentaRegresiva);
            this.padre = padre;
        }

        @Override
        public void onTick(long milisegundosRestantes) {
            padre.milisegundosRestantes = milisegundosRestantes;
            if (padre.escuchadorTick != null) {
                padre.escuchadorTick.enTick(milisegundosRestantes);
            }
        }

        @Override
        public void onFinish() {
            padre.milisegundosRestantes = 0;
            if (padre.escuchadorFinalizacion != null) {
                padre.escuchadorFinalizacion.enFinalizacion();
            }
        }

        public long getMilisegundosRestantes() {
            return padre.milisegundosRestantes;
        }
    }

    public void pausarTemporizador() {
        temporizador.cancel();
        estaCorriendo = false;
    }

    public void reanudarTemporizador() {
        if (!estaCorriendo && temporizador.getMilisegundosRestantes() > 0) {
            temporizador = new TemporizadorInterno(this, temporizador.getMilisegundosRestantes(), intervaloCuentaRegresiva);
            iniciarTemporizador();
        }
    }

    public void iniciarTemporizador() {
        temporizador.start();
        estaCorriendo = true;
    }

    public void reiniciarTemporizador() {
        temporizador.cancel();
        temporizador = new TemporizadorInterno(this, milisegundosFuturos, intervaloCuentaRegresiva);
        iniciarTemporizador();
    }

    public void destruirTemporizador() {
        temporizador.cancel();
    }
}
