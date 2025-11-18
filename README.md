# RealEngine - 2D Platformer Game

Vítejte v RealEngine, 2D platformové hře a enginu napsaném v Javě s využitím knihovny Swing pro vykreslování. Tento projekt slouží jako ukázka jednoduchého herního enginu, který podporuje dynamické načítání map z obrázků, správu entit, základní fyziku a interaktivní herní prvky.

## Klíčové vlastnosti

*   **Dynamické načítání map**: Mapy jsou vytvářeny z jednoduchých `.png` souborů, kde každá barva pixelu odpovídá konkrétnímu typu dlaždice nebo objektu.
*   **Systém entit**: Zahrnuje hráče, různé typy nepřátel (chodící, skákající, Lakitu) a správce, který řídí jejich chování a interakce.
*   **Fyzika a kolize**: Jednoduchý fyzikální model s gravitací, skákáním a detekcí kolizí se světem.
*   **Interaktivní objekty**:
    *   Posuvné bedny
    *   Padající plošiny
    *   Pružiny pro vyšší skoky
    *   Teleporty
    *   Checkpoiny pro uložení postupu
*   **Kamera**: Sleduje pohyb hráče a zajišťuje plynulé posouvání herního světa.
*   **Audio**: Přehrávání hudby na pozadí pro jednotlivé mapy.
*   **Ukládání nejlepších časů**: Hra sleduje a ukládá nejlepší časy pro každou mapu do souboru `resources/saves/best_times.csv`.
*   **Menu pro výběr map**: Jednoduché uživatelské rozhraní pro výběr a načtení mapy.

## Ovládání

*   **Pohyb**: Šipky `←` `→` nebo klávesy `A` `D`
*   **Skok**: Šipka `↑`, `W` nebo `Mezerník`
*   **Krátký skok (Bunny Jump)**: `Shift`
*   **Pohyb po žebříku**: Šipky `↑` `↓` nebo klávesy `W` `S`
*   **Otevřít menu map**: `L`
*   **Přepnout textury/barvy (Debug)**: `P`
*   **Ukončit hru**: `ESC` (v menu map)

## Jak spustit projekt

Projekt je postaven pomocí Gradle. Pro spuštění postupujte následovně:

1.  Ujistěte se, že máte nainstalovanou Javu JDK verze 21 nebo novější.
2.  Naklonujte si repozitář.
3.  Otevřete terminál v kořenovém adresáři projektu.
4.  Spusťte hru pomocí Gradle wrapperu:

    ```bash
    ./gradlew run
    ```

    Nebo na Windows:

    ```bash
    ./gradlew.bat run
    ```

## Jak vytvořit vlastní mapu

Mapy jsou `.png` obrázky umístěné ve složce `resources/maps`. Engine interpretuje barvy pixelů a převádí je na herní objekty.

1.  Vytvořte nový `.png` soubor v grafickém editoru (např. GIMP, Aseprite, MS Paint).
2.  Rozměry obrázku určují velikost mapy v dlaždicích (1 pixel = 1 dlaždice).
3.  Použijte následující barvy (hex kódy) k "nakreslení" vaší mapy. Seznam všech dlaždic a jejich barev naleznete v souboru `src/main/java/org/example/realengine/map/ETile.java`.

    | Objekt                  | Barva (Hex) | Popis                               |
    | ----------------------- | ----------- | ----------------------------------- |
    | **Stěna/Zem**           |             |                                     |
    | Kámen                   | `#808080`   | Pevná překážka.                     |
    | Tráva                   | `#049625`   | Pevná překážka.                     |
    | Hlína                   | `#67201a`   | Pevná překážka.                     |
    | Cihla                   | `#CD5C5C`   | Pevná překážka.                     |
    | **Spawn pointy**        |             |                                     |
    | Start hráče             | `#FFFF00`   | Místo, kde se hráč objeví.          |
    | Běžný nepřítel          | `#FF1050`   | Spawn pro chodícího nepřítele.      |
    | Skákající nepřítel      | `#FFE969`   | Spawn pro skákajícího nepřítele.    |
    | Lakitu                  | `#FF69FF`   | Spawn pro Lakitu.                   |
    | **Interaktivní objekty**|             |                                     |
    | Bedna (posuvná)         | `#8B4513`   | Hráč ji může tlačit.                |
    | Žebřík (liána)          | `#00BFFF`   | Umožňuje šplhání.                   |
    | Pružina                 | `#FF0000`   | Vymrští hráče do výšky.             |
    | Padající plošina        | `#00FF99`   | Spadne chvíli poté, co na ni hráč stoupne. |
    | Checkpoint              | `#9F09FF`   | Uloží novou pozici pro respawn.     |
    | **Nebezpečí**           |             |                                     |
    | Láva                    | `#FF9900`   | Okamžitá smrt.                      |
    | Bodáky                  | `#AFAEFF`   | Okamžitá smrt.                      |
    | **Ostatní**             |             |                                     |
    | Prázdný prostor (nebe)  | `#FFFFFF`   | Průchozí prostor.                   |
    | Cíl úrovně              | `#FC38D8`   | Ukončí úroveň.                      |

4.  Uložte obrázek do složky `resources/maps`.
5.  Spusťte hru, stiskněte `L` pro otevření menu a vyberte vaši novou mapu.
