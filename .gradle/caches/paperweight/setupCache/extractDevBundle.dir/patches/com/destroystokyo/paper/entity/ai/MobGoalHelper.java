package com.destroystokyo.paper.entity.ai;

import com.destroystokyo.paper.entity.RangedEntity;
import com.destroystokyo.paper.util.set.OptimizedSmallEnumSet;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Cat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Strider;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zoglin;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.ZombieVillager;

public class MobGoalHelper {

    private static final BiMap<String, String> deobfuscationMap = HashBiMap.create();
    private static final Map<Class<? extends Goal>, Class<? extends Mob>> entityClassCache = new HashMap<>();
    private static final Map<Class<? extends net.minecraft.world.entity.Mob>, Class<? extends Mob>> bukkitMap = new HashMap<>();

    static final Set<String> ignored = new HashSet<>();

    static {
        // TODO these kinda should be checked on each release, in case obfuscation changes
        deobfuscationMap.put("bee_b", "bee_attack");
        deobfuscationMap.put("bee_c", "bee_become_angry");
        deobfuscationMap.put("bee_d", "bee_enter_hive");
        deobfuscationMap.put("bee_e", "bee_go_to_hive");
        deobfuscationMap.put("bee_f", "bee_go_to_known_flower");
        deobfuscationMap.put("bee_g", "bee_grow_crop");
        deobfuscationMap.put("bee_h", "bee_hurt_by_other");
        deobfuscationMap.put("bee_i", "bee_locate_hive");
        deobfuscationMap.put("bee_k", "bee_pollinate");
        deobfuscationMap.put("bee_l", "bee_wander");
        deobfuscationMap.put("cat_a", "cat_avoid_entity");
        deobfuscationMap.put("cat_b", "cat_relax_on_owner");
        deobfuscationMap.put("dolphin_b", "dolphin_swim_to_treasure");
        deobfuscationMap.put("dolphin_c", "dolphin_swim_with_player");
        deobfuscationMap.put("dolphin_d", "dolphin_play_with_items");
        deobfuscationMap.put("drowned_a", "drowned_attack");
        deobfuscationMap.put("drowned_b", "drowned_goto_beach");
        deobfuscationMap.put("drowned_c", "drowned_goto_water");
        deobfuscationMap.put("drowned_e", "drowned_swim_up");
        deobfuscationMap.put("drowned_f", "drowned_trident_attack");
        deobfuscationMap.put("enderman_a", "enderman_freeze_when_looked_at");
        deobfuscationMap.put("evoker_a", "evoker_attack_spell");
        deobfuscationMap.put("evoker_b", "evoker_cast_spell");
        deobfuscationMap.put("evoker_c", "evoker_summon_spell");
        deobfuscationMap.put("evoker_d", "evoker_wololo_spell");
        deobfuscationMap.put("fish_b", "fish_swim");
        deobfuscationMap.put("fox_a", "fox_defend_trusted");
        deobfuscationMap.put("fox_b", "fox_faceplant");
        deobfuscationMap.put("fox_e", "fox_breed");
        deobfuscationMap.put("fox_f", "fox_eat_berries");
        deobfuscationMap.put("fox_g", "fox_float");
        deobfuscationMap.put("fox_h", "fox_follow_parent");
        deobfuscationMap.put("fox_j", "fox_look_at_player");
        deobfuscationMap.put("fox_l", "fox_melee_attack");
        deobfuscationMap.put("fox_n", "fox_panic");
        deobfuscationMap.put("fox_o", "fox_pounce");
        deobfuscationMap.put("fox_p", "fox_search_for_items");
        deobfuscationMap.put("fox_q", "fox_stroll_through_village");
        deobfuscationMap.put("fox_r", "fox_perch_and_search");
        deobfuscationMap.put("fox_s", "fox_seek_shelter");
        deobfuscationMap.put("fox_t", "fox_sleep");
        deobfuscationMap.put("fox_u", "fox_stalk_prey");
        deobfuscationMap.put("illager_abstract_b", "raider_open_door");
        deobfuscationMap.put("illager_illusioner_a", "illusioner_blindness_spell");
        deobfuscationMap.put("illager_illusioner_b", "illusioner_mirror_spell");
        deobfuscationMap.put("illager_wizard_b", "spellcaster_cast_spell");
        deobfuscationMap.put("llama_a", "llama_attack_wolf");
        deobfuscationMap.put("llama_c", "llama_hurt_by");
        deobfuscationMap.put("llama_trader_a", "llamatrader_defended_wandering_trader");
        deobfuscationMap.put("monster_patrolling_a", "long_distance_patrol");
        deobfuscationMap.put("ocelot_a", "ocelot_avoid_entity");
        deobfuscationMap.put("ocelot_b", "ocelot_tempt");
        deobfuscationMap.put("panda_b", "panda_attack");
        deobfuscationMap.put("panda_c", "panda_avoid");
        deobfuscationMap.put("panda_d", "panda_breed");
        deobfuscationMap.put("panda_e", "panda_hurt_by_target");
        deobfuscationMap.put("panda_f", "panda_lie_on_back");
        deobfuscationMap.put("panda_g", "panda_look_at_player");
        deobfuscationMap.put("panda_i", "panda_panic");
        deobfuscationMap.put("panda_j", "panda_roll");
        deobfuscationMap.put("panda_k", "panda_sit");
        deobfuscationMap.put("panda_l", "panda_sneeze");
        deobfuscationMap.put("phantom_b", "phantom_attack_player");
        deobfuscationMap.put("phantom_c", "phantom_attack_strategy");
        deobfuscationMap.put("phantom_e", "phantom_circle_around_anchor");
        deobfuscationMap.put("phantom_i", "phantom_sweep_attack");
        deobfuscationMap.put("polar_bear_a", "polarbear_attack_players");
        deobfuscationMap.put("polar_bear_b", "polarbear_hurt_by");
        deobfuscationMap.put("polar_bear_c", "polarbear_melee");
        deobfuscationMap.put("polar_bear_d", "polarbear_panic");
        deobfuscationMap.put("puffer_fish_a", "pufferfish_puff");
        deobfuscationMap.put("raider_a", "raider_hold_ground");
        deobfuscationMap.put("raider_b", "raider_obtain_banner");
        deobfuscationMap.put("raider_c", "raider_celebration");
        deobfuscationMap.put("raider_d", "raider_move_through_village");
        deobfuscationMap.put("ravager_a", "ravager_melee_attack");
        deobfuscationMap.put("shulker_a", "shulker_attack");
        deobfuscationMap.put("shulker_c", "shulker_defense");
        deobfuscationMap.put("shulker_d", "shulker_nearest");
        deobfuscationMap.put("shulker_e", "shulker_peek");
        deobfuscationMap.put("squid_a", "squid_flee");
        deobfuscationMap.put("abstract_skeleton_1", "skeleton_melee");
        deobfuscationMap.put("strider_a", "strider_go_to_lava");
        deobfuscationMap.put("turtle_a", "turtle_breed");
        deobfuscationMap.put("turtle_b", "turtle_go_home");
        deobfuscationMap.put("turtle_c", "turtle_goto_water");
        deobfuscationMap.put("turtle_d", "turtle_lay_egg");
        deobfuscationMap.put("turtle_f", "turtle_panic");
        deobfuscationMap.put("turtle_h", "turtle_random_stroll");
        deobfuscationMap.put("turtle_i", "turtle_tempt");
        deobfuscationMap.put("turtle_j", "turtle_travel");
        deobfuscationMap.put("vex_a", "vex_charge_attack");
        deobfuscationMap.put("vex_b", "vex_copy_target_of_owner");
        deobfuscationMap.put("vex_d", "vex_random_move");
        deobfuscationMap.put("villager_trader_a", "villagertrader_wander_to_position");
        deobfuscationMap.put("vindicator_a", "vindicator_break_door");
        deobfuscationMap.put("vindicator_b", "vindicator_johnny_attack");
        deobfuscationMap.put("vindicator_c", "vindicator_melee_attack");
        deobfuscationMap.put("wither_a", "wither_do_nothing");
        deobfuscationMap.put("wolf_a", "wolf_avoid_entity");
        deobfuscationMap.put("zombie_a", "zombie_attack_turtle_egg");

        ignored.add("goal_selector_1");
        ignored.add("goal_selector_2");
        ignored.add("selector_1");
        ignored.add("selector_2");
        ignored.add("wrapped");

        bukkitMap.put(net.minecraft.world.entity.Mob.class, Mob.class);
        bukkitMap.put(net.minecraft.world.entity.AgeableMob.class, Ageable.class);
        bukkitMap.put(AmbientCreature.class, Ambient.class);
        bukkitMap.put(Animal.class, Animals.class);
        bukkitMap.put(net.minecraft.world.entity.ambient.Bat.class, Bat.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Bee.class, Bee.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Blaze.class, Blaze.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Cat.class, Cat.class);
        bukkitMap.put(net.minecraft.world.entity.monster.CaveSpider.class, CaveSpider.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Chicken.class, Chicken.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Cod.class, Cod.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Cow.class, Cow.class);
        bukkitMap.put(PathfinderMob.class, Creature.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Creeper.class, Creeper.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Dolphin.class, Dolphin.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Drowned.class, Drowned.class);
        bukkitMap.put(net.minecraft.world.entity.boss.enderdragon.EnderDragon.class, EnderDragon.class);
        bukkitMap.put(EnderMan.class, Enderman.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Endermite.class, Endermite.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Evoker.class, Evoker.class);
        bukkitMap.put(AbstractFish.class, Fish.class);
        bukkitMap.put(AbstractSchoolingFish.class, Fish.class); // close enough
        bukkitMap.put(FlyingMob.class, Flying.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Fox.class, Fox.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Ghast.class, Ghast.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Giant.class, Giant.class);
        bukkitMap.put(AbstractGolem.class, Golem.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Guardian.class, Guardian.class);
        bukkitMap.put(net.minecraft.world.entity.monster.ElderGuardian.class, ElderGuardian.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.Horse.class, Horse.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.AbstractHorse.class, AbstractHorse.class);
        bukkitMap.put(AbstractChestedHorse.class, ChestedHorse.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.Donkey.class, Donkey.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.Mule.class, Mule.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.SkeletonHorse.class, SkeletonHorse.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.ZombieHorse.class, ZombieHorse.class);
        bukkitMap.put(AbstractIllager.class, Illager.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Illusioner.class, Illusioner.class);
        bukkitMap.put(SpellcasterIllager.class, Spellcaster.class);
        bukkitMap.put(net.minecraft.world.entity.animal.IronGolem.class, IronGolem.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.Llama.class, Llama.class);
        bukkitMap.put(net.minecraft.world.entity.animal.horse.TraderLlama.class, TraderLlama.class);
        bukkitMap.put(net.minecraft.world.entity.monster.MagmaCube.class, MagmaCube.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Monster.class, Monster.class);
        bukkitMap.put(PatrollingMonster.class, Monster.class); // close enough
        bukkitMap.put(net.minecraft.world.entity.animal.MushroomCow.class, MushroomCow.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Ocelot.class, Ocelot.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Panda.class, Panda.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Parrot.class, Parrot.class);
        bukkitMap.put(ShoulderRidingEntity.class, Parrot.class); // close enough
        bukkitMap.put(net.minecraft.world.entity.monster.Phantom.class, Phantom.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Pig.class, Pig.class);
        bukkitMap.put(ZombifiedPiglin.class, PigZombie.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Pillager.class, Pillager.class);
        bukkitMap.put(net.minecraft.world.entity.animal.PolarBear.class, PolarBear.class);
        bukkitMap.put(Pufferfish.class, PufferFish.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Rabbit.class, Rabbit.class);
        bukkitMap.put(net.minecraft.world.entity.raid.Raider.class, Raider.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Ravager.class, Ravager.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Salmon.class, Salmon.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Sheep.class, Sheep.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Shulker.class, Shulker.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Silverfish.class, Silverfish.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Skeleton.class, Skeleton.class);
        bukkitMap.put(AbstractSkeleton.class, Skeleton.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Stray.class, Stray.class);
        bukkitMap.put(net.minecraft.world.entity.monster.WitherSkeleton.class, WitherSkeleton.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Slime.class, Slime.class);
        bukkitMap.put(SnowGolem.class, Snowman.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Spider.class, Spider.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Squid.class, Squid.class);
        bukkitMap.put(TamableAnimal.class, Tameable.class);
        bukkitMap.put(net.minecraft.world.entity.animal.TropicalFish.class, TropicalFish.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Turtle.class, Turtle.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Vex.class, Vex.class);
        bukkitMap.put(net.minecraft.world.entity.npc.Villager.class, Villager.class);
        bukkitMap.put(net.minecraft.world.entity.npc.AbstractVillager.class, AbstractVillager.class);
        bukkitMap.put(net.minecraft.world.entity.npc.WanderingTrader.class, WanderingTrader.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Vindicator.class, Vindicator.class);
        bukkitMap.put(WaterAnimal.class, WaterMob.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Witch.class, Witch.class);
        bukkitMap.put(WitherBoss.class, Wither.class);
        bukkitMap.put(net.minecraft.world.entity.animal.Wolf.class, Wolf.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Zombie.class, Zombie.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Husk.class, Husk.class);
        bukkitMap.put(net.minecraft.world.entity.monster.ZombieVillager.class, ZombieVillager.class);
        bukkitMap.put(net.minecraft.world.entity.monster.hoglin.Hoglin.class, Hoglin.class);
        bukkitMap.put(net.minecraft.world.entity.monster.piglin.Piglin.class, Piglin.class);
        bukkitMap.put(AbstractPiglin.class, PiglinAbstract.class);
        bukkitMap.put(net.minecraft.world.entity.monster.piglin.PiglinBrute.class, PiglinBrute.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Strider.class, Strider.class);
        bukkitMap.put(net.minecraft.world.entity.monster.Zoglin.class, Zoglin.class);
        bukkitMap.put(net.minecraft.world.entity.GlowSquid.class, org.bukkit.entity.GlowSquid.class);
        bukkitMap.put(net.minecraft.world.entity.animal.axolotl.Axolotl.class, org.bukkit.entity.Axolotl.class);
        bukkitMap.put(net.minecraft.world.entity.animal.goat.Goat.class, org.bukkit.entity.Goat.class);
    }

