package net.plazmix.bedwars.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GameConst {

    public final String TITLE = "§c§lBed§f§lWars";
    public final String PREFIX = String.format("%s §7:: §f", TITLE);

    public final String TEAM_SPAWN_LOCATION = "TeamSpawnLocation";
    public final String TEAM_GENERATOR_LOCATION = "TeamGeneratorLocation";
    public final String TEAM_BED_LOCATION = "TeamBedLocation";
    public final String TEAM_HAS_BED = "TeamHasBed";
    public final String TEAM_UPGRADES = "TeamUpgrades";

    public final String TRADER_LOCATIONS = "TraderLocations";
    public final String UPGRADER_LOCATIONS = "UpgraderLocations";

    public final String OPAL_GENERATOR_LOCATIONS = "OpalGeneratorLocations";

    public final String PLAYER_ARMOR_TYPE = "ArmorType";
    public final String PLAYER_TOOL_LEVELS = "ToolLevels";
    public final String PLAYER_FINAL_KILLS = "FinalKills";
    public final String PLAYER_KILLS = "PlayerKills";
    public final String PLAYER_BEDS_BROKEN = "BedsBroken";
    public final String PLAYER_EARNED_POINTS = "EarnedPoints";

    public final String GAME_LOADED_TEAMS = "GameLoadedTeams";
    public final String INGAME_TEAM_STATUSES = "InGameTeamStatuses";
    public final String IS_TEAM_ALIVE = "IsTeamAlive";
    public static String TIME = "Time";
    public static String IS_TIE = "isTie";
    public static String WIN_TEAM = "winTeam";

    /* PLAYER DATABASE CONSTANTS */
    public static final String WINS_PLAYER_DATA         = "wins";
    public static final String BEDS_BROKEN_PLAYER_DATA  = "beds";
    public static final String KILLS_PLAYER_DATA        = "kills";
    public static final String BLOCK_PLACED_PLAYER_DATA = "blockPlaced";
    public static final String GAMES_PLAYED_PLAYER_DATA = "gamesPlayed";
}