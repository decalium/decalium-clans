CREATE TABLE IF NOT EXISTS `regions`
(
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `clan_id` INTEGER NOT NULL,
    `x` INTEGER NOT NULL,
    `y` INTEGER NOT NULL,
    `z` INTEGER NOT NULL,
    `world` VARCHAR(64) NOT NULL,
    `level` INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `region_shields`
(
    `region_id` INTEGER NOT NULL,
    `start` TIMESTAMP NOT NULL,
    `end` TIMESTAMP NOT NULL,
    UNIQUE(`region_id`),
    FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE CASCADE
);

CREATE VIEW `regions_simple` AS SELECT * FROM `regions` LEFT JOIN `region_shields` ON regions.id = region_shields.region_id;