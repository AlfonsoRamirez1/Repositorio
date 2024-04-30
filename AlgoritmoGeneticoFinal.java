package AlgoritmoGenetico;

/**
 * UNIVERSIDAD AUTONOMA DE COAHUILA.
 * Facultad de sistemas.
 *
 * Analisis y modelación de sistemas.
 *
 * Proyecto: Algoritmo genético.
 *
 * Desarrollado por Alfonso Ramírez Cárdenas.
 **/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class AlgoritmoGeneticoFinal {

    // Método principal para generar y evaluar matrices
    public static void main(String[] args) {
        String[] todasLasPalabras = new String[50];
        int palabraIndex = 0;

        // Solicitar al usuario que ingrese las 50 palabras en lotes de 5
        for (int lote = 0; lote < 10; lote++) {
            for (int palabraEnLote = 0; palabraEnLote < 5; palabraEnLote++) {
                String mensaje = String.format("Ingrese la palabra %d de la matriz %d", palabraEnLote + 1, lote + 1);
                todasLasPalabras[palabraIndex] = JOptionPane.showInputDialog(mensaje);
                palabraIndex++;
            }
        }

        // Ciclo para evolucionar las matrices a lo largo de 50 generaciones
        for (int generacion = 1; generacion <= 50; generacion++) {
            char[][][] matrices = generarNuevasMatrices(todasLasPalabras);

            // Generar archivo con matrices legibles
            generarArchivoMatricesLegibles(matrices, generacion);

            // Generar archivo con detalles de matrices
            generarArchivoDetallesMatrices(matrices, generacion);

            // Seleccionar las mejores 4 matrices por puntuación y generar archivo con las mejores puntuaciones
            seleccionarMejoresPuntuaciones(matrices, generacion);

            // Realizar la cruza para generar nuevas matrices y mantener 10 en total
            realizarCruza(matrices);

            // Actualizar lista de palabras con las palabras de las nuevas matrices
            todasLasPalabras = actualizarListaPalabras(matrices);
        }

        JOptionPane.showMessageDialog(null, "Se han completado 50 generaciones. ¡Proceso finalizado!");
    }

    // Función para generar nuevas matrices a partir de una lista de palabras
    public static char[][][] generarNuevasMatrices(String[] todasLasPalabras) {
        char[][][] matrices = new char[10][5][];
        int palabraIndex = 0;

        for (int lote = 0; lote < 10; lote++) {
            for (int palabraEnLote = 0; palabraEnLote < 5; palabraEnLote++) {
                if (palabraIndex < todasLasPalabras.length && todasLasPalabras[palabraIndex] != null) {
                    matrices[lote][palabraEnLote] = todasLasPalabras[palabraIndex].toCharArray();
                }
                palabraIndex++;
            }
            insertarGuionesAleatorios(matrices[lote]);
        }

        return matrices;
    }

    // Función para insertar guiones aleatorios en la matriz (excepto en la palabra más larga)
    public static void insertarGuionesAleatorios(char[][] matriz) {
        int longitudMaxima = obtenerLongitudMaxima(matriz);

        for (int i = 0; i < matriz.length; i++) {
            char[] palabra = matriz[i];
            if (palabra != null && palabra.length < longitudMaxima) {
                int guionesNecesarios = longitudMaxima - palabra.length;
                for (int j = 0; j < guionesNecesarios; j++) {
                    int posicionGuion = (int) (Math.random() * (palabra.length + 1));
                    palabra = insertarCaracter(palabra, '-', posicionGuion);
                }
                matriz[i] = palabra;
            }
        }
    }

    // Función para insertar un caracter en una posición específica de un arreglo de caracteres
    public static char[] insertarCaracter(char[] palabra, char caracter, int posicion) {
        char[] nuevaPalabra = new char[palabra.length + 1];
        for (int i = 0; i < nuevaPalabra.length; i++) {
            if (i < posicion) {
                nuevaPalabra[i] = palabra[i];
            } else if (i == posicion) {
                nuevaPalabra[i] = caracter;
            } else {
                nuevaPalabra[i] = palabra[i - 1];
            }
        }
        return nuevaPalabra;
    }

    // Función para generar archivo con matrices legibles
    public static void generarArchivoMatricesLegibles(char[][][] matrices, int generacion) {
        try {
            File archivo = new File("matrices_legibles.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true));

            writer.write("------ Generación " + generacion + " ------\n");

            for (int lote = 0; lote < 10; lote++) {
                writer.write("Matriz " + (lote + 1) + ":\n");

                for (int palabraEnLote = 0; palabraEnLote < 5; palabraEnLote++) {
                    char[] palabraActual = matrices[lote][palabraEnLote];

                    if (palabraActual != null) {
                        writer.write(String.valueOf(palabraActual));
                        writer.newLine();
                    }
                }

                writer.newLine(); // Separador entre matrices
            }

            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al escribir el archivo 'matrices_legibles.txt': " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Función para generar archivo con detalles de matrices
    public static void generarArchivoDetallesMatrices(char[][][] matrices, int generacion) {
        try {
            File archivo = new File("detalles_matrices.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true));

            writer.write("------ Generación " + generacion + " ------\n");

            for (int lote = 0; lote < 10; lote++) {
                writer.write("Matriz " + (lote + 1) + ":\n");

                int puntuacionTotalMatriz = 0;

                for (int palabraEnLote = 0; palabraEnLote < 5; palabraEnLote++) {
                    char[] palabraActual = matrices[lote][palabraEnLote];

                    if (palabraActual != null) {
                        String palabraSinGuiones = eliminarGuiones(palabraActual);
                        int puntuacionPalabra = calcularPuntuacionPalabra(palabraActual);

                        writer.write(palabraSinGuiones + " - Puntuación: " + puntuacionPalabra);
                        writer.newLine();
                        puntuacionTotalMatriz += puntuacionPalabra;
                    }
                }

                writer.write("Puntuación Total de la Matriz: " + puntuacionTotalMatriz);
                writer.newLine();
                writer.newLine(); // Separador entre matrices
            }

            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al escribir el archivo 'detalles_matrices.txt': " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Función para seleccionar las mejores 4 matrices por puntuación y generar archivo con las mejores puntuaciones
    public static void seleccionarMejoresPuntuaciones(char[][][] matrices, int generacion) {
        try {
            MatrizPuntuada[] matricesPuntuadas = new MatrizPuntuada[10];

            for (int i = 0; i < 10; i++) {
                int puntuacionTotalMatriz = 0;

                for (int j = 0; j < 5; j++) {
                    char[] palabraActual = matrices[i][j];
                    if (palabraActual != null) {
                        puntuacionTotalMatriz += calcularPuntuacionPalabra(palabraActual);
                    }
                }

                matricesPuntuadas[i] = new MatrizPuntuada(i, puntuacionTotalMatriz);
            }

            Arrays.sort(matricesPuntuadas, Comparator.reverseOrder());

            File archivo = new File("mejores_puntuaciones.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true));

            writer.write("------ Generación " + generacion + " ------\n");
            writer.write("Las mejores 4 matrices por puntuación son:\n\n");
            for (int i = 0; i < 4; i++) {
                int indiceMatriz = matricesPuntuadas[i].indice;
                writer.write("Matriz " + (indiceMatriz + 1) + " - Puntuación: " + matricesPuntuadas[i].puntuacion);
                writer.newLine();
                writer.newLine();

                writer.write("Contenido de la matriz:\n");
                for (int j = 0; j < 5; j++) {
                    char[] palabraActual = matrices[indiceMatriz][j];
                    if (palabraActual != null) {
                        writer.write(String.valueOf(palabraActual));
                        writer.newLine();
                    }
                }
                writer.newLine(); // Separador entre matrices
            }

            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al escribir el archivo 'mejores_puntuaciones.txt': " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Función para realizar la cruza y mantener 10 matrices en total
    public static void realizarCruza(char[][][] matrices) {
        char[][][] mejoresMatrices = new char[4][5][];

        for (int i = 0; i < 4; i++) {
            mejoresMatrices[i] = matrices[i];
        }

        Random random = new Random();

        while (mejoresMatrices.length < 10) {
            int indexPadre1 = random.nextInt(4);
            int indexPadre2 = random.nextInt(4);
            while (indexPadre2 == indexPadre1) {
                indexPadre2 = random.nextInt(4);
            }

            char[][] hijo = new char[5][];
            int contadorPalabrasPadre1 = 0;
            int contadorPalabrasPadre2 = 0;

            for (int i = 0; i < 5; i++) {
                if (i < 3) {
                    hijo[i] = mejoresMatrices[indexPadre1][contadorPalabrasPadre1++];
                } else {
                    hijo[i] = mejoresMatrices[indexPadre2][contadorPalabrasPadre2++];
                }
            }

            mejoresMatrices = Arrays.copyOf(mejoresMatrices, mejoresMatrices.length + 1);
            mejoresMatrices[mejoresMatrices.length - 1] = hijo;
        }

        for (int i = 0; i < 10; i++) {
            matrices[i] = mejoresMatrices[i];
        }
    }

    // Función auxiliar para obtener la longitud máxima de las palabras en una matriz
    public static int obtenerLongitudMaxima(char[][] matriz) {
        int longitudMaxima = 0;
        for (char[] palabra : matriz) {
            if (palabra != null && palabra.length > longitudMaxima) {
                longitudMaxima = palabra.length;
            }
        }
        return longitudMaxima;
    }

    // Función auxiliar para calcular la puntuación de una palabra
    public static int calcularPuntuacionPalabra(char[] palabra) {
        int puntuacion = 0;
        for (char c : palabra) {
            puntuacion += (int) c;
        }
        return puntuacion;
    }

    // Función auxiliar para eliminar guiones de una palabra representada como arreglo de caracteres
    public static String eliminarGuiones(char[] palabra) {
        StringBuilder palabraSinGuiones = new StringBuilder();
        for (char c : palabra) {
            if (c != '-') {
                palabraSinGuiones.append(c);
            }
        }
        return palabraSinGuiones.toString();
    }

    // Clase interna para almacenar la puntuación total de una matriz junto con su índice
    static class MatrizPuntuada implements Comparable<MatrizPuntuada> {
        int indice;
        int puntuacion;

        public MatrizPuntuada(int indice, int puntuacion) {
            this.indice = indice;
            this.puntuacion = puntuacion;
        }

        @Override
        public int compareTo(MatrizPuntuada otra) {
            return Integer.compare(this.puntuacion, otra.puntuacion);
        }
    }

    // Función auxiliar para actualizar la lista de palabras con las palabras de las nuevas matrices
    public static String[] actualizarListaPalabras(char[][][] matrices) {
        String[] todasLasPalabras = new String[50];
        int palabraIndex = 0;

        for (int lote = 0; lote < 10; lote++) {
            for (int palabraEnLote = 0; palabraEnLote < 5; palabraEnLote++) {
                todasLasPalabras[palabraIndex] = convertirPalabra(matrices[lote][palabraEnLote]);
                palabraIndex++;
            }
        }

        return todasLasPalabras;
    }

    // Función auxiliar para convertir un arreglo de caracteres en una cadena de texto
    public static String convertirPalabra(char[] palabra) {
        return new String(palabra);
    }
}
