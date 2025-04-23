package org.example.realengine.input;

/**
 * Neměnný záznam (record) uchovávající stav relevantních vstupů v daném okamžiku.
 *
 * @param left  Zda je stisknuta klávesa pro pohyb doleva.
 * @param right Zda je stisknuta klávesa pro pohyb doprava.
 * @param up    Zda je stisknuta klávesa pro pohyb nahoru.
 * @param down  Zda je stisknuta klávesa pro pohyb dolů.
 * @param jump  Zda je stisknuta klávesa pro skok.
 */
public record InputSnapshot(
        boolean left,
        boolean right,
        boolean up,
        boolean down,
        boolean jump
) {

}