    // TODO: FIX THIS
    public static String getUsableName(Class<?> clazz) {
        String name = clazz.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        boolean flag = false;
        // inner classes
        if (name.contains("$")) {
            String cut = name.substring(name.indexOf("$") + 1);
            if (cut.length() <= 2) {
                name = name.replace("Entity", "");
                name = name.replace("$", "_");
                flag = true;
            } else {
                // mapped, wooo
                name = cut;
            }
        }
        name = name.replace("PathfinderGoal", "");
        name = name.replace("TargetGoal", "");
        name = name.replace("Goal", "");
        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        name = sb.toString();
        name = name.replaceFirst("_", "");

        if (flag && !deobfuscationMap.containsKey(name.toLowerCase()) && !ignored.contains(name)) {
            System.out.println("need to map " + clazz.getName() + " (" + name.toLowerCase() + ")");
        }

        // did we rename this key?
        return deobfuscationMap.getOrDefault(name, name);
    }

    public static EnumSet<GoalType> vanillaToPaper(OptimizedSmallEnumSet<Goal.Flag> types) {
        EnumSet<GoalType> goals = EnumSet.noneOf(GoalType.class);
        for (GoalType type : GoalType.values()) {
            if (types.hasElement(paperToVanilla(type))) {
                goals.add(type);
            }
        }
        return goals;
    }

