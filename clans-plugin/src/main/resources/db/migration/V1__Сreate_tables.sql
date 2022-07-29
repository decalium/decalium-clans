CREATE TABLE IF NOT EXISTS `clans`
(
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `tag` VARCHAR(16) NOT NULL,
    `owner` BINARY(16) NOT NULL,
    `display_name` JSON NOT NULL,
    UNIQUE(`tag`, `owner`),
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `members`
(
    `clan_id` INTEGER NOT NULL,
    `uuid` BINARY(16) NOT NULL,
    `role` VARCHAR(32) NOT NULL,
    UNIQUE(`clan_id`, `uuid`),
    FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE,
    PRIMARY KEY(`uuid`)
);

CREATE TABLE IF NOT EXISTS `homes`
(
    `id` INTEGER AUTO_INCREMENT,
    `clan_id` INTEGER NOT NULL,
    `name` VARCHAR(32) NOT NULL,
    `display_name` JSON NOT NULL,
    `creator` BINARY(16) NOT NULL,
    `icon` BLOB,
    UNIQUE (`clan_id`, `name`, `creator`),
    FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `locations`
(
    `home_id` INTEGER NOT NULL,
    `x` INTEGER NOT NULL,
    `y` INTEGER NOT NULL,
    `z` INTEGER NOT NULL,
    `world` VARCHAR(64) NOT NULL,
    FOREIGN KEY (`home_id`) REFERENCES `homes` (`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `statistics` (
    `clan_id` INTEGER NOT NULL,
    `type` VARCHAR(16) NOT NULL,
    `value` INTEGER NOT NULL,
    UNIQUE(`clan_id`, `type`),
    FOREIGN KEY(`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE
);
