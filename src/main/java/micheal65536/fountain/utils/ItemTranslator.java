package micheal65536.fountain.utils;

import org.apache.logging.log4j.LogManager;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import micheal65536.fountain.Main;
import micheal65536.fountain.registry.JavaItems;

public final class ItemTranslator {
	@NotNull
	public static ItemData translateJavaToBedrock(@Nullable ItemStack itemStack,
			@NotNull FabricRegistryManager fabricRegistryManager) {
		ItemData.Builder builder = ItemData.builder();

		if (itemStack == null) {
			return builder.build();
		}

		int javaId = itemStack.getId();
		JavaItems.BedrockMapping bedrockMapping = JavaItems.getBedrockMapping(javaId, fabricRegistryManager);
		if (bedrockMapping == null) {
			LogManager.getLogger().warn("Attempt to translate item with no mapping {}",
					JavaItems.getName(javaId, fabricRegistryManager));
			return builder.build();
		}
		builder.definition(Main.ITEM_DEFINITION_REGISTRY.getDefinition(bedrockMapping.id));

		if (bedrockMapping.toolWear) {
			int damage = 0;
			if (itemStack.getDataComponents() != null) {
				damage = itemStack.getDataComponents().getOrDefault(DataComponentType.DAMAGE, 0);
			}

			builder.damage(damage);
		} else {
			builder.damage(bedrockMapping.aux);
		}

		builder.count(itemStack.getAmount());

		return builder.build();
	}
}