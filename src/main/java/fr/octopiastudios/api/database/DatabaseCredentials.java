package fr.octopiastudios.api.database;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseCredentials {

    private String host = "localhost";
    private String database = "plugins";
    private String username = "username";
    private String password = "password";
    private int port = 3306;

    public String getURL() {
        return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&characterEncoding=UTF-8";
    }
}
