CREATE TABLE IF NOT EXISTS `shields`
(
    `clan_id` INTEGER NOT NULL,
    `start` TIMESTAMP NOT NULL,
    `end` TIMESTAMP NOT NULL,
    UNIQUE(`clan_id`),
    FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE
);