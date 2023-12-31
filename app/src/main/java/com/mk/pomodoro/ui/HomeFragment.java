package com.mk.pomodoro.ui;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.mk.pomodoro.R;
import com.mk.pomodoro.controller.Temporizador;

import java.util.Locale;

public class HomeFragment extends Fragment {
    AppCompatTextView tiempo;
    TabLayout tabLayout;
    private ProgressBar barraProgresoCircular;
    private MaterialButton botonParar, botonIniciar, botonPausar,botonContinuar;
    private Temporizador temporizador;
    private boolean iniciarTemporizador = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = view.findViewById(R.id.tlOptionsTime);
        tiempo = view.findViewById(R.id.tvTime);
        barraProgresoCircular = view.findViewById(R.id.pbCircle);
        botonParar = view.findViewById(R.id.btnParar);
        botonIniciar = view.findViewById(R.id.btnIniciar);
        botonPausar = view.findViewById(R.id.btnPausar);
        botonContinuar = view.findViewById(R.id.btnContinuar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                iniciarTemporizador = false;
                if (position == 0) {
                    prepararTemporizador(45 * 60);

                } else if (position == 1) {
                    prepararTemporizador(15 * 60);
                }
                botonIniciar.setVisibility(View.VISIBLE);
                botonPausar.setVisibility(View.GONE);
                botonContinuar.setVisibility(View.GONE);
                botonParar.setVisibility(View.GONE);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        prepararTemporizador(45 * 60);

        botonParar.setOnClickListener(v -> {
            temporizador.reiniciarTemporizador(barraProgresoCircular, tiempo);
            botonIniciar.setVisibility(View.VISIBLE);
            botonPausar.setVisibility(View.GONE);
            botonContinuar.setVisibility(View.GONE);
            botonParar.setVisibility(View.GONE);
        });

        botonIniciar.setOnClickListener(v -> {
            iniciarTemporizador = true;
            temporizador.iniciarTemporizador();
            botonIniciar.setVisibility(View.GONE);
            botonPausar.setVisibility(View.VISIBLE);
            botonParar.setVisibility(View.VISIBLE);
        });

        botonPausar.setOnClickListener(v -> {
            temporizador.pausarTemporizador();
            botonPausar.setVisibility(View.GONE);
            botonContinuar.setVisibility(View.VISIBLE);
        });

        botonContinuar.setOnClickListener(v -> {
            temporizador.reanudarTemporizador();
            botonContinuar.setVisibility(View.GONE);
            botonPausar.setVisibility(View.VISIBLE);
        });
        return view;
    }

    private void prepararTemporizador(int segundos) {
        if (temporizador != null) {
            temporizador.destruirTemporizador();
        }
        barraProgresoCircular.setMax(segundos);
        temporizador = new Temporizador(segundos * 1000L, 1000);
        temporizador.setEscuchadorTick(millisUntilFinished -> {
            int segundosRestantes = (int) (millisUntilFinished / 1000f);
            tiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", segundosRestantes / 60, segundosRestantes % 60));
            barraProgresoCircular.setProgress(segundosRestantes);
        });
        temporizador.setEscuchadorFinalizacion(() -> tiempo.setText("00:00"));
        // Mostramos el tiempo inicial sin iniciar el temporizador
        int segundosRestantes = segundos;
        tiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", segundosRestantes / 60, segundosRestantes % 60));
        barraProgresoCircular.setProgress(segundosRestantes);
        // Iniciamos el temporizador solo si iniciarTemporizador es verdadero
        if (iniciarTemporizador) {
            temporizador.iniciarTemporizador();
        }
    }
}