package net.plazmix.bedwars.state;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.component.*;
import net.plazmix.bedwars.util.*;
import net.plazmix.game.GameCache;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.protocollib.entity.impl.FakeVillager;
import net.plazmix.protocollib.team.ProtocolTeam;
import net.plazmix.utility.ItemUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class IngameState extends GameState {

    @Getter
    final PlazmixBedWars plugin;

    final Map<GameUser, Long> tempGhosts = new ConcurrentHashMap<>();
    final Map<Player, BiContainer<Player, Long>> lastHitMap = new ConcurrentHashMap<>();
    final Set<Location> placedBlocks = new HashSet<>();
    final Collection<GameTeam> loadedTeams = new ArrayList<>();
    final Map<Integer, String> teamStatusMap = new HashMap<>();
    int secondsToBedBreak = 1500;
    int secondsToDragonSpawn = 300;
    int secondsToTie = 300;
    boolean isBedBrooked = false;
    boolean isDragonSpawned = false;
    int secs = 60;

    public IngameState(@NonNull PlazmixBedWars plugin) {
        super(plugin, "Идёт игра", false);

        this.plugin = plugin;
    }

    @Override
    protected void onStart() {
        getPlugin().getCache().set(GameConst.IS_TIE, false);

        registerAndRunGenerators();

        GamePluginService service = plugin.getService();
        Collection<Integer> teamIndexes = new ArrayList<>();

        service.getMapWorld().setDifficulty(Difficulty.EASY);
        service.setSetting(GameSetting.INTERACT_BLOCK, true);
        service.setSetting(GameSetting.INTERACT_ITEM, true);
        service.setSetting(GameSetting.BLOCK_BREAK, true);
        service.setSetting(GameSetting.BLOCK_PLACE, true);
        service.setSetting(GameSetting.PLAYER_DAMAGE, true);
        service.setSetting(GameSetting.ENTITY_EXPLODE, true);
        service.setSetting(GameSetting.PLAYER_PICKUP_ITEM, true);
        service.setSetting(GameSetting.PLAYER_DROP_ITEM, true);
        service.setSetting(GameSetting.CREATURE_SPAWN_GENERIC, true);

        service.throwPlayersToTeams(plugin.getBedWarsMode().getTeamSize(), service.getLoadedTeams());

        GameSchedulers.runTimer(20L, 20L, () -> {
            if(secondsToBedBreak == 0 && !isBedBrooked) {
                service.getLoadedTeams().forEach(team -> {
                    Block block = team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).getBlock();
                    if(block.getLocation().subtract(1, 0, 0).getBlock().getType() == Material.BED_BLOCK)
                        block.getLocation().subtract(1, 0, 0).getBlock().setType(Material.AIR);
                    else if(block.getLocation().subtract(-1, 0, 0).getBlock().getType() == Material.BED_BLOCK)
                        block.getLocation().subtract(-1, 0, 0).getBlock().setType(Material.AIR);
                    else if(block.getLocation().subtract(0, 0, 1).getBlock().getType() == Material.BED_BLOCK)
                        block.getLocation().subtract(0, 0, 1).getBlock().setType(Material.AIR);
                    else if(block.getLocation().subtract(0, 0, -1).getBlock().getType() == Material.BED_BLOCK)
                        block.getLocation().subtract(0, 0, -1).getBlock().setType(Material.AIR);
                    isBedBrooked = true;
                    block.setType(Material.AIR);
                    team.getCache().set(GameConst.TEAM_HAS_BED, false);
                    teamStatusMap.put(team.getTeamIndex(), team.getChatColor() + "" + team.getPlayers().size());
                });
                Bukkit.broadcastMessage(GameConst.PREFIX + "§c§lВсе кровати были уничтожены!");
            } else if(!isBedBrooked) {
                secondsToBedBreak = secondsToBedBreak - 1;
                secs = secs - 1;
                String time;
                if(secs < 10)
                    time = "§cУничт. кроватей §e" + TimeUnit.SECONDS.toMinutes(secondsToBedBreak) + ":" + "0" + secs;
                else
                    time = "§cУничт. кроватей §e" + TimeUnit.SECONDS.toMinutes(secondsToBedBreak) + ":" + secs;
                getPlugin().getCache().set(GameConst.TIME, time);
                if(secs == 0) {
                    secs = 60;
                }
            } else if(secondsToDragonSpawn == 0 && !isDragonSpawned) {
                service.getLoadedTeams().forEach(team -> {
                    Location dragonSpawnLoc = team.getCache().getLocation(GameConst.TEAM_SPAWN_LOCATION).add(0, 30, 0);
                    dragonSpawnLoc.getWorld().spawnEntity(dragonSpawnLoc, EntityType.ENDER_DRAGON);
                });
                isDragonSpawned = true;
                Bukkit.broadcastMessage(GameConst.PREFIX + "§c§lДраконы были заспавнены!");
            } else if(!isDragonSpawned) {
                secondsToDragonSpawn = secondsToDragonSpawn - 1;
                secs = secs - 1;
                String time;
                if(secs < 10)
                    time = "§cСпавн дракона §e" + TimeUnit.SECONDS.toMinutes(secondsToDragonSpawn) + ":" + "0" + secs;
                else
                    time = "§cСпавн дракона §e" + TimeUnit.SECONDS.toMinutes(secondsToDragonSpawn) + ":" + secs;
                getPlugin().getCache().set(GameConst.TIME, time);
                if(secs == 0) {
                    secs = 60;
                }
            } else if(secondsToTie == 0) {
                getPlugin().getCache().set(GameConst.IS_TIE, true);
                nextStage();
            } else {
                secondsToTie = secondsToTie - 1;
                secs = secs - 1;
                String time;
                if(secs < 10)
                    time = "§cКонец игры §e" + TimeUnit.SECONDS.toMinutes(secondsToTie) + ":" + "0" + secs;
                else
                    time = "§cКонец игры §e" + TimeUnit.SECONDS.toMinutes(secondsToTie) + ":" + secs;
                getPlugin().getCache().set(GameConst.TIME, time);
                if(secs == 0) {
                    secs = 60;
                }
            }
        });

        for (GameTeam team : plugin.getService().getLoadedTeams()) {
            team.getPlayers().forEach(user -> {
                user.getBukkitHandle().spigot().setCollidesWithEntities(true);
                GameScoreBoard.INGAME.createAndChange(user);
                user.getBukkitHandle().setGameMode(GameMode.SURVIVAL);
                ProtocolTeam.findEntry(user.getBukkitHandle()).setPrefix(team.getChatColor() + "§l" + ColorUtil.asTeamColorName(ColorUtil.asDyeColor(team.getChatColor())).charAt(0) + ChatColor.RESET + team.getChatColor() + " ");
                respawn(user);
            });


            if(team.getPlayers().size() == 0) {
                Bukkit.getLogger().info(team.getCache().get(GameConst.TEAM_BED_LOCATION).toString());

                team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).getBlock().setType(Material.AIR);
                team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).subtract(1, 0, 0).getBlock().setType(Material.AIR);
                team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).subtract(-1, 0, 0).getBlock().setType(Material.AIR);
                team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).subtract(0, 0, 1).getBlock().setType(Material.AIR);
                team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).subtract(0, 0, -1).getBlock().setType(Material.AIR);

                teamIndexes.add(team.getTeamIndex());
            } else {
                team.getCache().set(GameConst.IS_TEAM_ALIVE, true);
            }
        }

        teamIndexes.forEach(teamIndex -> service.getTeamManager().getLoadedTeamMap().remove(teamIndex));

        plugin.getService().getLoadedTeams().forEach(team -> {
            loadedTeams.add(team);
            teamStatusMap.put(team.getTeamIndex(), team.getChatColor() + "⬛");
        });

        getPlugin().getCache().set(GameConst.GAME_LOADED_TEAMS, loadedTeams);
        getPlugin().getCache().set(GameConst.INGAME_TEAM_STATUSES, teamStatusMap);

        GameSchedulers.runTimer(20L, 20L, () -> {
            for (Map.Entry<GameUser, Long> entry : tempGhosts.entrySet()) {
                long time = entry.getValue() - System.currentTimeMillis();
                GameUser user = entry.getKey();

                if(time <= 0) {
                    respawn(user);
                } else {
                    user.getBukkitHandle().sendTitle("§a" + TimeUnit.MILLISECONDS.toSeconds(time) + " секунд", "§eдо возрождения");
                }
            }
            lastHitMap.forEach((player, lastHitContainer) -> {
                if(lastHitContainer.secondValue() <= System.currentTimeMillis()) {
                    lastHitMap.remove(lastHitContainer.firstValue());
                }
            });
        });

        plugin.getCache().getList(GameConst.TRADER_LOCATIONS, BlockState.class).forEach(block -> {
            Location loc = block.getLocation().add(0.5, 0, 0.5);
            float yaw = (float) MathUtil.asYaw(block.getData().toString().split("\\s+")[2]);

            FakeVillager fakeVillager = new FakeVillager(loc);
            fakeVillager.setClickAction(GameInventory.SHOP_FAVOURITES::createAndOpen);
            fakeVillager.look(yaw, 0);
            loc.setYaw(yaw);
            fakeVillager.spawn();
            SimpleHolographic name = new SimpleHolographic(loc);
            name.addOriginalHolographicLine("§6§lМагазин");
            name.spawn();

            plugin.getLogger().info("Spawning trader on location " + loc.getX() + " " + loc.getZ());
        });
        plugin.getCache().getList(GameConst.UPGRADER_LOCATIONS, BlockState.class).forEach(block -> {
            Location loc = block.getLocation().add(0.5, 0, 0.5);
            float yaw = (float) MathUtil.asYaw(block.getData().toString().split("\\s+")[2]);

            FakeVillager fakeVillager = new FakeVillager(loc);
            fakeVillager.setClickAction(GameInventory.UPGRADER::createAndOpen);
            fakeVillager.look(yaw, 0);
            loc.setYaw(yaw);
            fakeVillager.spawn();
            SimpleHolographic name = new SimpleHolographic(loc);
            name.addOriginalHolographicLine("§6§lУлучшения");
            name.spawn();

            plugin.getLogger().info("Spawning upgrader on location " + loc.getX() + " " + loc.getZ());
        });
    }

    @Override
    protected void onShutdown() {}

    private void registerAndRunGenerators() {
        plugin.getService().getLoadedTeams().stream().map(GameTeam::getCache).map(cache -> cache.getLocation(GameConst.TEAM_GENERATOR_LOCATION)).forEach(location -> {
            ResourceGenerator.create(Resource.BRONZE, location);
            ResourceGenerator.create(Resource.RUBY, location);
        });

        GameSchedulers.runTimer(1200L, 1200L, () -> {
            plugin.getCache().getList(GameConst.OPAL_GENERATOR_LOCATIONS, Location.class).forEach(loc -> {
                new ResourceGenerator(loc, Resource.OPAL).spawnResource();
            });
        });

        GameSchedulers.runTimer(60L, 60L, () -> {
            plugin.getService().getLoadedTeams().stream().map(GameTeam::getCache).map(cache -> cache.getLocation(GameConst.TEAM_GENERATOR_LOCATION)).forEach(location -> {
                new ResourceGenerator(location, Resource.BRONZE).spawnResource();;
            });
        });

        GameSchedulers.runTimer(400L, 400L, () -> {
            plugin.getService().getLoadedTeams().stream().map(GameTeam::getCache).map(cache -> cache.getLocation(GameConst.TEAM_GENERATOR_LOCATION)).forEach(location -> {
                new ResourceGenerator(location, Resource.RUBY).spawnResource();;
            });
        });
    }

    private static void equipArmor(@NonNull GameUser user) {
        Color color = ColorUtil.asDyeColor(user.getCurrentTeam().getChatColor()).getColor();
        PlayerInventory inventory = user.getBukkitHandle().getInventory();
        ArmorType armorType = user.getCache().getOrDefault(GameConst.PLAYER_ARMOR_TYPE, () -> ArmorType.LEATHER);

        inventory.setHelmet(PlazmixApi.newItemBuilder(Material.LEATHER_HELMET).setLeatherColor(color).addEnchantment(Enchantment.WATER_WORKER, 1).setUnbreakable(true).build());
        inventory.setLeggings(PlazmixApi.newItemBuilder(Material.LEATHER_LEGGINGS).setLeatherColor(color).setUnbreakable(true).build());

        ItemUtil.ItemBuilder chestplateBuilder = PlazmixApi.newItemBuilder(armorType.getChestplateType()).setUnbreakable(true);
        ItemUtil.ItemBuilder bootsBuilder = PlazmixApi.newItemBuilder(armorType.getBootsType()).setUnbreakable(true);

        if(armorType == ArmorType.LEATHER) {
            chestplateBuilder.setLeatherColor(color);
            bootsBuilder.setLeatherColor(color);
        }

        inventory.setChestplate(chestplateBuilder.build());
        inventory.setBoots(bootsBuilder.build());

        int protection = user.getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.PROTECTION, 0);

        if(protection > 0) {
            ItemStack[] armor = inventory.getArmorContents();

            for (ItemStack item : armor) {
                item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            }

            inventory.setArmorContents(armor);
        }
    }

    @EventHandler
    public void onPlayerDamageByBlock(EntityDamageByBlockEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();

        if(event.getDamage() >= player.getHealth()) {
            event.setDamage(0);

            for (ItemStack item : player.getInventory()) {
                if(item.getType() == Material.SKULL_ITEM) player.getWorld().dropItemNaturally(event.getDamager().getLocation().clone().add(0, 1, 0), item);
            }

            kill(GameUser.from(player));
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageEvent event) {
        boolean isFallDamage = false;
        EntityDamageByEntityEvent damageByEntityEvent;
        Player player = (Player) event.getEntity();
        if(GameUser.from((Player) event.getEntity()).isGhost()) return;

        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            isFallDamage = true;
        if(!isFallDamage) {
            damageByEntityEvent = (EntityDamageByEntityEvent) event;
            if (event.getEntityType() != EntityType.PLAYER || damageByEntityEvent.getDamager().getType() != EntityType.PLAYER)
                return;
            Player damager = (Player) damageByEntityEvent.getDamager();
            if(event.getDamage() >= player.getHealth()) {
                for (ItemStack item : player.getInventory()) {
                    if (item == null)
                        continue;
                    if (item.getType() == Material.SKULL_ITEM) {
                        damager.getInventory().addItem(item);
                    }
                }

                event.setCancelled(true);

                boolean finalKill = kill(GameUser.from(player));
                GameUser.from(damager).getCache().increment(GameConst.KILLS_PLAYER_DATA);

                if(finalKill) {
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e был убит игроком " + GameUser.from(damager).getCurrentTeam().getChatColor() + GameUser.from(damager).getName() + " §f(§b§lФинальное убийство§f)");
                    GameUser.from(damager).getCache().set(GameConst.PLAYER_FINAL_KILLS, GameUser.from(damager).getCache().getInt(GameConst.PLAYER_FINAL_KILLS) + 1);
                    GameUser.from(damager).getCache().set(GameConst.PLAYER_EARNED_POINTS, GameUser.from(damager).getCache().getInt(GameConst.PLAYER_EARNED_POINTS) + 2);
                    damager.sendMessage("§e+2 поинта (Финальное убийство)");

                    GameTeam team = GameUser.from(player).getCurrentTeam();
                    team.removePlayer(GameUser.from(player));
                } else {
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e был убит игроком " + GameUser.from(damager).getCurrentTeam().getChatColor() + GameUser.from(damager).getName());
                    GameUser.from(damager).getCache().set(GameConst.PLAYER_KILLS, GameUser.from(damager).getCache().getInt(GameConst.PLAYER_KILLS) + 1);
                    GameUser.from(damager).getCache().set(GameConst.PLAYER_EARNED_POINTS, GameUser.from(damager).getCache().getInt(GameConst.PLAYER_EARNED_POINTS) + 1);
                    damager.sendMessage("§e+1 поинт (Убийство)");
                }
            } else {
                lastHitMap.put(player, new BiContainer<>(damager, System.currentTimeMillis() + 10_000L));
            }
        } else {
            if(event.getDamage() >= player.getHealth()) {
                event.setCancelled(true);

                boolean finalKill = kill(GameUser.from(player));

                if(finalKill) {
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e разбился");

                    GameTeam team = GameUser.from(player).getCurrentTeam();
                    team.removePlayer(GameUser.from(player));
                } else {
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e разбился");
                }
            }
        }
    }

    private boolean kill(GameUser gameUser) {
        gameUser.setGhost(true);
        PlayerUtil.setInvulnerable(gameUser.getBukkitHandle(), true);

        if(gameUser.getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_HAS_BED, () -> true)) {
            tempGhosts.put(gameUser, System.currentTimeMillis() + 5000L);
            return false;
        } else {
            if (gameUser.getCurrentTeam() != null) {
                teamStatusMap.put(gameUser.getCurrentTeam().getTeamIndex(), gameUser.getCurrentTeam().getChatColor() + "✘");
                gameUser.getCurrentTeam().getCache().set(GameConst.IS_TEAM_ALIVE, false);
                plugin.broadcastMessage("§eКоманда " + gameUser.getCurrentTeam().getTeamName() + "§e была уничтожена!");
                getPlugin().getCache().set(GameConst.INGAME_TEAM_STATUSES, teamStatusMap);
                Collection<GameTeam> aliveTeams = getPlugin().getService().getLoadedTeams()
                        .stream()
                        .filter(team -> team.getCache().get(GameConst.IS_TEAM_ALIVE))
                        .collect(Collectors.toSet());

                if(aliveTeams.size() <= 1) {
                    nextStage();
                }
            }
            return true;
        }
    }

    private void respawn(GameUser gameUser) {
        gameUser.setGhost(false);
        PlayerUtil.setInvulnerable(gameUser.getBukkitHandle(), false);

        equipArmor(gameUser);
        gameUser.getBukkitHandle().teleport(gameUser.getCurrentTeam().getCache().getLocation(GameConst.TEAM_SPAWN_LOCATION));
        gameUser.getBukkitHandle().setGameMode(GameMode.SURVIVAL);
        gameUser.getBukkitHandle().getActivePotionEffects().forEach(potionEffect -> gameUser.getBukkitHandle().removePotionEffect(potionEffect.getType()));
        GameHotBar.INGAME.setFor(gameUser.getBukkitHandle());
        int sharpness = gameUser.getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.SHARPNESS, 0);
        int fastdigging = gameUser.getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.FAST_DIGGING, 0);

        if(sharpness != 0)
            gameUser.getBukkitHandle().getInventory().getItem(0).addEnchantment(Enchantment.DAMAGE_ALL, sharpness);
        if(fastdigging != 0)
            gameUser.getBukkitHandle().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, fastdigging));
        ProtocolTeam.findEntry(gameUser.getBukkitHandle()).setPrefix(gameUser.getCurrentTeam().getChatColor() + "§l" + ColorUtil.asTeamColorName(ColorUtil.asDyeColor(gameUser.getCurrentTeam().getChatColor())).charAt(0) + ChatColor.RESET + gameUser.getCurrentTeam().getChatColor() + " ");

        tempGhosts.remove(gameUser);
        PlayerUtil.setInvulnerable(gameUser.getBukkitHandle(), true);
        GameSchedulers.runLater(100, () -> {
            PlayerUtil.setInvulnerable(gameUser.getBukkitHandle(), false);
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();

        for (GameCache teamCache : plugin.getService().getLoadedTeams().stream().map(GameTeam::getCache).collect(Collectors.toList())) {
            loadedTeams.forEach(teams -> {
                if(location.distance(teams.getCache().getLocation(GameConst.TEAM_SPAWN_LOCATION)) <= plugin.getConfig().getDouble("spawn-protection-distance")) {
                    event.setCancelled(true);
                }
            });


            if(location.distance(teamCache.getLocation(GameConst.TEAM_GENERATOR_LOCATION)) <= 2) {
                event.setCancelled(true);
                break;
            }

            placedBlocks.add(location);
            GameUser.from(event.getPlayer()).getCache().increment(GameConst.BLOCK_PLACED_PLAYER_DATA);
            break;
        }

        for(BlockState block : (List<BlockState>) plugin.getCache().get(GameConst.TRADER_LOCATIONS)) {
            Location traderLoc = block.getLocation();
            if(traderLoc.distance(location) <= 3) {
                event.setCancelled(true);
                break;
            }
        }

        for(BlockState block : (List<BlockState>) plugin.getCache().get(GameConst.UPGRADER_LOCATIONS)) {
            Location updaterLoc = block.getLocation();
            if(updaterLoc.distance(location) <= 3) {
                event.setCancelled(true);
                break;
            }
        }

        if(event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType() == Material.SKULL_ITEM && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType().name().contains("CHEST")) {
                if (event.getPlayer().isSneaking())
                    event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GameUser user = GameUser.from(event.getPlayer());
        Block block = event.getBlock();

        if(placedBlocks.contains(block.getLocation())) return;

        if(block.getType() != Material.BED_BLOCK) {
            event.setCancelled(true);
            return;
        }

        ChatColor color = ColorUtil.asChatColor(((Wool) block.getRelative(BlockFace.DOWN).getState().getData()).getColor());
        GameTeam team = plugin.getService().getTeamManager().getTeamsByColor(color)[0];

        if(team.getTeamIndex() == user.getCurrentTeam().getTeamIndex()) {
            user.getBukkitHandle().sendMessage(GameConst.PREFIX + "§cВы не можете сломать кровать своей команды!");

            event.setCancelled(true);
            return;
        }

        block.getWorld().getNearbyEntities(block.getLocation(), 1, 1, 1).stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM)
                .map(Item.class::cast).filter(item -> item.getItemStack().getType() == Material.BED).forEach(Entity::remove);

        team.getCache().set(GameConst.TEAM_HAS_BED, false);
        team.handleBroadcast(teamUser -> teamUser.getBukkitHandle().sendTitle("§cКровать уничтожена", "§eВы больше не возродитесь!"));

        teamStatusMap.put(team.getTeamIndex(), team.getChatColor() + String.valueOf(team.getPlayers().size()) + " ");
        getPlugin().getCache().set(GameConst.INGAME_TEAM_STATUSES, teamStatusMap);

        plugin.broadcastMessage("\n§eКровать команды " + team.getTeamName() + "§e уничтожена игроком " + user.getCurrentTeam().getChatColor() + "[" + user.getCurrentTeam().getTeamName() + "] " + user.getName() + "\n");
        user.getCache().set(GameConst.PLAYER_BEDS_BROKEN, user.getCache().getInt(GameConst.PLAYER_BEDS_BROKEN) + 1);
        user.getCache().increment(GameConst.BEDS_BROKEN_PLAYER_DATA);
        user.getCache().set(GameConst.PLAYER_EARNED_POINTS, user.getCache().getInt(GameConst.PLAYER_EARNED_POINTS) + 4);
        user.getBukkitHandle().sendMessage("§e+4 поинта (Сломанная кровать)");

        if(block.getLocation().subtract(1, 0, 0).getBlock().getType() == Material.BED_BLOCK)
            block.getLocation().subtract(1, 0, 0).getBlock().setType(Material.AIR);
        else if(block.getLocation().subtract(-1, 0, 0).getBlock().getType() == Material.BED_BLOCK)
            block.getLocation().subtract(-1, 0, 0).getBlock().setType(Material.AIR);
        else if(block.getLocation().subtract(0, 0, 1).getBlock().getType() == Material.BED_BLOCK)
            block.getLocation().subtract(0, 0, 1).getBlock().setType(Material.AIR);
        else if(block.getLocation().subtract(0, 0, -1).getBlock().getType() == Material.BED_BLOCK)
            block.getLocation().subtract(0, 0, -1).getBlock().setType(Material.AIR);

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        loadedTeams.forEach(gameTeam -> {
            int trap = gameTeam.getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.TRAP, 0);

            if(event.getPlayer().getLocation().distance(gameTeam.getCache().getLocation(GameConst.TEAM_SPAWN_LOCATION)) < 20 && trap != 0 && !gameTeam.hasPlayer(event.getPlayer())) {
                gameTeam.getPlayers().forEach(gamePlayer -> {
                    gamePlayer.getBukkitHandle().sendTitle("§cЛовушка сработала!", "§eкто-то у вас на базе!");
                });
                gameTeam.getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).put(TeamUpgrade.TRAP, 0);
            }
        });

        if(event.getTo().getY() <= 0) {
            Player player = event.getPlayer();

            GameUser gameUser = GameUser.from(event.getPlayer());
            GameTeam gameTeam = gameUser.getCurrentTeam();
            Location location = gameTeam != null ? gameTeam.getCache().getLocation(GameConst.TEAM_SPAWN_LOCATION) : plugin.getService().getMapWorld().getSpawnLocation();
            boolean finalDeath = kill(gameUser);
            event.setTo(location);
            if (!finalDeath && gameTeam != null) {
                gameTeam.addPlayer(gameUser);
            }

            if (lastHitMap.containsKey(player)) {
                Player killer = lastHitMap.get(player).firstValue();
                GameUser killerUser = GameUser.from(killer);
                killerUser.getCache().increment(GameConst.KILLS_PLAYER_DATA);

                if(finalDeath) {
                    if (gameUser.getCurrentTeam() != null) {
                        plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e был убит игроком " + GameUser.from(killer).getCurrentTeam().getChatColor() + GameUser.from(killer).getName() + " §f(§b§lФинальное убийство§f)");
                        GameUser.from(killer).getCache().set(GameConst.PLAYER_FINAL_KILLS, GameUser.from(killer).getCache().getInt(GameConst.PLAYER_FINAL_KILLS) + 1);
                        GameUser.from(killer).getCache().set(GameConst.PLAYER_EARNED_POINTS, GameUser.from(killer).getCache().getInt(GameConst.PLAYER_EARNED_POINTS) + 2);
                        killer.sendMessage("§e+2 поинта (Финальное убийство)");

                        GameTeam team = gameUser.getCurrentTeam();
                        team.removePlayer(gameUser);

                        plugin.broadcastMessage("§eКоманда " + team.getTeamName() + "§e была уничтожена!");
                        teamStatusMap.put(gameUser.getCurrentTeam().getTeamIndex(), gameUser.getCurrentTeam().getChatColor() + "✘");
                        gameUser.getCurrentTeam().getCache().set(GameConst.IS_TEAM_ALIVE, false);
                        getPlugin().getCache().set(GameConst.INGAME_TEAM_STATUSES, teamStatusMap);
                        Collection<GameTeam> aliveTeams = getPlugin().getService().getLoadedTeams()
                                .stream()
                                .filter(t -> t.getCache().get(GameConst.IS_TEAM_ALIVE))
                                .collect(Collectors.toSet());

                        if(aliveTeams.size() <= 1) {
                            nextStage();
                        }
                    }
                } else {
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e был убит игроком " + GameUser.from(killer).getCurrentTeam().getChatColor() + GameUser.from(killer).getName());
                    GameUser.from(killer).getCache().set(GameConst.PLAYER_KILLS, GameUser.from(killer).getCache().getInt(GameConst.PLAYER_KILLS) + 1);
                    GameUser.from(killer).getCache().set(GameConst.PLAYER_EARNED_POINTS, GameUser.from(killer).getCache().getInt(GameConst.PLAYER_EARNED_POINTS) + 1);
                    killer.sendMessage("§e+1 поинт (Убийство)");
                }

                lastHitMap.remove(player);
            } else {
                if(finalDeath)
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e упал в бездну §f(§b§lФинальное убийство§f)");
                else
                    plugin.broadcastMessage("§eИгрок " + GameUser.from(player).getCurrentTeam().getChatColor() + GameUser.from(player).getName() + "§e упал в бездну");
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !placedBlocks.contains(block.getLocation()));
    }

    @EventHandler
    public void onArmorUnequip(InventoryClickEvent event) {
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR))
                event.setCancelled(true);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        GameUser gameUser = GameUser.from(event.getPlayer());
        if (gameUser.getCurrentTeam() == null)
            return;
        GameTeam team = gameUser.getCurrentTeam();
        gameUser.getCurrentTeam().removePlayer(gameUser);
        if(team.getPlayers().size() == 0) {
            teamStatusMap.put(team.getTeamIndex(), team.getChatColor() + "✘");
            team.getCache().set(GameConst.IS_TEAM_ALIVE, false);
            getPlugin().getCache().set(GameConst.INGAME_TEAM_STATUSES, teamStatusMap);
            Block block = team.getCache().getLocation(GameConst.TEAM_BED_LOCATION).getBlock();
            Collection<Integer> aliveTeams = new ArrayList<>();

            teamStatusMap.forEach((i, s) -> {
                ChatColor cc = getPlugin().getService().getTeam(i).getChatColor();
                if(!s.equals(cc + "✘"))
                    aliveTeams.add(i);
            });

            if(block.getLocation().subtract(1, 0, 0).getBlock().getType() == Material.BED_BLOCK)
                block.getLocation().subtract(1, 0, 0).getBlock().setType(Material.AIR);
            else if(block.getLocation().subtract(-1, 0, 0).getBlock().getType() == Material.BED_BLOCK)
                block.getLocation().subtract(-1, 0, 0).getBlock().setType(Material.AIR);
            else if(block.getLocation().subtract(0, 0, 1).getBlock().getType() == Material.BED_BLOCK)
                block.getLocation().subtract(0, 0, 1).getBlock().setType(Material.AIR);
            else if(block.getLocation().subtract(0, 0, -1).getBlock().getType() == Material.BED_BLOCK)
                block.getLocation().subtract(0, 0, -1).getBlock().setType(Material.AIR);

            if(aliveTeams.size() <= 1)
                nextStage();
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        if(event.getItem().getItemStack().getType() == Material.BED)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        if(event.getItem().getType() == Material.POTION) {
            event.getPlayer().getItemInHand().setType(Material.AIR);
        }
        System.out.println("drinked");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(GameUser.from(event.getPlayer()).getCurrentTeam() != null && !GameUser.from(event.getPlayer()).isGhost()) {
            if(PlazmixBedWars.getInstance().getBedWarsMode().getTeamSize() == 1)
                event.setFormat(PlayerUtil.formatMessageToChat(GameUser.from(event.getPlayer()).getCurrentTeam().getChatColor() + event.getPlayer().getName(), event.getMessage()));
            else {
                String isToAll = Character.toString(event.getMessage().charAt(0));
                if(isToAll.equals("!"))
                    event.setFormat(PlayerUtil.formatMessageToChat( "§6§l[ВСЕМ] " + ChatColor.RESET + GameUser.from(event.getPlayer()).getCurrentTeam().getChatColor() + event.getPlayer().getName(), event.getMessage()));
                else {
                    GameUser.from(event.getPlayer()).getCurrentTeam().getPlayers().forEach(teammate -> {
                        teammate.getBukkitHandle().sendMessage(PlayerUtil.formatMessageToChat( "§7§l[КОМАНДА] " + ChatColor.RESET + GameUser.from(event.getPlayer()).getCurrentTeam().getChatColor() + event.getPlayer().getName(), event.getMessage()));
                    });
                }

            }
        }
        if(GameUser.from(event.getPlayer()).isGhost()) {
            tempGhosts.forEach((t, m) -> {
                t.getBukkitHandle().sendMessage(PlayerUtil.formatMessageToChat(GameUser.from(event.getPlayer()).getPlazmixHandle().getDisplayName(), event.getMessage()));
                event.setFormat("§7§l[Наблюдатель]§r " + GameUser.from(event.getPlayer()).getPlazmixHandle().getDisplayName() + " §8§l↪ §r" + event.getMessage());
            });
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(event.getItemDrop().getItemStack().getType().name().endsWith("SWORD"))
            event.setCancelled(true);
    }
}