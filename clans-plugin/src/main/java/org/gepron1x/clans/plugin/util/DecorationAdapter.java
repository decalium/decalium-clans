package org.gepron1x.clans.plugin.util;

import com.google.gson.*;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.gepron1x.clans.api.decoration.ColorDecoration;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.api.decoration.GradientDecoration;
import org.gepron1x.clans.api.decoration.SymbolDecoration;

import java.lang.reflect.Type;

public class DecorationAdapter implements JsonDeserializer<CombinedDecoration>, JsonSerializer<CombinedDecoration> {


	private static final Gson GSON = GsonComponentSerializer.gson().populator().apply(new GsonBuilder())
			.registerTypeAdapter(CombinedDecoration.class, new DecorationAdapter()).serializeNulls().create();

	@Override
	public CombinedDecoration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonNull()) return CombinedDecoration.EMPTY;
		if (json.isJsonPrimitive() && json.getAsString().equals("null")) return CombinedDecoration.EMPTY;
		JsonObject object = json.getAsJsonObject();
		JsonElement colorElement = object.get("color");
		ColorDecoration color = null;
		GradientDecoration gradient = null;
		SymbolDecoration symbol = null;

		if (colorElement != null) color = new ColorDecoration(context.deserialize(colorElement, TextColor.class));

		JsonElement gradientElement = object.get("gradient");
		if (gradientElement != null) {
			JsonObject obj = gradientElement.getAsJsonObject();
			TextColor first = context.deserialize(obj.get("first"), TextColor.class);
			TextColor second = context.deserialize(obj.get("second"), TextColor.class);
			gradient = new GradientDecoration(first, second);
		}

		JsonElement symbolElement = object.get("symbol");
		if (symbolElement != null) symbol = new SymbolDecoration(symbolElement.getAsString());
		return new CombinedDecoration(color, gradient, symbol);
	}

	@Override
	public JsonElement serialize(CombinedDecoration src, Type typeOfSrc, JsonSerializationContext context) {
		if (src.equals(CombinedDecoration.EMPTY)) return JsonNull.INSTANCE;
		JsonObject object = new JsonObject();
		src.color().ifPresent(c -> object.add("color", context.serialize(c.color(), TextColor.class)));
		src.gradient().ifPresent(g -> object.add("gradient", context.serialize(g, GradientDecoration.class)));
		src.symbol().ifPresent(s -> object.addProperty("symbol", s.symbol()));
		return object;
	}

	public static String asString(CombinedDecoration decoration) {
		return GSON.toJson(decoration);
	}

	public static CombinedDecoration fromString(String str) {
		if (String.valueOf(str).equals("null")) return CombinedDecoration.EMPTY;
		return GSON.fromJson(str, CombinedDecoration.class);
	}


}
