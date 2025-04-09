**Autor**

**Imię i nazwisko:** Olaf Olejnik\
**Numer indeksu:** 122681\
**Uczelnia:** Społeczna Akademia Nauk, Łódź

**Opis programu**

Kalkulator Wyznacznika Macierzy to aplikacja graficzna napisana w języku
Java, umożliwiająca użytkownikowi obliczenie wyznacznika macierzy przy
wykorzystaniu różnych metod matematycznych. Program został
zaimplementowany z wykorzystaniem bibliotek Swing i oferuje intuicyjny
interfejs graficzny.

<p align="center">
  <img src="https://github.com/adadak/KalkulatorWyznacznikaMacierzy/blob/main/0.jpg" width="300">
  <img src="https://github.com/adadak/KalkulatorWyznacznikaMacierzy/blob/main/1.jpg" width="300">
</p>

**Funkcjonalności**

1.  **Wybór rozmiaru macierzy:** Użytkownik może wybrać rozmiar macierzy
    w zakresie od 1x1 do 6x6 za pomocą pola tekstowego.

2.  **Wprowadzenie danych:** Użytkownik może ręcznie wprowadzać wartości
    elementów macierzy lub automatycznie wygenerować liczby losowe z
    zakresu od -100 do 100.

3.  **Obliczanie wyznacznika:** Program obsługuje trzy metody
    obliczeniowe:

    - **Metoda Laplace\'a** (z rozwinięciem względem wierszy lub
      kolumn).

    - **Metoda eliminacji Gaussa.**

    - **Metoda Sarrusa** (dla macierzy 3x3).

4.  **Dynamiczne dopasowanie interfejsu:** Wielkość okna oraz liczba pól
    tekstowych dostosowują się do wybranego rozmiaru macierzy.

5.  **Wyświetlanie szczegółowych kroków:** Dla każdej metody
    obliczeniowej dostępny jest widok szczegółowy, pokazujący kolejne
    kroki obliczeń w osobnym oknie.

6.  **Walidacja danych:** Aplikacja weryfikuje poprawność wprowadzanych
    danych i wyświetla odpowiednie komunikaty błędów.

**Jak korzystać**

1.  **Uruchomienie programu:**

    - Program można uruchomić za pomocą klasy WyznacznikMacierzyGUI
      (metoda main).

2.  **Konfiguracja macierzy:**

    - W polu \"Rozmiar macierzy\" wprowadź wartość od 1 do 6, aby
      określić rozmiar macierzy.

    - Wypełnij pola tekstowe danymi lub skorzystaj z przycisku \"Wstaw
      losowe liczby\".

3.  **Wybór metody obliczeń:**

    - Z rozwijanej listy wybierz jedną z dostępnych metod
      obliczeniowych.

    - Dla metody Laplace\'a wybierz wiersz lub kolumnę, a następnie
      wskaż ich indeks, a dla Sarrusa sposób rozszerzenia macierzy.

4.  **Obliczenie wyznacznika:**

    - Kliknij przycisk \"Oblicz wyznacznik\", aby uzyskać wynik.

5.  **Szczegółowe kroki obliczeń:**

    - W przypadku metody Laplace\'a, Gaussa lub Sarrusa, szczegóły są
      wyświetlane w nowym oknie „Kroki".

**Więcej szczegółów w README.docx**
