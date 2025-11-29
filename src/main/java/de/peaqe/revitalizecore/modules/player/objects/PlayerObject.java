package de.peaqe.revitalizecore.modules.player.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:54 Uhr
 * *
 */

@Data
@AllArgsConstructor
public class PlayerObject {

    private final String name;
    private final UUID uniqueId;
    private int coins;

}