    public static GoalType vanillaToPaper(Goal.Flag type) {
        switch (type) {
            case MOVE:
                return GoalType.MOVE;
            case LOOK:
                return GoalType.LOOK;
            case JUMP:
                return GoalType.JUMP;
            case UNKNOWN_BEHAVIOR:
                return GoalType.UNKNOWN_BEHAVIOR;
            case TARGET:
                return GoalType.TARGET;
            default:
                throw new IllegalArgumentException("Unknown vanilla mob goal type " + type.name());
        }
    }

    public static EnumSet<Goal.Flag> paperToVanilla(EnumSet<GoalType> types) {
        EnumSet<Goal.Flag> goals = EnumSet.noneOf(Goal.Flag.class);
        for (GoalType type : types) {
            goals.add(paperToVanilla(type));
        }
        return goals;
    }

    public static Goal.Flag paperToVanilla(GoalType type) {
        switch (type) {
            case MOVE:
                return Goal.Flag.MOVE;
            case LOOK:
                return Goal.Flag.LOOK;
            case JUMP:
                return Goal.Flag.JUMP;
            case UNKNOWN_BEHAVIOR:
                return Goal.Flag.UNKNOWN_BEHAVIOR;
            case TARGET:
                return Goal.Flag.TARGET;
            default:
                throw new IllegalArgumentException("Unknown paper mob goal type " + type.name());
        }
    }

