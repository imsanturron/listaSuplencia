package com.example.listasuplencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        cargaProfesores();
        HashSet<String> horariosLunes = new HashSet<>();
        horariosLunes.add("9:00 AM - 12:00 PM");
        horariosLunes.add("2:00 PM - 5:00 PM");
        profesor.agregarDisponibilidad("Lunes", horariosLunes);

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
        guardarProfesorEnFirebase(profesor);

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

    public void cargaProfesores(){
        // Agregar algunos profesores a la lista con disponibilidad en diferentes días
        Profesor profesor1 = new Profesor("don", "2899", "lengua");
        HashSet<String> horariosProfesor1 = new HashSet<>(Arrays.asList("9:00 AM - 12:00 PM", "2:00 PM - 5:00 PM"));
        profesor1.agregarDisponibilidad("Lunes", horariosProfesor1);
        listaProfesores.add(profesor1);

        Profesor profesor2 = new Profesor("camila", "838", "matematica");
        HashSet<String> horariosProfesor2 = new HashSet<>(Arrays.asList("10:00 AM - 1:00 PM", "3:00 PM - 6:00 PM"));
        profesor2.agregarDisponibilidad("Martes", horariosProfesor2);
        listaProfesores.add(profesor2);

        Profesor profesor3 = new Profesor("javi", "111111", "naturales");
        HashSet<String> horariosProfesor3 = new HashSet<>(Arrays.asList("9:00 AM - 12:00 PM", "2:00 PM - 5:00 PM"));
        profesor3.agregarDisponibilidad("Martes", horariosProfesor3);
        listaProfesores.add(profesor3);
    }

    private void calendarClicked() {
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
        databaseReference.child(stringDateSelected).setValue(editText.getText().toString()/* + goal*/);
    }

    public void buttonViewEvent(View view) throws ParseException {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyymmdd");
        Date date = inFormat.parse(stringDateSelected);
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String dia = outFormat.format(date);
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
                    System.out.println("Profesores disponibles el martes:");

                    for (Profesor profesor : profesoresDisponiblesMartes) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("Martes"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el martes.");
                }
            } else if (dia.equalsIgnoreCase("miercoles")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesMiercoles = buscarProfesoresDisponibles(listaProfesores, "Miercoles");

                if (!profesoresDisponiblesMiercoles.isEmpty()) {
                    System.out.println("Profesores disponibles el miercoles:");

                    for (Profesor profesor : profesoresDisponiblesMiercoles) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("miercoles"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el miercoles.");
                }
            } else if (dia.equalsIgnoreCase("jueves")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesJueves = buscarProfesoresDisponibles(listaProfesores, "jueves");

                if (!profesoresDisponiblesJueves.isEmpty()) {
                    System.out.println("Profesores disponibles el jueves:");

                    for (Profesor profesor : profesoresDisponiblesJueves) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("jueves"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el jueves.");
                }
            } else if (dia.equalsIgnoreCase("viernes")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesViernes = buscarProfesoresDisponibles(listaProfesores, "viernes");

                if (!profesoresDisponiblesViernes.isEmpty()) {
                    System.out.println("Profesores disponibles el viernes:");

                    for (Profesor profesor : profesoresDisponiblesViernes) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("viernes"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el viernes.");
                }
            } else if (dia.equalsIgnoreCase("sabado")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesSabado = buscarProfesoresDisponibles(listaProfesores, "sabado");

                if (!profesoresDisponiblesSabado.isEmpty()) {
                    System.out.println("Profesores disponibles el sabado:");

                    for (Profesor profesor : profesoresDisponiblesSabado) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("sabado"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el sabado.");
                }
            } else if (dia.equalsIgnoreCase("domingo")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesDomingo = buscarProfesoresDisponibles(listaProfesores, "domingo");

                if (!profesoresDisponiblesDomingo.isEmpty()) {
                    System.out.println("Profesores disponibles el domingo:");

                    for (Profesor profesor : profesoresDisponiblesDomingo) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("domingo"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el martes.");
                }
            } else if (dia.equalsIgnoreCase("lunes")) {
                // Buscar profesores disponibles el martes
                List<Profesor> profesoresDisponiblesLunes = buscarProfesoresDisponibles(listaProfesores, "lunes");

                if (!profesoresDisponiblesLunes.isEmpty()) {
                    System.out.println("Profesores disponibles el lunes:");

                    for (Profesor profesor : profesoresDisponiblesLunes) {
                        System.out.println("Nombre: " + profesor.getNombre());
                        System.out.println("Horarios disponibles: " + profesor.obtenerHorarios("lunes"));
                        System.out.println("----");
                    }
                } else {
                    System.out.println("No hay profesores disponibles el lunes.");
                }
            }
        } else {
            System.out.println("error en la busqueda");
        }
    }
}