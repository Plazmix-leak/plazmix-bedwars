package net.plazmix.bedwars.component.npc;

import net.plazmix.lobby.npc.ServerNPC;
import net.plazmix.protocollib.entity.impl.FakeVillager;
import org.bukkit.Location;

public abstract class VillagerNPC extends ServerNPC<FakeVillager> {

    public VillagerNPC(Location location) {
        super(location);

        setHandle(new FakeVillager(location));

    }
}
