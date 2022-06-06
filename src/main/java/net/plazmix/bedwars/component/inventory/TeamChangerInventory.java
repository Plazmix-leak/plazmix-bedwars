package net.plazmix.bedwars.component.inventory;

import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.util.ColorUtil;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.team.GameTeamManager;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.protocollib.team.ProtocolTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class TeamChangerInventory extends BaseSimpleInventory {

    public TeamChangerInventory() {
        super("§eВыбор команды", 3);
    }

    @Override
    public void drawInventory(Player player) {
        GameTeamManager teamManager = PlazmixBedWars.getInstance().getService().getTeamManager();

        GameTeam[] loadedTeams = teamManager.getLoadedTeamMap().values(new GameTeam[0]);

        for (int i = 0; i < teamManager.getMappedTeamsCount(); i++) {
            GameTeam team = loadedTeams[i];
            List<String> teamPlayers = new ArrayList<>();
            team.getPlayers().forEach(gameUser -> {
                teamPlayers.add(team.getChatColor() + gameUser.getName());
            });

            teamPlayers.add("");

            teamPlayers.add("§eНажмите, чтобы выбрать команду!");

            setClickItem(i + 1, PlazmixApi.newItemBuilder(Material.WOOL).setDyeColor(ColorUtil.asDyeColor(team.getChatColor())).setName(team.getChatColor() + ColorUtil.asTeamColorName((ColorUtil.asDyeColor(team.getChatColor()))) + " ($IS/$MAX)".replace("$MAX", String.valueOf(PlazmixBedWars.getInstance().getBedWarsMode().getTeamSize())).replace("$IS", String.valueOf(team.getPlayersCount()))).setLore(teamPlayers).build(), (p, event) -> {
                GameUser user = GameUser.from(p);

                if(team.getPlayersCount() >= PlazmixBedWars.getInstance().getBedWarsMode().getTeamSize()) {
                    p.sendMessage(GameConst.PREFIX + "§cКоманда переполнена");
                } else {
                    if (user.getCurrentTeam() != null) {
                        user.getCurrentTeam().removePlayer(user);
                    }
                    team.addPlayer(user);
                    Bukkit.getOnlinePlayers().forEach(this::updateInventory);

                    p.sendMessage(GameConst.PREFIX + "§eВы успешно выбрали команду " + team.getChatColor() + ColorUtil.asTeamColorName((ColorUtil.asDyeColor(team.getChatColor()))));

                    ProtocolTeam.findEntry(user.getBukkitHandle()).setPrefix(team.getChatColor() + "");

                    setOriginalItem(23, PlazmixApi.newItemBuilder(Material.WOOL).setDyeColor(ColorUtil.asDyeColor(team.getChatColor())).setName(team.getChatColor() + ColorUtil.asTeamColorName((ColorUtil.asDyeColor(team.getChatColor())))).build());
                    updateInventory(p);
                }
            });
        }

        GameTeam team = GameUser.from(player).getCurrentTeam();

        if (team == null) {
            setOriginalItem(23, PlazmixApi.newItemBuilder(Material.BEDROCK).setName("§7Вы ещё не выбрали команду").build());
        } else {
            setOriginalItem(23, PlazmixApi.newItemBuilder(Material.WOOL).setDyeColor(ColorUtil.asDyeColor(team.getChatColor())).setName(team.getChatColor() + ColorUtil.asTeamColorName((ColorUtil.asDyeColor(team.getChatColor())))).build());
        }
    }
}