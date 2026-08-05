package micheal65536.fountain.utils.entities;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.geysermc.mcprotocollib.protocol.data.game.entity.EntityEvent;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.MetadataType;
import org.jetbrains.annotations.NotNull;

public class AgeableJavaEntityInstance<T extends AgeableBedrockEntityInstance> extends MobJavaEntityInstance<T>
{
	public AgeableJavaEntityInstance(@NotNull String bedrockEntityIdentifier, @NotNull T bedrockEntityInstance)
	{
		super(bedrockEntityIdentifier, bedrockEntityInstance);
	}

	@Override
	protected void onMetadataFieldChanged(@NotNull EntityMetadata<?, ? extends MetadataType<?>> metadata)
	{
		super.onMetadataFieldChanged(metadata);
		getMetadataField(metadata, 16, MetadataType.BOOLEAN, value ->
		{
			this.bedrockEntityInstance.baby = value;
		});
	}

	@Override
	public boolean handleEvent(@NotNull EntityEvent entityEvent)
	{
		switch (entityEvent)
		{
			case ANIMAL_EMIT_HEARTS ->
			{
				this.bedrockEntityInstance.sendEvent(EntityEventType.LOVE_PARTICLES);
				return true;
			}
			default ->
			{
				return super.handleEvent(entityEvent);
			}
		}
	}
}