package org.gepron1x.clans.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.Base64;
import java.util.UUID;

public final class Heads {

	public static PlayerProfile base64(String base64) {
		PlayerProfile profile = Bukkit.createProfile(new UUID(base64.hashCode(), base64.hashCode()));
		profile.setProperty(new ProfileProperty("textures", base64));
		return profile;
	}

	public static PlayerProfile fromId(String id) {
		String url = "http://textures.minecraft.net/texture/" + id;
		JsonObject skin = new JsonObject();
		skin.addProperty("url", url);
		JsonObject textures = new JsonObject();
		textures.add("SKIN", skin);
		JsonObject object = new JsonObject();
		object.add("textures", textures);
		return base64(Base64.getEncoder().encodeToString(object.toString().getBytes()));
	}

	public static final PlayerProfile GREEN_CHECKMARK = fromId("a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6");
	public static final PlayerProfile DECLINE = fromId("beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7");
	public static final PlayerProfile GO_BACK = fromId("74133f6ac3be2e2499a784efadcfffeb9ace025c3646ada67f3414e5ef3394");
}
