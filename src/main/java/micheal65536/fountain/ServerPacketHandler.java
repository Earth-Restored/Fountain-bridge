package micheal65536.fountain;

import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockChangeEntry;
import org.geysermc.mcprotocollib.protocol.data.game.level.notify.GameEvent;
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundCustomPayloadPacket;
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundDisconnectPacket;
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundPingPacket;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundPongPacket;
import org.geysermc.mcprotocollib.protocol.packet.configuration.clientbound.ClientboundRegistryDataPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundChangeDifficultyPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundDelimiterPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundAnimatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundDamageEventPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundEntityEventPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosRotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityRotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundRemoveEntitiesPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundRotateHeadPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundSetEntityDataPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundSetEntityMotionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundSetEquipmentPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundSetPassengersPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundTakeItemEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundTeleportEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundUpdateAttributesPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundBlockChangedAckPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundSetCarriedItemPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetContentPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundBlockDestructionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundBlockEntityDataPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundBlockEventPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundBlockUpdatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundChunkBatchFinishedPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundChunkBatchStartPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundForgetLevelChunkPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundGameEventPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundLevelChunkWithLightPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundLevelEventPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundLevelParticlesPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundLightUpdatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSectionBlocksUpdatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSoundPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundChunkBatchReceivedPacket;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector4f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ServerPacketHandler extends SessionAdapter {
	private final PlayerSession playerSession;

	public ServerPacketHandler(@NotNull PlayerSession playerSession) {
		this.playerSession = playerSession;
	}

	@Override
	public void packetReceived(Session session, Packet packet) {
		try {
			this.playerSession.mutex.lock();

			if (packet instanceof ClientboundLoginPacket) {
				this.playerSession.onJavaLogin((ClientboundLoginPacket) packet);
			} else if (packet instanceof ClientboundCustomPayloadPacket
					&& ((ClientboundCustomPayloadPacket) packet).getChannel().equals(Key.key("minecraft", "register"))) {
				for (String channel : new String(((ClientboundCustomPayloadPacket) packet).getData(),
						StandardCharsets.US_ASCII).split("\0")) {
					this.playerSession.onJavaChannelRegister(channel);
				}
			} else if (packet instanceof ClientboundRegistryDataPacket registryPacket) {
				if (registryPacket.getRegistry().asString().equals("minecraft:worldgen/biome")) {
					this.playerSession.loadJavaBiomes(registryPacket.getEntries());
				}
			} else if (packet instanceof ClientboundCustomPayloadPacket
					&& ((ClientboundCustomPayloadPacket) packet).getChannel().equals(Key.key("fabric", "registry/sync/direct"))) {
				this.playerSession.handleFabricRegistrySyncData(((ClientboundCustomPayloadPacket) packet).getData());
			} else if (packet instanceof ClientboundDisconnectPacket) {
				// empty
			}

			else if (packet instanceof ClientboundDelimiterPacket) {
				// empty
			}

			else if (packet instanceof ClientboundSetTimePacket) {
				this.playerSession.updateTime(((ClientboundSetTimePacket) packet).getTime());
			} else if (packet instanceof ClientboundChangeDifficultyPacket) {
				this.playerSession
						.onJavaDifficultyChanged(((ClientboundChangeDifficultyPacket) packet).getDifficulty());
			} else if (packet instanceof ClientboundGameEventPacket
					&& ((ClientboundGameEventPacket) packet).getNotification() == GameEvent.CHANGE_GAMEMODE) {
				this.playerSession.onJavaGameModeChanged((GameMode) ((ClientboundGameEventPacket) packet).getValue());
			}

			else if (packet instanceof ClientboundLevelChunkWithLightPacket) {
				this.playerSession.onJavaLevelChunk((ClientboundLevelChunkWithLightPacket) packet);
			} else if (packet instanceof ClientboundChunkBatchStartPacket) {
				// empty
			} else if (packet instanceof ClientboundChunkBatchFinishedPacket) {
				this.playerSession.sendJavaPacket(new ServerboundChunkBatchReceivedPacket(20.0f));
			} else if (packet instanceof ClientboundBlockUpdatePacket) {
				this.playerSession.onJavaBlockUpdate(((ClientboundBlockUpdatePacket) packet).getEntry());
			} else if (packet instanceof ClientboundSectionBlocksUpdatePacket) {
				for (BlockChangeEntry blockChangeEntry : ((ClientboundSectionBlocksUpdatePacket) packet).getEntries()) {
					this.playerSession.onJavaBlockUpdate(blockChangeEntry);
				}
			} else if (packet instanceof ClientboundBlockEntityDataPacket) {
				this.playerSession.onJavaBlockEntityUpdate((ClientboundBlockEntityDataPacket) packet);
			} else if (packet instanceof ClientboundBlockEventPacket) {
				this.playerSession.onJavaBlockEvent((ClientboundBlockEventPacket) packet);
			} else if (packet instanceof ClientboundLightUpdatePacket) {
				// empty
			} else if (packet instanceof ClientboundForgetLevelChunkPacket) {
				// empty
			}

			else if (packet instanceof ClientboundBlockDestructionPacket) {
				// TODO: figure out block damage stuff for GenoaUpdateBlockPacket
			}

			else if (packet instanceof ClientboundPlayerPositionPacket) {
				this.playerSession.sendJavaPacket(new ServerboundAcceptTeleportationPacket(
						((ClientboundPlayerPositionPacket) packet).getTeleportId()));
			} else if (packet instanceof ClientboundBlockChangedAckPacket) {
				// empty
			} else if (packet instanceof ClientboundContainerSetContentPacket) {
				this.playerSession.onJavaContainerSetContent((ClientboundContainerSetContentPacket) packet);
			} else if (packet instanceof ClientboundContainerSetSlotPacket) {
				this.playerSession.onJavaContainerSetSlot((ClientboundContainerSetSlotPacket) packet);
			} else if (packet instanceof ClientboundCustomPayloadPacket && ((ClientboundCustomPayloadPacket) packet)
					.getChannel().equals(Key.key("fountain", "inventory_sync_response"))) {
				this.playerSession.onJavaInventorySyncResponse(((ClientboundCustomPayloadPacket) packet).getData());
			} else if (packet instanceof ClientboundCustomPayloadPacket
					&& ((ClientboundCustomPayloadPacket) packet).getChannel().equals(Key.key("fountain", "set_hotbar_response"))) {
				this.playerSession.onJavaSetHotbarResponse(((ClientboundCustomPayloadPacket) packet).getData());
			} else if (packet instanceof ClientboundSetCarriedItemPacket) {
				this.playerSession.onJavaSetCarriedItem(((ClientboundSetCarriedItemPacket) packet).getSlot());
			} else if (packet instanceof ClientboundCustomPayloadPacket
					&& ((ClientboundCustomPayloadPacket) packet).getChannel().equals(Key.key("fountain", "item_particle"))) {
				this.playerSession.onJavaItemPickupParticle(((ClientboundCustomPayloadPacket) packet).getData());
			}

			else if (packet instanceof ClientboundLevelEventPacket) {
				this.playerSession.onJavaLevelEvent(((ClientboundLevelEventPacket) packet).getPosition(),
						((ClientboundLevelEventPacket) packet).getEvent(),
						((ClientboundLevelEventPacket) packet).getData());
			} else if (packet instanceof ClientboundLevelParticlesPacket) {
				this.playerSession.onJavaParticleEvent(((ClientboundLevelParticlesPacket) packet).getParticle(),
						Vector3f.from(((ClientboundLevelParticlesPacket) packet).getX(),
								((ClientboundLevelParticlesPacket) packet).getY(),
								((ClientboundLevelParticlesPacket) packet).getZ()),
						Vector4f.from(((ClientboundLevelParticlesPacket) packet).getOffsetX(),
								((ClientboundLevelParticlesPacket) packet).getOffsetY(),
								((ClientboundLevelParticlesPacket) packet).getOffsetZ(),
								((ClientboundLevelParticlesPacket) packet).getVelocityOffset()),
						((ClientboundLevelParticlesPacket) packet).getAmount(),
						((ClientboundLevelParticlesPacket) packet).isLongDistance());
			} else if (packet instanceof ClientboundSoundPacket) {
				this.playerSession.onJavaSoundEvent(((ClientboundSoundPacket) packet).getSound(),
						Vector3f.from(((ClientboundSoundPacket) packet).getX(),
								((ClientboundSoundPacket) packet).getY(), ((ClientboundSoundPacket) packet).getZ()),
						((ClientboundSoundPacket) packet).getPitch(), ((ClientboundSoundPacket) packet).getVolume());
			}

			else if (packet instanceof ClientboundAddEntityCustomPacket) {
				this.playerSession.onJavaEntityAdd((ClientboundAddEntityCustomPacket) packet);
			} else if (packet instanceof ClientboundRemoveEntitiesPacket) {
				for (int entityInstanceId : ((ClientboundRemoveEntitiesPacket) packet).getEntityIds()) {
					this.playerSession.onJavaEntityRemove(entityInstanceId);
				}
			} else if (packet instanceof ClientboundMoveEntityPosPacket
					|| packet instanceof ClientboundMoveEntityRotPacket
					|| packet instanceof ClientboundMoveEntityPosRotPacket
					|| packet instanceof ClientboundTeleportEntityPacket) {
				int entityId;
				Vector3f pos;
				Vector2f rot;
				boolean onGround;
				if (packet instanceof ClientboundMoveEntityPosPacket) {
					entityId = ((ClientboundMoveEntityPosPacket) packet).getEntityId();
					pos = Vector3f.from(((ClientboundMoveEntityPosPacket) packet).getMoveX(),
							((ClientboundMoveEntityPosPacket) packet).getMoveY(),
							((ClientboundMoveEntityPosPacket) packet).getMoveZ());
					rot = null;
					onGround = ((ClientboundMoveEntityPosPacket) packet).isOnGround();
				} else if (packet instanceof ClientboundMoveEntityRotPacket) {
					entityId = ((ClientboundMoveEntityRotPacket) packet).getEntityId();
					pos = null;
					rot = Vector2f.from(((ClientboundMoveEntityRotPacket) packet).getPitch(),
							((ClientboundMoveEntityRotPacket) packet).getYaw());
					onGround = ((ClientboundMoveEntityRotPacket) packet).isOnGround();
				} else if (packet instanceof ClientboundMoveEntityPosRotPacket) {
					entityId = ((ClientboundMoveEntityPosRotPacket) packet).getEntityId();
					pos = Vector3f.from(((ClientboundMoveEntityPosRotPacket) packet).getMoveX(),
							((ClientboundMoveEntityPosRotPacket) packet).getMoveY(),
							((ClientboundMoveEntityPosRotPacket) packet).getMoveZ());
					rot = Vector2f.from(((ClientboundMoveEntityPosRotPacket) packet).getPitch(),
							((ClientboundMoveEntityPosRotPacket) packet).getYaw());
					onGround = ((ClientboundMoveEntityPosRotPacket) packet).isOnGround();
				} else if (packet instanceof ClientboundTeleportEntityPacket) {
					entityId = ((ClientboundTeleportEntityPacket) packet).getEntityId();
					pos = Vector3f.from(((ClientboundTeleportEntityPacket) packet).getX(),
							((ClientboundTeleportEntityPacket) packet).getY(),
							((ClientboundTeleportEntityPacket) packet).getZ());
					rot = Vector2f.from(((ClientboundTeleportEntityPacket) packet).getPitch(),
							((ClientboundTeleportEntityPacket) packet).getYaw());
					onGround = ((ClientboundTeleportEntityPacket) packet).isOnGround();
				} else {
					assert false;
					return;
				}
				this.playerSession.onJavaEntityMove(entityId, pos, rot, onGround,
						!(packet instanceof ClientboundTeleportEntityPacket));
			} else if (packet instanceof ClientboundSetEntityMotionPacket) {
				this.playerSession.onJavaEntitySetVelocity(((ClientboundSetEntityMotionPacket) packet).getEntityId(),
						Vector3f.from(((ClientboundSetEntityMotionPacket) packet).getMotionX(),
								((ClientboundSetEntityMotionPacket) packet).getMotionY(),
								((ClientboundSetEntityMotionPacket) packet).getMotionZ()));
			} else if (packet instanceof ClientboundRotateHeadPacket) {
				this.playerSession.onJavaEntityRotateHead(((ClientboundRotateHeadPacket) packet).getEntityId(),
						((ClientboundRotateHeadPacket) packet).getHeadYaw());
			} else if (packet instanceof ClientboundSetEntityDataPacket) {
				this.playerSession.onJavaEntityUpdateData(((ClientboundSetEntityDataPacket) packet).getEntityId(),
						((ClientboundSetEntityDataPacket) packet).getMetadata());
			} else if (packet instanceof ClientboundUpdateAttributesPacket) {
				this.playerSession.onJavaEntityUpdateAttributes(
						((ClientboundUpdateAttributesPacket) packet).getEntityId(),
						((ClientboundUpdateAttributesPacket) packet).getAttributes());
			} else if (packet instanceof ClientboundSetEquipmentPacket) {
				this.playerSession.onJavaEntitySetEquipment(((ClientboundSetEquipmentPacket) packet).getEntityId(),
						((ClientboundSetEquipmentPacket) packet).getEquipment());
			} else if (packet instanceof ClientboundEntityEventPacket) {
				this.playerSession.onJavaEntityEvent(((ClientboundEntityEventPacket) packet).getEntityId(),
						((ClientboundEntityEventPacket) packet).getEvent());
			} else if (packet instanceof ClientboundAnimatePacket) {
				this.playerSession.onJavaEntityAnimation(((ClientboundAnimatePacket) packet).getEntityId(),
						((ClientboundAnimatePacket) packet).getAnimation());
			} else if (packet instanceof ClientboundDamageEventPacket) {
				this.playerSession.onJavaEntityHurt(((ClientboundDamageEventPacket) packet).getEntityId());
			} else if (packet instanceof ClientboundSetPassengersPacket) {
				this.playerSession.onJavaEntitySetPassengers(((ClientboundSetPassengersPacket) packet).getEntityId(),
						((ClientboundSetPassengersPacket) packet).getPassengerIds());
			} else if (packet instanceof ClientboundTakeItemEntityPacket) {
				this.playerSession.onJavaEntityTaken((ClientboundTakeItemEntityPacket) packet);
			}

			else if (packet instanceof ClientboundPingPacket) {
				this.playerSession.sendJavaPacket(new ServerboundPongPacket(((ClientboundPingPacket) packet).getId()));
			}

			else {
				LogManager.getLogger().debug("Unhandled server packet {}", packet.getClass().getSimpleName());
			}
		} finally {
			this.playerSession.mutex.unlock();
		}
	}

	@Override
	public void disconnected(DisconnectedEvent event) {
		try {
			this.playerSession.mutex.lock();

			LogManager.getLogger().info("Server has disconnected: {}",
					event != null ? PlainTextComponentSerializer.plainText().serialize(event.getReason()) : "null");
			this.playerSession.disconnect(true);
		} finally {
			this.playerSession.mutex.unlock();
		}
	}
}