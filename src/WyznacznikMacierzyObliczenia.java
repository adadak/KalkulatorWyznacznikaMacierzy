
import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * © Olaf Olejnik, 122681, Społeczna Akademia Nauk, Łódź, Poland
 *
 * Klasa zawierająca metody związane z obliczeniami wyznacznika i wyświetlaniem kroków
 * dla metod Laplace'a, Gaussa i Sarrusa.
 *
 * Metody zachowują oryginalne polskie nazewnictwo i komentarze.
 */
public class WyznacznikMacierzyObliczenia {

    // ---------------------------------------------------------------------------------
    // LAPLACE
    // ---------------------------------------------------------------------------------

    // Metoda pokazująca kroki dla metody Laplace'a
    public static void pokazKrokiLaplace(double[][] macierz, boolean poWierszach, int indeks) {
        JTextArea krokiArea = new JTextArea(20, 50);
        krokiArea.setEditable(false);

        JFrame krokiFrame = new JFrame("Kroki - Laplace");
        krokiFrame.add(new JScrollPane(krokiArea));
        krokiFrame.setSize(600, 400);
        krokiFrame.setVisible(true);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                obliczWyznacznikLaplace(macierz, krokiArea, poWierszach, indeks);
                return null;
            }
        };

        worker.execute();
    }

    public static double obliczWyznacznikLaplace(double[][] macierz, JTextArea krokiArea, boolean poWierszach, int indeks) {
        int n = macierz.length;

        if (n == 1) {
            if (krokiArea != null) {
                krokiArea.append("Wyznacznik dla macierzy 1x1: " + macierz[0][0] + "\n");
            }
            return macierz[0][0];
        } else if (n == 2) {
            BigDecimal bd1 = new BigDecimal(macierz[0][0]).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
            BigDecimal bd2 = new BigDecimal(macierz[1][1]).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
            BigDecimal bd3 = new BigDecimal(macierz[0][1]).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
            BigDecimal bd4 = new BigDecimal(macierz[1][0]).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();

            // Obliczenie wyznacznika
            BigDecimal wynik = bd1.multiply(bd2).subtract(bd3.multiply(bd4));

            // Wyświetlenie kroku
            if (krokiArea != null) {
                krokiArea.append("Wyznacznik dla macierzy 2x2: " + wynik.stripTrailingZeros().toPlainString() + "\n");
            }

            // Zwrócenie wyniku jako double
            return wynik.doubleValue();
        }

        double wyznacznik = 0;
        for (int i = 0; i < n; i++) {
            int row = poWierszach ? indeks : i;
            int col = poWierszach ? i : indeks;
            if (macierz[row][col] == 0) {
                if (krokiArea != null) {
                    krokiArea.append("Element A(" + (row + 1) + "," + (col + 1) + ") = 0, pomijanie...\n");
                }
                continue;
            }
            double[][] podmacierz = podmacierz(macierz, row, col);
            if (krokiArea != null) {
                krokiArea.append("Obliczanie rozwinięcia dla elementu A(" + (row + 1) + "," + (col + 1) + "): " + macierz[row][col] + "\n");
                krokiArea.append("Podmacierz:\n");
                for (double[] wiersz : podmacierz) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int j = 0; j < wiersz.length; j++) {
                        BigDecimal bd = new BigDecimal(wiersz[j]).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
                        sb.append(bd.toPlainString());
                        if (j < wiersz.length - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("]");
                    krokiArea.append(sb.toString() + "\n");
                }
            }

            double wynikCzesciowy = Math.pow(-1, row + col)
                    * macierz[row][col]
                    * obliczWyznacznikLaplace(podmacierz, krokiArea, true, 0);

            if (krokiArea != null) {
                krokiArea.append("Częściowy wynik: " + formatujLiczbe(wynikCzesciowy) + "\n");
            }
            wyznacznik += wynikCzesciowy;
        }
        if (krokiArea != null) {
            krokiArea.append("Wyznacznik: " + formatujLiczbe(wyznacznik) + "\n");
        }
        return wyznacznik;
    }

    // ---------------------------------------------------------------------------------
    // GAUSS
    // ---------------------------------------------------------------------------------

    // Metoda pokazująca kroki dla metody Gaussa
    public static void pokazKrokiGauss(double[][] macierz) {
        JTextArea krokiArea = new JTextArea(20, 50);
        krokiArea.setEditable(false);

        JFrame krokiFrame = new JFrame("Kroki - Gauss");
        krokiFrame.add(new JScrollPane(krokiArea));
        krokiFrame.setSize(600, 400);
        krokiFrame.setVisible(true);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                obliczWyznacznikGauss(macierz, krokiArea);
                return null;
            }
        };

        worker.execute();
    }

    public static double obliczWyznacznikGauss(double[][] macierz, JTextArea krokiArea) {
        int n = macierz.length;
        double[][] kopia = new double[n][n];

        // Kopiowanie macierzy
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                kopia[i][j] = macierz[i][j];
            }
        }

        double det = 1;
        for (int i = 0; i < n; i++) {
            // Znajdowanie maksymalnego elementu w kolumnie
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(kopia[j][i]) > Math.abs(kopia[max][i])) {
                    max = j;
                }
            }

            // Zamiana wierszy
            if (i != max) {
                double[] temp = kopia[i];
                kopia[i] = kopia[max];
                kopia[max] = temp;
                det *= -1; // Zmiana znaku wyznacznika przy zamianie wierszy
                appendToArea(krokiArea, "Zamiana: Wiersz " + (i + 1) + " <-> Wiersz " + (max + 1));
            }

            // Sprawdzanie zerowego elementu na przekątnej
            if (kopia[i][i] == 0) {
                appendToArea(krokiArea, "Wyznacznik równy 0 - zerowy element na przekątnej");
                return 0; // Wyznacznik zerowy
            }

            // Eliminacja Gaussa
            for (int j = i + 1; j < n; j++) {
                double factor = kopia[j][i] / kopia[i][i];
                appendToArea(krokiArea, "Wiersz " + (j + 1) + " = Wiersz " + (j + 1) + " - "
                        + factor + " * Wiersz " + (i + 1));
                for (int k = i; k < n; k++) {
                    kopia[j][k] -= factor * kopia[i][k];
                }
            }

            det *= kopia[i][i];
        }

        appendToArea(krokiArea, "Wyznacznik: " + formatujLiczbe(det));
        return det;
    }

    // ---------------------------------------------------------------------------------
    // SARRUS
    // ---------------------------------------------------------------------------------

    // Metoda pokazująca kroki dla metody Sarrusa
    public static void pokazKrokiSarrus(double[][] macierz, boolean dopiszWiersze) {
        JTextArea krokiArea = new JTextArea(20, 50);
        krokiArea.setEditable(false);

        JFrame krokiFrame = new JFrame("Kroki - Sarrus");
        krokiFrame.add(new JScrollPane(krokiArea));
        krokiFrame.setSize(600, 400);
        krokiFrame.setVisible(true);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                obliczWyznacznikSarrus(macierz, krokiArea, dopiszWiersze);
                return null;
            }
        };

        worker.execute();
    }

    public static double obliczWyznacznikSarrus(double[][] macierz, JTextArea krokiArea, boolean dopiszWiersze) {
        if (macierz.length != 3 || macierz[0].length != 3) {
            throw new IllegalArgumentException("Metoda Sarrusa działa tylko dla macierzy 3x3.");
        }

        double[][] rozszerzona = new double[3][5];

        if (dopiszWiersze) {
            // Dopisywanie dwóch pierwszych wierszy
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    rozszerzona[i][j] = macierz[i][j];
                    if (j < 2) {
                        rozszerzona[i][j + 3] = macierz[i][j]; // Kopiowanie dodatkowych kolumn
                    }
                }
            }
        } else {
            // Dopisywanie dwóch pierwszych kolumn
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    rozszerzona[i][j] = macierz[i][j];
                }
            }
            for (int i = 0; i < 3; i++) {
                rozszerzona[i][3] = macierz[i][0]; // Kopiowanie pierwszej kolumny
                rozszerzona[i][4] = macierz[i][1]; // Kopiowanie drugiej kolumny
            }
        }

        // Obliczanie przekątnych
        double suma1 = rozszerzona[0][0] * rozszerzona[1][1] * rozszerzona[2][2]
                + rozszerzona[0][1] * rozszerzona[1][2] * rozszerzona[2][3]
                + rozszerzona[0][2] * rozszerzona[1][3] * rozszerzona[2][4];
        appendToArea(krokiArea, "Suma iloczynów przekątnych: " + suma1);

        double suma2 = rozszerzona[0][4] * rozszerzona[1][3] * rozszerzona[2][2]
                + rozszerzona[0][3] * rozszerzona[1][2] * rozszerzona[2][1]
                + rozszerzona[0][2] * rozszerzona[1][1] * rozszerzona[2][0];
        appendToArea(krokiArea, "Suma iloczynów przeciwprzekątnych: " + suma2);

        double wynik = suma1 - suma2;
        appendToArea(krokiArea, "Wyznacznik (Suma1 - Suma2): " + formatujLiczbe(wynik));
        return wynik;
    }

    // ---------------------------------------------------------------------------------
    // METODY POMOCNICZE
    // ---------------------------------------------------------------------------------

    // Metoda do tworzenia podmacierzy (pomocnicza do obliczania wyznacznika)
    public static double[][] podmacierz(double[][] macierz, int wiersz, int kolumna) {
        int n = macierz.length;
        double[][] podmacierz = new double[n - 1][n - 1];

        int p = 0;
        for (int i = 0; i < n; i++) {
            if (i == wiersz) continue;
            int q = 0;
            for (int j = 0; j < n; j++) {
                if (j == kolumna) continue;
                podmacierz[p][q] = macierz[i][j];
                q++;
            }
            p++;
        }
        return podmacierz;
    }

    // Metoda pomocnicza do aktualizacji JTextArea w czasie rzeczywistym
    private static void appendToArea(JTextArea krokiArea, String message) {
        SwingUtilities.invokeLater(() -> krokiArea.append(message + "\n"));
    }

    // Metoda pomocnicza niwelująca problemy z wyświetlaniem liczb
    public static String formatujLiczbe(double liczba) {
        BigDecimal bd = new BigDecimal(Double.toString(liczba));
        bd = bd.setScale(5, RoundingMode.HALF_UP); // Zaokrąglij do 5 miejsc
        return bd.stripTrailingZeros().toPlainString(); // Usuń zbędne zera i .0
    }
}
