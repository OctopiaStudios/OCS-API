package fr.octopiastudios.api.logs;

import lombok.Getter;

@Getter
public enum LogType {

    SUCCESS("§a"),
    INFO("§7"),
    WARNING("§6"),
    ERROR("§c"),
    ;

    final String color;

    LogType(String color) {
        this.color = color;
    }
}

