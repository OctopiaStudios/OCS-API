package fr.octopiastudios.api.database;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationElement
public class DatabaseCredentials {

    private String host = "localhost";
    private String database = "plugins";
    private String username = "username";
    private String password = "password";
    private DatabaseType type = DatabaseType.MYSQL;
    private int port = 3306;

    public String getURL() {
        return this.type + "://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&characterEncoding=UTF-8";
    }
}
