package org.example.realengine.map;

import org.example.realengine.entity.Entity;
import org.example.realengine.entity.Player;

import java.util.List;

/**
 * Výsledek načtení mapy, obsahuje samotnou mapu a nalezené entity.
 *
 * @param map      Načtená instance {@link RMap}.
 * @param entities Seznam všech entit vytvořených ze spawn pointů na mapě.
 * @param player   Odkaz na vytvořenou entitu hráče (pokud byla nalezena na mapě), jinak `null`.
 */
public record MapLoadResult(RMap map, List<Entity> entities, Player player) {
}