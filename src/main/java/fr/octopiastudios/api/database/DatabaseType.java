package fr.octopiastudios.api.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseType {

    MARIADB("jdbc:mysql"),
    MYSQL("jdbc:mysql"),
    SQLITE("jdbc:sqlite"),
    POSTGRESQL("jdbc:postgresql"),
    ;

    final String jdbcPrefix;
}