    public static <T extends Mob> GoalKey<T> getKey(Class<? extends Goal> goalClass) {
        String name = getUsableName(goalClass);
        if (ignored.contains(name)) {
            //noinspection unchecked
            return (GoalKey<T>) GoalKey.of(Mob.class, NamespacedKey.minecraft(name));
        }
        return GoalKey.of(getEntity(goalClass), NamespacedKey.minecraft(name));
    }

    public static <T extends Mob> Class<T> getEntity(Class<? extends Goal> goalClass) {
        //noinspection unchecked
        return (Class<T>) entityClassCache.computeIfAbsent(goalClass, key -> {
            for (Constructor<?> ctor : key.getDeclaredConstructors()) {
                for (int i = 0; i < ctor.getParameterCount(); i++) {
                    Class<?> param = ctor.getParameterTypes()[i];
                    if (net.minecraft.world.entity.Mob.class.isAssignableFrom(param)) {
                        //noinspection unchecked
                        return toBukkitClass((Class<? extends net.minecraft.world.entity.Mob>) param);
                    } else if (RangedAttackMob.class.isAssignableFrom(param)) {
                        return RangedEntity.class;
                    }
                }
            }
            throw new RuntimeException("Can't figure out applicable entity for mob goal " + goalClass); // maybe just return EntityInsentient?
        });
    }

    public static Class<? extends Mob> toBukkitClass(Class<? extends net.minecraft.world.entity.Mob> nmsClass) {
        Class<? extends Mob> bukkitClass = bukkitMap.get(nmsClass);
        if (bukkitClass == null) {
            throw new RuntimeException("Can't figure out applicable bukkit entity for nms entity " + nmsClass); // maybe just return Mob?
        }
        return bukkitClass;
    }
}
