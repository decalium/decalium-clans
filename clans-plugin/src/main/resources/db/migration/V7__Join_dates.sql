ALTER TABLE `members` ADD `joined` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
CREATE OR REPLACE VIEW `clans_simple` AS SELECT
clans.id `clan_id`, clans.tag `clan_tag`, clans.owner `clan_owner`, clans.display_name `clan_display_name`,
members.uuid `member_uuid`, members.role `member_role`, members.joined `member_joined`
homes.name `home_name`, homes.creator `home_creator`, homes.display_name `home_display_name`, homes.icon `home_icon`, homes.level `home_level`,
locations.x `location_x`, locations.y `location_y`, locations.z `location_z`, locations.world `location_world`,
statistics.type `statistic_type`, statistics.value `statistic_value`
FROM clans
LEFT JOIN members ON clans.id = members.clan_id
LEFT JOIN homes ON clans.id = homes.clan_id
LEFT JOIN locations ON homes.id = locations.home_id
LEFT JOIN statistics ON clans.id = statistics.clan_id;