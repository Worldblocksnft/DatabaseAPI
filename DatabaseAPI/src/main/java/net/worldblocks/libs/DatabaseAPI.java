package net.worldblocks.libs;

import lombok.Getter;
import net.worldblocks.libs.playerdata.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DatabaseAPI extends JavaPlugin implements Listener {

    @Getter private static DatabaseAPI instance;
    @Getter private SqlClient sqlClient;
    @Getter private PlayerDataHandler playerDataHandler;

    @Override
    public void onEnable() {
        instance = this;
        instanceLocally();
        this.playerDataHandler = new PlayerDataHandler(sqlClient, "playerdat");

        System.out.println(this.sqlClient.keyExists("playerdat", "uuid", "209534c9-b09d-4d7e-8fa6-7187037269b6"));

        this.sqlClient.createTable(
                "example",
                new PostgresColumn("uuid", PostgresType.UUID),
                new PostgresColumn("data", PostgresType.JSON)
        );

        this.sqlClient.insertData(
                "example",
                new PostgresData("uuid", UUID.randomUUID())
        );

        this.sqlClient.deleteData(
                "example",
                "uuid",
                "18ec2e7e-e50e-404e-97ba-103b78c926fa"
        );

        List<String> data = this.sqlClient.fetchData(
                "example",
                "uuid"
        );
        System.out.println(Arrays.toString(data.toArray()));

        List<PostgresRow> multiData = this.sqlClient.fetchDataMultiple(
                "example",
                new String[]{"uuid", "data"}
        );
        System.out.println(Arrays.toString(multiData.toArray()));

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        playerDataHandler.addValueToPlayer(player, "holy_data", player.getAddress().getHostName());
        playerDataHandler.getCachedData(player).setUpdating(this, sqlClient, "playerdat", 30);
        System.out.println(playerDataHandler.getCachedData(player).getData("holy_data"));
    }

    /*
    Instantiates with a standard localhost instance on a testing database.
     */
    public void instanceLocally() {
        this.sqlClient = new SqlClient(this, "localhost", 5432, "testing", "postgres", "lewish09");
    }

    /*
    Instantiates with a global instance on a production database.
     */
    public void instanceGlobally(String ip, int port, String dbName, String user, String pass) {
        this.sqlClient = new SqlClient(this, ip, port, dbName, user, pass);
    }

}
