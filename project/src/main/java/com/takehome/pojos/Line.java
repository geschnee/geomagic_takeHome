package com.takehome.pojos;

// Linie (eine Zeile in der Inputdatei)
public record Line(int id, Point p1, Point p2, float length) { }