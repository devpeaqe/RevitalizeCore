package de.peaqe.revitalizecore.modules.coins;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import de.peaqe.revitalizecore.framework.loader.ModuleBase;
import de.peaqe.revitalizecore.modules.coins.commands.CoinsCommand;
import de.peaqe.revitalizecore.utils.MessageUtil;
import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 01:18 Uhr
 * *
 */

@Getter
@RevitalizeModule(name = "coins")
public class CoinsModule extends ModuleBase {

    private MessageUtil messageUtil;

    public static final Integer DEFAULT_COINS = 1000;

    public CoinsModule() {
    }

    public void onLoad(RevitalizeCore revitalizeCore) {
        this.setCore(revitalizeCore);
        this.messageUtil = new MessageUtil(this.getRevitalizeCore());
    }

    public void onEnable(RevitalizeCore revitalizeCore) {
        new CoinsCommand(this);
        this.getLogger().info("enabled");
    }

}
