# Clinic App (Kotlin + XML Views)

Starter projektu aplikacji kliniki na Androida, przygotowany pod dalszy rozwój w architekturze MVVM.

## Funkcje
- Ekran główny „Klinika Zdrowia” z kartami menu
- Nawigacja między ekranami: home, appointments, history, settings
- Placeholdere ekranów gotowe do rozbudowy
- Reużywalny komponent karty menu oparty o XML (`card_menu.xml` + adapter RecyclerView)
- Modele danych dla pacjentów, wizyt i lekarzy
- Konfiguracja Hilt, Room, Navigation Components (Fragmenty) i Material 3 (XML)

## Technology Stack
- Kotlin 1.9.24
- Android SDK 34 (min 24, target 34)
- XML Views + Material Components
- Hilt (DI)
- Room (lokalna baza)
- Navigation Components (Fragment)

## Setup krok po kroku
1. Otwórz projekt w Android Studio (Hedgehog lub nowszy).
2. Upewnij się, że masz zainstalowane Android SDK 34.
3. Synchronizuj Gradle (`Sync Project with Gradle Files`).
4. Uruchom emulator lub podłącz urządzenie.
5. Kliknij **Run** dla modułu `app`.

## Struktura projektu
- `app/build.gradle.kts` – konfiguracja Android/XML Views/Hilt/Room
- `app/src/main/java/com/dominox/clinicapp/ClinicApp.kt` – klasa aplikacji z Hilt
- `app/src/main/java/com/dominox/clinicapp/MainActivity.kt` – Activity z hostem nawigacji
- `app/src/main/res/navigation/nav_graph.xml` – graph i trasy Fragmentów
- `app/src/main/java/com/dominox/clinicapp/ui/screens` – fragmenty ekranów
- `app/src/main/res/layout` – layouty XML
- `app/src/main/java/com/dominox/clinicapp/data/models` – modele danych
