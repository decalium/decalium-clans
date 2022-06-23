package org.gepron1x.clans.plugin.util;

import org.slf4j.Logger;

public final class AsciiArt {


    private static final String ART = """
            ________                           .__   .__                \s
            \\______ \\    ____    ____  _____   |  |  |__| __ __   _____ \s
             |    |  \\ _/ __ \\ _/ ___\\ \\__  \\  |  |  |  ||  |  \\ /     \\\s
             |    `   \\\\  ___/ \\  \\___  / __ \\_|  |__|  ||  |  /|  Y Y  \\
            /_______  / \\___  > \\___  >(____  /|____/|__||____/ |__|_|  /
                    \\/      \\/      \\/      \\/                        \\/\s
            _________  .__                                              \s
            \\_   ___ \\ |  |  _____     ____    ______                   \s
            /    \\  \\/ |  |  \\__  \\   /    \\  /  ___/                   \s
            \\     \\____|  |__ / __ \\_|   |  \\ \\___ \\                    \s
             \\______  /|____/(____  /|___|  //____  >                   \s
                    \\/            \\/      \\/      \\/\s
            """;

    private final Logger logger;

    public AsciiArt(Logger logger) {

        this.logger = logger;
    }


    public void print() {

        for(String str : ART.split("\n")) {
            this.logger.info(str);
        }

    }
}
