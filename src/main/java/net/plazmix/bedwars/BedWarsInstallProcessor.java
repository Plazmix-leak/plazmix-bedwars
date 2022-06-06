package net.plazmix.bedwars;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.component.BedwarsGameStatsMysqlDatabase;
import net.plazmix.bedwars.util.ColorUtil;
import net.plazmix.bedwars.util.Functions;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class BedWarsInstallProcessor extends GameInstallerTask {

    @Getter PlazmixBedWars plugin;

    public BedWarsInstallProcessor(@NonNull PlazmixBedWars plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
        GamePluginService service = plugin.getService();

        service.setSetting(GameSetting.WEATHER_CHANGE, true);
        service.setSetting(GameSetting.CREATURE_SPAWN_GENERIC, false);
        service.setSetting(GameSetting.CREATURE_SPAWN_CUSTOM, false);
        service.setSetting(GameSetting.BLOCK_PHYSICS, false);

        settings.setCenter(service.getMapWorld().getSpawnLocation());
        settings.setUseOnlyTileBlocks(false);
        settings.setRadius(plugin.getConfig().getInt("install-radius", 200));

        plugin.getService().addGameDatabase(new BedwarsGameStatsMysqlDatabase());

        actions.addEntity(EntityType.ARMOR_STAND, entity -> {
            Location location = entity.getLocation();
            Block down = location.getBlock().getRelative(BlockFace.DOWN);

            if (down.getType() == Material.WOOL) {
                ChatColor color = ColorUtil.asChatColor(((Wool) down.getState().getData()).getColor());

                Functions.accept(GameTeam.getDefault(color)
                                .orElse(new GameTeam(13 + ThreadLocalRandom.current().nextInt(), color, ColorUtil.format(color, true))), service::registerTeam)
                        .getCache().set(GameConst.TEAM_SPAWN_LOCATION, location);

                Bukkit.getLogger().info("Локация спавна команды " + color.name() + " успешно зарегистрирована!");

                down.setType(Material.AIR);
            }

            entity.remove();
        });

        actions.addBlock(Material.CHEST, block -> {

            List<BlockState> traderLocations = plugin.getCache().getOrDefault(GameConst.TRADER_LOCATIONS, ArrayList::new);

            if(block.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.HARD_CLAY) {
                return;
            }

            traderLocations.add(block.getState());

            plugin.getCache().set(GameConst.TRADER_LOCATIONS, traderLocations);

            block.setType(Material.AIR);

            Bukkit.getLogger().info("Новая локация торговца успешно зарегистрирована!");
        });

        actions.addBlock(Material.TRAPPED_CHEST, block -> {
            List<BlockState> upgraderLocations = plugin.getCache().getOrDefault(GameConst.UPGRADER_LOCATIONS, ArrayList::new);

            upgraderLocations.add(block.getState());

            plugin.getCache().set(GameConst.UPGRADER_LOCATIONS, upgraderLocations);

            block.setType(Material.AIR);

            Bukkit.getLogger().info("Новая локация улучшателя успешно зарегистрирована! ");
        });

        actions.addBlock(Material.FURNACE, block -> {
            Location location = block.getLocation();
            Block down = location.clone().subtract(0, 1, 0).getBlock();

            switch (down.getType()) {
                case EMERALD_BLOCK: {
                    List<Location> opalGenerators = plugin.getCache().getOrDefault(GameConst.OPAL_GENERATOR_LOCATIONS, ArrayList::new);

                    opalGenerators.add(location);

                    plugin.getCache().set(GameConst.OPAL_GENERATOR_LOCATIONS, opalGenerators);

                    block.setType(Material.AIR);

                    Bukkit.getLogger().info("Новая локация генератора опалов успешно зарегистрирована!");

                    break;
                }

                case WOOL: {
                    ChatColor color = ColorUtil.asChatColor(((Wool) down.getState().getData()).getColor());

                    service.getTeamManager().getTeamsByColor(color)[0]
                            .getCache().set(GameConst.TEAM_GENERATOR_LOCATION, down.getLocation());

                    block.setType(Material.AIR);
                    down.setType(Material.AIR);

                    Bukkit.getLogger().info("Базовый генератор успешно зарегистрирован для команды " + color.name());

                    break;
                }
            }
        });

        actions.addBlock(Material.BED_BLOCK, block -> {
            service.getTeamManager().getTeamsByColor(ColorUtil.asChatColor(((Wool) block.getLocation().clone().subtract(0, 1, 0).getBlock().getState().getData()).getColor()))[0]
                    .getCache().set(GameConst.TEAM_BED_LOCATION, block.getLocation());
        });
    }

}