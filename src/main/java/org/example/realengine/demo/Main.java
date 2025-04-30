package org.example.realengine.demo;

import com.google.gson.Gson;

import javax.swing.*;

/**
 * Hlavní třída aplikace, která spouští hru.
 */
public class Main {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Execute());
    }
}