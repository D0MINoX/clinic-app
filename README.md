# Clinic App (Kotlin + Jetpack Compose)

Starter projektu aplikacji kliniki na Androida, przygotowany pod dalszy rozwój w architekturze MVVM.

## Funkcje
- Ekran główny „Klinika Zdrowia” z kartami menu
- Nawigacja między ekranami: home, appointments, history, settings
- Placeholdere ekranów gotowe do rozbudowy
- Reużywalny komponent `MenuCard`
- Modele danych dla pacjentów, wizyt i lekarzy
- Konfiguracja Hilt, Room, Navigation Compose i Material 3

## Technology Stack
- Kotlin 1.9.20
- Android SDK 34 (min 24, target 34)
- Jetpack Compose + Material 3 (BOM 2023.10.01)
- Hilt (DI)
- Room (lokalna baza)
- Navigation Compose

## Setup krok po kroku
1. Otwórz projekt w Android Studio (Hedgehog lub nowszy).
2. Upewnij się, że masz zainstalowane Android SDK 34.
3. Synchronizuj Gradle (`Sync Project with Gradle Files`).
4. Uruchom emulator lub podłącz urządzenie.
5. Kliknij **Run** dla modułu `app`.

## Struktura projektu
- `app/build.gradle.kts` – konfiguracja Android/Compose/Hilt/Room
- `app/src/main/java/com/dominox/clinicapp/ClinicApp.kt` – klasa aplikacji z Hilt
- `app/src/main/java/com/dominox/clinicapp/MainActivity.kt` – punkt wejścia UI
- `app/src/main/java/com/dominox/clinicapp/navigation/ClinicNavigation.kt` – graph i trasy
- `app/src/main/java/com/dominox/clinicapp/ui/screens` – ekrany
- `app/src/main/java/com/dominox/clinicapp/ui/components` – komponenty UI
- `app/src/main/java/com/dominox/clinicapp/data/models` – modele danych

## Kotlin basics (dla początkujących)
- `val` – niezmienna referencja
- `var` – zmienna referencja
- Funkcje: `fun greet(name: String): String = "Cześć $name"`
- Nullable: `String?` może być `null`, `String` nie może

## Jetpack Compose basics
- UI budujesz funkcjami oznaczonymi `@Composable`
- Komponenty Material: `Text`, `Card`, `Button`, `TopAppBar`
- Stan trzymasz np. przez `remember { mutableStateOf(...) }`

## Jak rozbudować projekt
- Dodaj nowy ekran: utwórz composable i nową trasę w `ClinicNavigation.kt`
- Dodaj ViewModel: `@HiltViewModel`, `StateFlow`, podpięcie do UI
- Rozbij warstwy na `data/domain/presentation` gdy projekt rośnie

## Room - przykładowy kierunek
- Dodaj `@Dao` z metodami `@Insert`, `@Query`, `@Delete`
- Utwórz `RoomDatabase` z encjami `Appointment`, `Patient`
- Wstrzyknij DB przez Hilt do repozytorium

## Integracja API (Retrofit)
1. Dodaj zależności Retrofit + OkHttp.
2. Zdefiniuj interfejs endpointów (`@GET`, `@POST`).
3. Utwórz DTO i mapowanie na modele domenowe.
4. Obsłuż błędy i stany ładowania w ViewModelu.

## Testing examples
- Unit test ViewModela: testuj logikę i stany.
- Compose UI test: sprawdź czy `MenuCard` i nagłówki są widoczne.
- Instrumentation test: smoke test uruchomienia `MainActivity`.

## Troubleshooting
- **Gradle sync failed**: sprawdź wersję Android Gradle Plugin i SDK 34.
- **KSP/Hilt errors**: wykonaj `Clean Project` i ponowny build.
- **Compose preview nie działa**: sprawdź zgodność Kotlin + Compose compiler.
