package micheal65536.fountain.utils.entities;

import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.MetadataType;
import org.jetbrains.annotations.NotNull;

public class PigJavaEntityInstance<T extends PigBedrockEntityInstance> extends AgeableJavaEntityInstance<T>
{
	public PigJavaEntityInstance(@NotNull String bedrockEntityIdentifier, @NotNull T bedrockEntityInstance)
	{
		super(bedrockEntityIdentifier, bedrockEntityInstance);
	}

	@Override
	protected void onMetadataFieldChanged(@NotNull EntityMetadata<?, ? extends MetadataType<?>> metadata)
	{
		super.onMetadataFieldChanged(metadata);
		getMetadataField(metadata, 17, MetadataType.BOOLEAN, value ->
		{
			this.bedrockEntityInstance.saddled = value;
		});
	}
}