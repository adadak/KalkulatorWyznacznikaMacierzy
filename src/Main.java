
import javax.swing.SwingUtilities;

/**
 * © Olaf Olejnik, 122681, Społeczna Akademia Nauk, Łódź, Poland
 *
 * Klasa uruchamiająca główną aplikację (punkt wejścia).
 */
public class Main {
    // Główna metoda uruchamiająca aplikację
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WyznacznikMacierzyGUI().stworzOkno());
    }
}
