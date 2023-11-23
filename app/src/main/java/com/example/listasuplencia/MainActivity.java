package com.example.listasuplencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    Profesor profesor = new Profesor("juan", "223", "matematica");
    List<Profesor> listaProfesores = new ArrayList<>();
    private CalendarView calendarView;
    private EditText editText;
    private String stringDateSelected;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        traerProfesoresFromDB();
        ///////////////cargaProfesores();
        ///////////////HashSet<String> horariosLunes = new HashSet<>();
        ///////////////horariosLunes.add("9:00 AM - 12:00 PM");
        ///////////////horariosLunes.add("2:00 PM - 5:00 PM");
        ///////////////profesor.agregarDisponibilidad("Lunes", horariosLunes);

        calendarView = findViewById(R.id.calendarView);
        editText = findViewById(R.id.editText);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                stringDateSelected = Integer.toString(i) + Integer.toString(i1 + 1) + Integer.toString(i2);
                calendarClicked();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        // Obtén una referencia a tu base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("profesores");
        //////////////guardarProfesorEnFirebase(profesor);

///////////////////////////
        // Agregar disponibilidad para lunes
   /*     HashSet<String> horariosLunes = new HashSet<>();
        horariosLunes.add("9:00 AM - 12:00 PM");
        horariosLunes.add("2:00 PM - 5:00 PM");
        profesor.agregarDisponibilidad("Lunes", horariosLunes);

        // Verificar disponibilidad para lunes
        if (profesor.esDiaDisponible("Lunes")) {
            System.out.println("El profesor está disponible el lunes.");

            // Obtener horarios disponibles para lunes
            Set<String> horariosDisponiblesLunes = profesor.obtenerHorarios("Lunes");
            System.out.println("Horarios disponibles: " + horariosDisponiblesLunes);
        } else {
            System.out.println("El profesor no está disponible el lunes.");
        }

        // Verificar disponibilidad para un día no existente
        if (profesor.esDiaDisponible("Martes")) {
            System.out.println("El profesor está disponible el martes.");
        } else {
            System.out.println("El profesor no está disponible el martes.");
        }*/
        //////////
        //////////
        /*
        // Buscar profesores disponibles el martes
        List<Profesor> profesoresDisponiblesMartes = buscarProfesoresDisponibles(listaProfesores, "Martes");

        if (!profesoresDisponiblesMartes.isEmpty()) {
            System.out.println("Profesores disponibles el martes:");

            for (Profesor profesor : profesoresDisponiblesMartes) {
                System.out.println("Nombre: " + profesor.getNombre());
                System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("Martes"));
                System.out.println("----");
            }
        } else {
            System.out.println("No hay profesores disponibles el martes.");
        }*/
    }

    private void guardarProfesorEnFirebase(Profesor profesor) {
        databaseReference = FirebaseDatabase.getInstance().getReference("profesores");
        // Obtén una nueva clave única para el profesor
        String key = databaseReference.push().getKey();

        // Convierte el objeto Profesor a un mapa
        Map<String, Object> profesorMap = profesor.toMap();

        // Crea un mapa para guardar el profesor con su clave única
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, profesorMap);

        // Actualiza la base de datos con el nuevo profesor
        databaseReference.updateChildren(childUpdates);
    }

    // Método para buscar profesores disponibles en un día específico
    public static List<Profesor> buscarProfesoresDisponibles(List<Profesor> listaProfesores, String dia) {
        List<Profesor> profesoresDisponibles = new ArrayList<>();

        for (Profesor profesor : listaProfesores) {
            if (profesor.esDiaDisponible(dia)) {
                profesoresDisponibles.add(profesor);
            }
        }

        return profesoresDisponibles;
    }

    public void traerProfesoresFromDB() {
        // Dentro de tu método onCreate() u otro lugar apropiado
        databaseReference = FirebaseDatabase.getInstance().getReference("profesores");

// Agregar un listener para escuchar los cambios en los datos
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista actual de profesores
                listaProfesores.clear();

                // Iterar sobre los nodos hijos (cada profesor) en dataSnapshot
                for (DataSnapshot profesorSnapshot : dataSnapshot.getChildren()) {
                    // Obtener datos como un Map
                    Map<String, Object> profesorData = (Map<String, Object>) profesorSnapshot.getValue();

                    // Crear objeto Profesor utilizando los datos del Map
                    Profesor profesor = construirProfesorDesdeMap(profesorData);

                    // Agregar el profesor a la lista
                    listaProfesores.add(profesor);
                }

                // Ahora, tu listaProfesores tiene todos los profesores de la base de datos
                // Puedes realizar cualquier acción necesaria con la lista actualizada
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de lectura de la base de datos (opcional)
                Log.e("FirebaseError", "Error al leer datos de la base de datos", error.toException());
            }
        });
    }

    // Método para construir un objeto Profesor desde un Map
    private Profesor construirProfesorDesdeMap(Map<String, Object> profesorData) {
        String nombre = (String) profesorData.get("nombre");
        String telefono = (String) profesorData.get("telefono");
        String materia = (String) profesorData.get("materia");

        // Crear objeto Profesor
        Profesor profesor = new Profesor(nombre, telefono, materia);

        // Agregar disponibilidad si está presente en el Map
        if (profesorData.containsKey("disponibilidad")) {
            Map<String, Object> disponibilidadData = (Map<String, Object>) profesorData.get("disponibilidad");
            construirDisponibilidadDesdeMap(profesor, disponibilidadData);
        }

        return profesor;
    }

    // Método para construir la disponibilidad de un Profesor desde un Map
    private void construirDisponibilidadDesdeMap(Profesor profesor, Map<String, Object> disponibilidadData) {
        for (Map.Entry<String, Object> entry : disponibilidadData.entrySet()) {
            String dia = entry.getKey();
            List<String> horarios = (List<String>) entry.getValue();
            profesor.agregarDisponibilidad(dia, new HashSet<>(horarios));
        }
    }


    public void cargaProfesores() {
        // Agregar algunos profesores a la lista con disponibilidad en diferentes días
        Profesor profesor1 = new Profesor("dolino", "289945", "lengua");
        HashSet<String> horariosProfesor1 = new HashSet<>(Arrays.asList("9:00 AM - 12:00 PM", "2:00 PM - 5:00 PM"));
        profesor1.agregarDisponibilidad("Lunes", horariosProfesor1);
        guardarProfesorEnFirebase(profesor1);
        listaProfesores.add(profesor1);

        Profesor profesor2 = new Profesor("camila", "838388", "matematica");
        HashSet<String> horariosProfesor2 = new HashSet<>(Arrays.asList("10:00 AM - 1:00 PM", "3:00 PM - 6:00 PM"));
        profesor2.agregarDisponibilidad("Martes", horariosProfesor2);
        guardarProfesorEnFirebase(profesor2);
        listaProfesores.add(profesor2);

        Profesor profesor3 = new Profesor("javier", "1112223", "naturales");
        HashSet<String> horariosProfesor3 = new HashSet<>(Arrays.asList("9:00 AM - 12:00 PM", "2:00 PM - 5:00 PM"));
        profesor3.agregarDisponibilidad("Martes", horariosProfesor3);
        guardarProfesorEnFirebase(profesor3);
        listaProfesores.add(profesor3);

        Profesor profesor4 = new Profesor("sofia", "6767662", "matematica");
        HashSet<String> horariosProfesor4 = new HashSet<>(Arrays.asList("11:00 AM - 12:45 AM", "8:30 PM - 9:30 PM"));
        profesor4.agregarDisponibilidad("Viernes", horariosProfesor4);
        guardarProfesorEnFirebase(profesor4);
        listaProfesores.add(profesor4);
    }

    private void calendarClicked() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    editText.setText(snapshot.getValue().toString());
                } else {
                    editText.setText("-");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void buttonSaveEvent(View view) throws ParseException {
        //SimpleDateFormat inFormat = new SimpleDateFormat("yyyymmdd");
        //Date date = inFormat.parse(stringDateSelected);
        //SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        //String goal = outFormat.format(date); // devuelve bien + 4 dias (lunes->jueves)
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        databaseReference.child(stringDateSelected).setValue(editText.getText().toString()/* + goal*/);
        databaseReference = FirebaseDatabase.getInstance().getReference("profesores");
    }

    public void buttonViewEvent(View view) throws ParseException {
        databaseReference = FirebaseDatabase.getInstance().getReference("profesores");
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyymmdd");
        Date date = inFormat.parse(stringDateSelected);
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String dia = outFormat.format(date);
        String envio = "";

        if (dia.equalsIgnoreCase("monday"))
            dia = "jueves";
        else if (dia.equalsIgnoreCase("tuesday"))
            dia = "viernes";
        else if (dia.equalsIgnoreCase("wednesday"))
            dia = "sabado";
        else if (dia.equalsIgnoreCase("thursday"))
            dia = "domingo";
        else if (dia.equalsIgnoreCase("friday"))
            dia = "lunes";
        else if (dia.equalsIgnoreCase("saturday"))
            dia = "martes";
        else if (dia.equalsIgnoreCase("sunday"))
            dia = "miercoles";
        else
            dia = "error";

        if (!dia.equalsIgnoreCase("error")) {
            if (dia.equalsIgnoreCase("martes")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesMartes = buscarProfesoresDisponibles(listaProfesores, "Martes");

                if (!profesoresDisponiblesMartes.isEmpty()) {
                    envio += "Profesores disponibles el martes:\n";

                    for (Profesor profesor : profesoresDisponiblesMartes) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("Martes") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el martes.";
                }
            } else if (dia.equalsIgnoreCase("miercoles")) {
                List<Profesor> profesoresDisponiblesMiercoles = buscarProfesoresDisponibles(listaProfesores, "Miercoles");

                if (!profesoresDisponiblesMiercoles.isEmpty()) {
                    envio += "Profesores disponibles el miercoles:\n";

                    for (Profesor profesor : profesoresDisponiblesMiercoles) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("Miercoles") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el miercoles.";
                }
            } else if (dia.equalsIgnoreCase("jueves")) {
                List<Profesor> profesoresDisponiblesJueves = buscarProfesoresDisponibles(listaProfesores, "jueves");

                if (!profesoresDisponiblesJueves.isEmpty()) {
                    envio += "Profesores disponibles el jueves:\n";

                    for (Profesor profesor : profesoresDisponiblesJueves) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("jueves") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el jueves.";
                }
            } else if (dia.equalsIgnoreCase("viernes")) {
                List<Profesor> profesoresDisponiblesViernes = buscarProfesoresDisponibles(listaProfesores, "Viernes");

                if (!profesoresDisponiblesViernes.isEmpty()) {
                    envio += "Profesores disponibles el viernes:\n";

                    for (Profesor profesor : profesoresDisponiblesViernes) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("viernes") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el viernes.";
                }
            } else if (dia.equalsIgnoreCase("sabado")) {
                List<Profesor> profesoresDisponiblesSabado = buscarProfesoresDisponibles(listaProfesores, "sabado");

                if (!profesoresDisponiblesSabado.isEmpty()) {
                    envio += "Profesores disponibles el sabado:\n";

                    for (Profesor profesor : profesoresDisponiblesSabado) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("sabado") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el sabado.";
                }
            } else if (dia.equalsIgnoreCase("domingo")) {
                List<Profesor> profesoresDisponiblesDomingo = buscarProfesoresDisponibles(listaProfesores, "domingo");

                if (!profesoresDisponiblesDomingo.isEmpty()) {
                    envio += "Profesores disponibles el domingo:\n";

                    for (Profesor profesor : profesoresDisponiblesDomingo) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("domingo") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el domingo.";
                }
            } else if (dia.equalsIgnoreCase("lunes")) {
                List<Profesor> profesoresDisponiblesLunes = buscarProfesoresDisponibles(listaProfesores, "lunes");

                if (!profesoresDisponiblesLunes.isEmpty()) {
                    envio += "Profesores disponibles el lunes:\n";

                    for (Profesor profesor : profesoresDisponiblesLunes) {
                        envio += ("Nombre: " + profesor.getNombre() + "\n");
                        envio += ("Horarios disponibles: " + profesor.obtenerHorarios("lunes") + "\n");
                        envio += "-----\n";
                    }
                } else {
                    envio += "No hay profesores disponibles el lunes.";
                }
            }
        } else {
            envio += "Error en la busqueda.";
        }
        Button botonVerDetalle = findViewById(R.id.button2);
        String finalEnvio = envio;
        botonVerDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad (DetalleActivity)
                Intent intent = new Intent(MainActivity.this, Vista.class);

                // Puedes pasar información a la nueva actividad usando extras en el Intent
                intent.putExtra("informacionExtra", finalEnvio);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });
    }

    public void buttonProfesSociales(View view) {
        List<Profesor> profesoresDeAsignatura = new ArrayList<>();
        String asignatura = "sociales";
        String envio = "";

        // Itera sobre la lista de profesores y verifica si enseñan la asignatura especificada
        for (Profesor profesor : listaProfesores) {
            if (profesor.getMateria().contains(asignatura)) {
                profesoresDeAsignatura.add(profesor);
            }
        }

        // Muestra los resultados en la consola o en tu interfaz de usuario
        if (!profesoresDeAsignatura.isEmpty()) {
            envio += ("Profesores de la asignatura " + asignatura + ":\n");
            for (Profesor profesor : profesoresDeAsignatura) {
                envio += ("Nombre: " + profesor.getNombre() + "\n");
                envio += ("Teléfono: " + profesor.getTelefono() + "\n");
                envio += ("Días disponibles: " + profesor.getDisponibilidad().keySet() + "\n");
                envio += ("Horarios disponibles: " + profesor.getDisponibilidad().values() + "\n");
                envio += ("\n----\n\n");
            }
        } else {
            envio += ("No hay profesores para la asignatura " + asignatura + ".\n");
        }

        Button botonVerDetalle = findViewById(R.id.buttonSociales);
        String finalEnvio = envio;
        botonVerDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad (DetalleActivity)
                Intent intent = new Intent(MainActivity.this, Vista.class);

                // Puedes pasar información a la nueva actividad usando extras en el Intent
                intent.putExtra("informacionExtra", finalEnvio);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });
    }

    public void buttonProfesMatematica(View view) {
        List<Profesor> profesoresDeAsignatura = new ArrayList<>();
        String asignatura = "matematica";
        String envio = "";

        // Itera sobre la lista de profesores y verifica si enseñan la asignatura especificada
        for (Profesor profesor : listaProfesores) {
            if (profesor.getMateria().contains(asignatura)) {
                profesoresDeAsignatura.add(profesor);
            }
        }

        // Muestra los resultados en la consola o en tu interfaz de usuario
        if (!profesoresDeAsignatura.isEmpty()) {
            envio += ("Profesores de la asignatura " + asignatura + ":\n");
            for (Profesor profesor : profesoresDeAsignatura) {
                envio += ("Nombre: " + profesor.getNombre() + "\n");
                envio += ("Teléfono: " + profesor.getTelefono() + "\n");
                envio += ("Días disponibles: " + profesor.getDisponibilidad().keySet() + "\n");
                envio += ("Horarios disponibles: " + profesor.getDisponibilidad().values() + "\n");
                envio += ("\n----\n\n");
            }
        } else {
            envio += ("No hay profesores para la asignatura " + asignatura + ".\n");
        }

        Button botonVerDetalle = findViewById(R.id.buttonMatematica);
        String finalEnvio = envio;
        botonVerDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad (DetalleActivity)
                Intent intent = new Intent(MainActivity.this, Vista.class);

                // Puedes pasar información a la nueva actividad usando extras en el Intent
                intent.putExtra("informacionExtra", finalEnvio);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });
    }

    public void buttonProfesNaturales(View view) {
        List<Profesor> profesoresDeAsignatura = new ArrayList<>();
        String asignatura = "naturales";
        String envio = "";

        // Itera sobre la lista de profesores y verifica si enseñan la asignatura especificada
        for (Profesor profesor : listaProfesores) {
            if (profesor.getMateria().contains(asignatura)) {
                profesoresDeAsignatura.add(profesor);
            }
        }

        // Muestra los resultados en la consola o en tu interfaz de usuario
        if (!profesoresDeAsignatura.isEmpty()) {
            envio += ("Profesores de la asignatura " + asignatura + ":\n");
            for (Profesor profesor : profesoresDeAsignatura) {
                envio += ("Nombre: " + profesor.getNombre() + "\n");
                envio += ("Teléfono: " + profesor.getTelefono() + "\n");
                envio += ("Días disponibles: " + profesor.getDisponibilidad().keySet() + "\n");
                envio += ("Horarios disponibles: " + profesor.getDisponibilidad().values() + "\n");
                envio += ("\n----\n\n");
            }
        } else {
            envio += ("No hay profesores para la asignatura " + asignatura + ".\n");
        }

        Button botonVerDetalle = findViewById(R.id.buttonNaturales);
        String finalEnvio = envio;
        botonVerDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad (DetalleActivity)
                Intent intent = new Intent(MainActivity.this, Vista.class);

                // Puedes pasar información a la nueva actividad usando extras en el Intent
                intent.putExtra("informacionExtra", finalEnvio);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });
    }
}