CREATE TABLE IF NOT EXISTS `region_effects`
(
    `region_id` INTEGER NOT NULL,
    `type` VARCHAR(32) NOT NULL,
    `end` TIMESTAMP NOT NULL,
    UNIQUE(`region_id`),
    FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE CASCADE
);

CREATE OR REPLACE VIEW `regions_simple` AS SELECT regions.*, region_shields.*, region_effects.`end` AS effect_end, region_effects.`type` AS effect_type,
clans.tag AS clan_tag FROM `regions`
LEFT JOIN `region_shields` ON regions.id = region_shields.region_id
LEFT JOIN `region_effects` on regions.id = region_effects.region_id
LEFT JOIN `clans` ON regions.clan_id = clans.id;