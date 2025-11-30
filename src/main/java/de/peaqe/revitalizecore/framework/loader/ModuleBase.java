package de.peaqe.revitalizecore.framework.loader;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.logging.ModuleLogger;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 16:23 Uhr
 * *
 */

public abstract class ModuleBase {

    private RevitalizeCore revitalizeCore;
    private ModuleLogger logger;

    public final void setCore(RevitalizeCore core) {
        if (core == null) {
            throw new IllegalArgumentException("RevitalizeCore cannot be null!");
        }
        this.revitalizeCore = core;
    }

    public final RevitalizeCore getRevitalizeCore() {
        if (this.revitalizeCore == null) {
            throw new IllegalStateException(
                    this.getClass().getSimpleName() + " cannot be used before setCore() was called!"
            );
        }
        return this.revitalizeCore;
    }

    public final ModuleLogger getLogger() {
        if (this.logger == null) {
            if (this.revitalizeCore == null) {
                throw new IllegalStateException(
                        this.getClass().getSimpleName() + " getLogger() called before setCore()!"
                );
            }
            this.logger = this.revitalizeCore
                    .getRevitalizeLogger()
                    .getLogger(this.getClass().getSimpleName());
        }
        return this.logger;
    }
}
