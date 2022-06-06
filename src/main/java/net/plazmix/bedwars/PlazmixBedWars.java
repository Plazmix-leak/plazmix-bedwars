package net.plazmix.bedwars;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.command.ShopCommand;
import net.plazmix.bedwars.state.EndingState;
import net.plazmix.bedwars.state.IngameState;
import net.plazmix.bedwars.state.WaitingState;
import net.plazmix.bedwars.util.BedWarsMode;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstaller;
import net.plazmix.game.installer.GameInstallerTask;
import org.bukkit.event.HandlerList;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class PlazmixBedWars extends GamePlugin {

    @Getter private static PlazmixBedWars instance;

    BedWarsMode bedWarsMode;

    @Override
    public GameInstallerTask getInstallerTask() {
        return new BedWarsInstallProcessor(this);
    }

    @Override
    protected void handleEnable() {
        (instance = this).saveDefaultConfig();

        bedWarsMode = BedWarsMode.valueOf(getConfig().getString("mode", "SOLO").toUpperCase());

        configureService();

        GameInstaller.create().executeInstall(getInstallerTask());

        PlazmixApi.registerCommand(this, new ShopCommand());
    }

    @Override
    protected void handleDisable() {
        getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    private void configureService() {
        service.setGameName("BedWars");
        service.setMapName(getConfig().getString("map-name"));
        service.setMaxPlayers(bedWarsMode.getTeamCount() * bedWarsMode.getTeamSize());
        service.setServerMode(bedWarsMode.getFormat());
        service.setMaxPlayers(bedWarsMode.getTeamCount() * bedWarsMode.getTeamSize());

        service.registerState(new WaitingState(this));
        service.registerState(new IngameState(this));
        service.registerState(new EndingState(this));
    }

    @Override
    public void broadcastMessage(@NonNull String message) {
        super.broadcastMessage(GameConst.PREFIX + message);
    }
}
