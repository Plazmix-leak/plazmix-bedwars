package net.plazmix.bedwars.util;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.component.Resource;
import net.plazmix.game.user.GameUser;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class ResourceGenerator {

    public static final Set<ResourceGenerator> RESOURCE_GENERATORS = new HashSet<>();

    @NonNull final Location location;
    @NonNull final Resource resource;
    long counter = 0L;

    private void respawn() {
        Collection<Player> players = location.getWorld().getNearbyEntities(location, 1.5, 1, 1.5)
                .stream()
                .filter(entity -> entity.getType() == EntityType.PLAYER)
                .map(Player.class::cast)
                .filter(player -> !GameUser.from(player).isGhost())
                .collect(Collectors.toList());

        if(players.isEmpty()) {
            location.getWorld().dropItem(location, resource.asItem());
        } else {
            players.forEach(player -> player.getInventory().addItem(resource.asItem()));
        }
    }

    public void tickRespawn() {
        if(counter <= 0L) {
            respawn();

            counter = MathUtil.getResourceGenerationDelay(resource);
        } else {
            counter -= 1000L;
        }
    }

    public void spawnResource() {
        respawn();
    }

    public static void create(@NonNull Resource resource, @NonNull Location location) {
        RESOURCE_GENERATORS.add(new ResourceGenerator(location, resource));
    }
}