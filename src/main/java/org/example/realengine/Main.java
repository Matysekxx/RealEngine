package org.example.realengine;

/**
 * Defines the main entry point for the RealEngine application.
 *
 * <h2>How to Create a Custom Map Image:</h2>
 * <ol>
 *   <li>Create a new image using any image editing software (e.g., Paint, GIMP, Photoshop).</li>
 *   <li>Set the dimensions of the image to match the desired map size in tiles (e.g., 80 pixels wide for 80 tiles). Each pixel represents one tile.</li>
 *   <li>Use the following exact RGB hex color codes to draw the map layout. Each pixel's color determines the object placed at that tile location:
 *     <ul>
 *       <li><code>#000000</code> (Black): Wall (Solid, impassable block)</li>
 *       <li><code>#333333</code> (Dark Gray): Border (Impassable boundary, like Wall)</li>
 *       <li><code>#8B4513</code> (Brown): Box (Pushable object)</li>
 *       <li><code>#AAAAAA</code> (Gray): Spikes (Damaging hazard)</li>
 *       <li><code>#FF9900</code> (Orange): Hazard Liquid (Lava/Water, damaging)</li>
 *       <li><code>#FFFF00</code> (Yellow): Player Spawn Location (Starting point for the player)</li>
 *       <li><code>#FFFFFF</code> (White): Empty Space (Background, passable)</li>
 *       <li><code>#00FF00</code> (Green): Spring (Launches player upwards)</li>
 *       <li><code>#B8860B</code> (Gold): Honey (Sticky surface, slows player)</li>
 *       <li><code>#00BFFF</code> (Blue): Ladder (Climbable surface)</li>
 *       <li><code>#8B0000</code> (Dark Red): Trap (Temporary platform, falls after contact)</li>
 *       <li><code>#87CEEB</code> (Sky Blue): Ignored (Can be used as visual background in editor, treated as Empty Space)</li>
 *       <li><em>Any other color will likely be treated as Empty Space.</em></li>
 *     </ul>
 *   </li>
 *   <li>Save the image as a PNG file (e.g., "my_map.png").</li>
 *   <li>Load the map in the game (Note: Map loading from PNG is not yet implemented in the provided code snippets, the game currently uses `createChallengeMap`).</li>
 * </ol>
 */
public class Main {
    /**
     * The main method that starts the game execution.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Execute.execute();


    }
}