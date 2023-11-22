package com.example.listasuplencia;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class Profesor {
    protected String nombre;
    protected String telefono;
    protected String materia; //Materias

    protected Map<String, Set<String>> disponibilidad;

    public Profesor(String nombre, String telefono,String materia) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.materia = materia;
        this.disponibilidad = new HashMap<>();
        //for (String dia : obtenerDiasSemana()) {
        //    disponibilidad.put(dia, new HashSet<>());
        //}
    }

    // Obtener los días de la semana
    //private String[] obtenerDiasSemana() {
    //    return new String[]{"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
    //}

    public Map<String, Set<String>> getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Map<String, Set<String>> disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    // Método para agregar la disponibilidad para un día específico
    public void agregarDisponibilidad(String dia, Set<String> horarios) {
        disponibilidad.put(dia, horarios);
    }

    // Método para verificar si un día está disponible
    public boolean esDiaDisponible(String dia) {
        return disponibilidad.containsKey(dia);
    }

    // Método para obtener los horarios disponibles para un día específico
    public Set<String> obtenerHorarios(String dia) {
        return disponibilidad.getOrDefault(dia, new HashSet<>());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    // Método para convertir el objeto a un mapa utilizando Gson
    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(this), type);
    }
}
