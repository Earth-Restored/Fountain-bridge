package micheal65536.fountain;

import org.geysermc.mcprotocollib.protocol.codec.MinecraftCodecHelper;
import org.geysermc.mcprotocollib.protocol.data.game.entity.Effect;
import org.geysermc.mcprotocollib.protocol.data.game.level.event.LevelEventType;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.BuiltinSound;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundUpdateMobEffectPacket;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.Map;

public class ClientboundUpdateMobEffectCustomPacket extends ClientboundUpdateMobEffectPacket {
	public static ClientboundUpdateMobEffectCustomPacket readUnchecked(ByteBuf in, MinecraftCodecHelper helper) {
		try {
			return read(in, helper);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static ClientboundUpdateMobEffectCustomPacket read(ByteBuf in, MinecraftCodecHelper helper)
			throws IOException {
		ByteBuf byteBuf = in.duplicate();
		helper.readVarInt(byteBuf);
		int effectId = helper.readVarInt(byteBuf);
		if (effectId >= Effect.values().length) {
			Int2ObjectMap<LevelEventType> levelEvents;
			Map<String, BuiltinSound> soundNames;
			try {
				Field levelEventsField = MinecraftCodecHelper.class.getDeclaredField("levelEvents");
				Field soundNamesField = MinecraftCodecHelper.class.getDeclaredField("soundNames");
				levelEventsField.setAccessible(true);
				soundNamesField.setAccessible(true);
				levelEvents = (Int2ObjectMap<LevelEventType>) levelEventsField.get(helper);
				soundNames = (Map<String, BuiltinSound>) soundNamesField.get(helper);
			} catch (Exception exception) {
				throw new AssertionError(exception);
			}
			return new ClientboundUpdateMobEffectCustomPacket(effectId, false, in,
					new MinecraftCodecHelper(levelEvents, soundNames) {
						private int varIntCounter = 0;

						@Override
						public int readVarInt(ByteBuf buf) {
							if (this.varIntCounter++ == 1) {
								super.readVarInt(buf);
								return 0;
							} else {
								return super.readVarInt(buf);
							}
						}
					});
		} else {
			return new ClientboundUpdateMobEffectCustomPacket(effectId, true, in, helper);
		}
	}

	public final int effectId;
	private final boolean hasVanillaEffect;

	private ClientboundUpdateMobEffectCustomPacket(int effectId, boolean hasVanillaEffect, ByteBuf in,
			MinecraftCodecHelper helper) throws IOException {
		super(in, helper);
		this.effectId = effectId;
		this.hasVanillaEffect = hasVanillaEffect;
	}

	@Override
	@Nullable
	public Effect getEffect() {
		return this.hasVanillaEffect ? super.getEffect() : null;
	}
}