
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * © Olaf Olejnik, 122681, Społeczna Akademia Nauk, Łódź, Poland
 *
 * Klasa odpowiedzialna za warstwę GUI (interfejs graficzny).
 * Zawiera kod do wyświetlania okna, paneli, obsługi zdarzeń i wprowadzania danych.
 */
public class WyznacznikMacierzyGUI {

    // Metoda tworząca główne okno aplikacji
    public void stworzOkno() {
        JFrame frame = new JFrame("Kalkulator Wyznacznika Macierzy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(750, 500));
        frame.setResizable(true);

        // Panel główny
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Pole tekstowe do wprowadzania rozmiaru macierzy
        JTextField rozmiarField = new JTextField(5);

        // Dodanie filtra do rozmiarField, aby akceptował tylko cyfry od 1 do 6 lub pustą wartość
        ((PlainDocument) rozmiarField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = new StringBuilder(currentText).replace(offset, offset + length, text).toString();
                if (isAllowedInput(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    pokazBlad(fb);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = new StringBuilder(currentText).insert(offset, string).toString();
                if (isAllowedInput(newText)) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    pokazBlad(fb);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }

            private void pokazBlad(FilterBypass fb) {
                try {
                    fb.remove(0, fb.getDocument().getLength()); // Czyści całe pole
                    JOptionPane.showMessageDialog(null, "Dozwolone wartości to tylko liczby od 1 do 6!", "Błąd", JOptionPane.ERROR_MESSAGE);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }

            private boolean isAllowedInput(String newText) {
                String currentText = "";
                try {
                    currentText = rozmiarField.getText(0, rozmiarField.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                // Scal obecny tekst i nowy tekst
                String combinedText = currentText + newText;
                // Sprawdź, czy tekst składa się wyłącznie z unikalnych cyfr 1-6
                return combinedText.matches("^[1-6]{1,6}$") && hasUniqueCharacters(combinedText);
            }

            private boolean hasUniqueCharacters(String text) {
                return text.chars().distinct().count() == text.length();
            }

            private void pokazBlad() {
                JOptionPane.showMessageDialog(frame, "Dozwolone wartości to tylko liczby od 1 do 6!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel do wprowadzania danych macierzy
        JPanel macierzPanel = new JPanel();

        // Przycisk do obliczania wyznacznika
        JButton obliczPrzycisk = new JButton("Oblicz wyznacznik");

        // Pole wyboru metody obliczania
        String[] metody = {"Laplace", "Gauss", "Sarrus"};
        JComboBox<String> metodaCombo = new JComboBox<>(metody);

        // Pole wyboru wiersza lub kolumny dla metody Laplace'a
        JComboBox<String> wyborLaplaceCombo = new JComboBox<>(new String[]{"Wiersz", "Kolumna"});
        JComboBox<Integer> indeksCombo = new JComboBox<>();

        // Etykieta wynikowa
        JLabel wynikLabel = new JLabel("Wyznacznik: ");

        // Górny panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Rozmiar macierzy: "));
        topPanel.add(rozmiarField);

        // Przycisk "Wstaw losowe liczby"
        JButton losowePrzycisk = new JButton("Wstaw losowe liczby");
        losowePrzycisk.addActionListener(e -> {
            Component[] pola = macierzPanel.getComponents();
            for (Component pole : pola) {
                if (pole instanceof JTextField) {
                    int losowaLiczba = (int) (Math.random() * 201) - 100; // Losowa liczba od -100 do 100
                    ((JTextField) pole).setText(String.valueOf(losowaLiczba));
                }
            }
        });
        topPanel.add(losowePrzycisk);

        // Obsługa zmian w polu tekstowym rozmiarField
        rozmiarField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                generujMacierz();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                generujMacierz();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                generujMacierz();
            }

            private void generujMacierz() {
                macierzPanel.removeAll();
                indeksCombo.removeAllItems();
                try {
                    String input = rozmiarField.getText();

                    // Jeśli pole jest puste, po prostu zakończ działanie metody
                    if (input.isEmpty()) {
                        frame.revalidate();
                        frame.repaint();
                        return;
                    }

                    // Sprawdza, czy wprowadzono tylko dozwolone wartości (1-6)
                    if (!input.matches("[1-6]")) {
                        throw new IllegalArgumentException("Dozwolone rozmiary macierzy to tylko 1, 2, 3, 4, 5, 6.");
                    }

                    int rozmiar = Integer.parseInt(input);

                    macierzPanel.setLayout(new GridLayout(rozmiar, rozmiar));

                    for (int i = 0; i < rozmiar * rozmiar; i++) {
                        JTextField pole = new JTextField(3);
                        pole.setText("0");
                        macierzPanel.add(pole);
                    }

                    for (int i = 1; i <= rozmiar; i++) {
                        indeksCombo.addItem(i);
                    }

                    // Ustawienie rozmiaru okna względem liczby elementów w macierzy
                    int newHeight = 100 + rozmiar * 30; // Dostosuj wartość
                    int newWidth = 200 + rozmiar * 30;
                    SwingUtilities.invokeLater(() -> {
                        frame.setSize(Math.max(newWidth, 500), Math.max(newHeight, 400));
                        frame.revalidate();
                        frame.repaint();
                    });

                } catch (NumberFormatException ex) {
                    macierzPanel.removeAll();
                    frame.revalidate();
                    frame.repaint();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    rozmiarField.setText(""); // Czyści pole w przypadku błędu
                }
            }
        });

        // Obsługa przycisku obliczania wyznacznika
        obliczPrzycisk.addActionListener(e -> {
            Component[] pola = macierzPanel.getComponents();
            int rozmiar = (int) Math.sqrt(pola.length);

            // Wczytywanie danych z pól tekstowych
            double[][] macierz = new double[rozmiar][rozmiar];
            try {
                for (int i = 0; i < rozmiar; i++) {
                    for (int j = 0; j < rozmiar; j++) {
                        JTextField pole = (JTextField) pola[i * rozmiar + j];
                        macierz[i][j] = Double.parseDouble(pole.getText());
                    }
                }

                // Wybór metody obliczania
                String metoda = (String) metodaCombo.getSelectedItem();
                if ("Laplace".equals(metoda)) {
                    int indeks = (int) indeksCombo.getSelectedItem() - 1;
                    String wybor = (String) wyborLaplaceCombo.getSelectedItem();

                    if ("Wiersz".equalsIgnoreCase(wybor)) {
                        SwingWorker<Void, Void> worker = new SwingWorker<>() {
                            @Override
                            protected Void doInBackground() {
                                // Wyświetlanie kroku w osobnym oknie
                                WyznacznikMacierzyObliczenia.pokazKrokiLaplace(macierz, true, indeks);
                                return null;
                            }

                            @Override
                            protected void done() {
                                // Po zakończeniu wyświetlania kroków – oblicz i pokaż wynik
                                double wynik = WyznacznikMacierzyObliczenia.obliczWyznacznikLaplace(macierz, new JTextArea(), true, indeks);
                                wynikLabel.setText("Wyznacznik: " + WyznacznikMacierzyObliczenia.formatujLiczbe(wynik));
                            }
                        };
                        worker.execute();
                    } else if ("Kolumna".equalsIgnoreCase(wybor)) {
                        SwingWorker<Void, Void> worker = new SwingWorker<>() {
                            @Override
                            protected Void doInBackground() {
                                WyznacznikMacierzyObliczenia.pokazKrokiLaplace(macierz, false, indeks);
                                return null;
                            }

                            @Override
                            protected void done() {
                                double wynik = WyznacznikMacierzyObliczenia.obliczWyznacznikLaplace(macierz, new JTextArea(), false, indeks);
                                wynikLabel.setText("Wyznacznik: " + WyznacznikMacierzyObliczenia.formatujLiczbe(wynik));
                            }
                        };
                        worker.execute();
                    }
                } else if ("Gauss".equals(metoda)) {
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            WyznacznikMacierzyObliczenia.pokazKrokiGauss(macierz);
                            return null;
                        }

                        @Override
                        protected void done() {
                            double wynik = WyznacznikMacierzyObliczenia.obliczWyznacznikGauss(macierz, new JTextArea());
                            wynikLabel.setText("Wyznacznik: " + WyznacznikMacierzyObliczenia.formatujLiczbe(wynik));
                        }
                    };
                    worker.execute();
                } else if ("Sarrus".equals(metoda)) {
                    if (macierz.length != 3 || macierz[0].length != 3) {
                        JOptionPane.showMessageDialog(frame, "Metoda Sarrusa działa tylko dla macierzy 3x3!", "Błąd", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            String wybor = (String) wyborLaplaceCombo.getSelectedItem();
                            boolean dopiszWiersze = "Wiersz".equalsIgnoreCase(wybor);
                            WyznacznikMacierzyObliczenia.pokazKrokiSarrus(macierz, dopiszWiersze);
                            return null;
                        }

                        @Override
                        protected void done() {
                            String wybor = (String) wyborLaplaceCombo.getSelectedItem();
                            boolean dopiszWiersze = "Wiersz".equalsIgnoreCase(wybor);
                            double wynik = WyznacznikMacierzyObliczenia.obliczWyznacznikSarrus(macierz, new JTextArea(), dopiszWiersze);
                            wynikLabel.setText("Wyznacznik: " + WyznacznikMacierzyObliczenia.formatujLiczbe(wynik));
                        }
                    };
                    worker.execute();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Wprowadź poprawne liczby w macierzy!", "Błąd", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Dolny panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Metoda: "));
        bottomPanel.add(metodaCombo);
        metodaCombo.addActionListener(e -> {
            int rozmiar = (int) Math.sqrt(macierzPanel.getComponentCount()); // Oblicz rozmiar macierzy
            boolean isLaplace = "Laplace".equals(metodaCombo.getSelectedItem());
            boolean isSarrus = "Sarrus".equals(metodaCombo.getSelectedItem());

            if (isSarrus) {
                if (rozmiar != 3) {
                    JOptionPane.showMessageDialog(frame, "Metoda Sarrusa działa tylko dla macierzy 3x3!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    metodaCombo.setSelectedIndex(0); // Przywróć domyślną metodę (Laplace)
                } else {
                    rozmiarField.setEnabled(false); // Zablokuj zmianę rozmiaru macierzy
                }
            } else {
                rozmiarField.setEnabled(true); // Odblokuj zmianę rozmiaru dla innych metod
            }

            wyborLaplaceCombo.setEnabled(isLaplace || isSarrus);
            indeksCombo.setEnabled(isLaplace);
        });

        bottomPanel.add(new JLabel("Rozwinięcie: "));
        bottomPanel.add(wyborLaplaceCombo);
        bottomPanel.add(new JLabel("Indeks: "));
        bottomPanel.add(indeksCombo);
        bottomPanel.add(obliczPrzycisk);
        bottomPanel.add(wynikLabel);

        // Panel na sygnaturę
        JPanel sygnaturaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel sygnaturaLabel = new JLabel("© Olaf Olejnik, 122681, Społeczna Akademia Nauk, Łódź, Poland");
        sygnaturaPanel.add(sygnaturaLabel);

        // Kontener nadrzędny dla bottomPanel i sygnaturaPanel
        JPanel dolnyKontener = new JPanel();
        dolnyKontener.setLayout(new BoxLayout(dolnyKontener, BoxLayout.Y_AXIS));
        dolnyKontener.add(bottomPanel);
        dolnyKontener.add(sygnaturaPanel);

        // Dodawanie paneli do głównego okna
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(macierzPanel), BorderLayout.CENTER);
        panel.add(dolnyKontener, BorderLayout.SOUTH);

        // Ustawienia okna
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
