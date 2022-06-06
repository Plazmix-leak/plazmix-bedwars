package net.plazmix.bedwars.component.npc;

import lombok.NonNull;
import net.plazmix.bedwars.component.GameInventory;
import net.plazmix.lobby.npc.ServerNPC;
import net.plazmix.protocollib.entity.impl.FakeVillager;
import org.bukkit.Location;

public final class UpgraderNPC extends ServerNPC<FakeVillager> {

    public UpgraderNPC(@NonNull Location location) {
        super(location);

        setHandle(new FakeVillager(location));
    }

    @Override
    protected void onReceive(@NonNull FakeVillager fakeVillager) {
        addHolographicLine("§6§lУлучшения");
        fakeVillager.setClickAction(GameInventory.UPGRADER::createAndOpen);

        enableAutoLooking();
        fakeVillager.spawn();
    }
}