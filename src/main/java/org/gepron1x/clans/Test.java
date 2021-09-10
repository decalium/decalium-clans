package org.gepron1x.clans;

import org.bukkit.persistence.PersistentDataType;
import org.gepron1x.clans.util.pdc.collection.CollectionDataType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        PersistentDataType<?, ?> dataType = CollectionDataType.of(Collectors.toCollection(ArrayList::new), PersistentDataType.INTEGER);
        System.out.println(dataType.getClass());
    }
}
