package com.example.listasuplencia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Vista extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vistainfo);

        // Obtener información del Intent
        Intent intent = getIntent();
        String informacionExtra = intent.getStringExtra("informacionExtra");

        // Actualizar la interfaz de usuario con la información adicional
        TextView textViewDetalle = findViewById(R.id.textViewN);
        textViewDetalle.setText(informacionExtra);

        ////////////////////////////////////
        // Obtener referencia al botón
        Button botonVolver = findViewById(R.id.botonVolver);

        // Configurar el evento click del botón
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volver a la vista anterior (puedes ajustar esto según tu lógica)
                finish();
            }
        });
    }
}
