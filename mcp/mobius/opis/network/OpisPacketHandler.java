package mcp.mobius.opis.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.data.ChunksData;
import mcp.mobius.opis.data.TileEntitiesData;
import mcp.mobius.opis.overlay.OverlayLoadedChunks;
import mcp.mobius.opis.overlay.OverlayStatus;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class OpisPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.channel.equals("Opis")) {
			byte header = this.getHeader(packet);
			if (header == Packets.LOADED_CHUNKS){
				Packet_LoadedChunks castedPacket = new Packet_LoadedChunks(packet);
				ChunksData.chunksLoad = castedPacket.chunkStatus;
			}
			
			else if (header == Packets.REQ_CHUNKS_IN_DIM){
				Packet_ReqChunksInDim castedPacket = new Packet_ReqChunksInDim(packet);
				modOpis.proxy.playerOverlayStatus.put(player, OverlayStatus.CHUNKSTATUS);
				modOpis.proxy.playerDimension.put(player, castedPacket.dimension);
				PacketDispatcher.sendPacketToPlayer(Packet_LoadedChunks.create(ChunksData.getLoadedChunks(castedPacket.dimension)), player);
			}
		
			else if (header == Packets.UNREGISTER_USER){
				Packet_UnregisterPlayer castedPacket = new Packet_UnregisterPlayer(packet);
				modOpis.proxy.playerOverlayStatus.remove(player);
				modOpis.proxy.playerDimension.remove(player);
			}

			else if (header == Packets.REQ_CHUNKS_ALL){
				Packet_ReqChunksAll castedPacket = new Packet_ReqChunksAll(packet);
			}			
			
			else if (header == Packets.REQ_MEANTIME_IN_DIM){
				Packet_ReqMeanTimeInDim castedPacket = new Packet_ReqMeanTimeInDim(packet);
				modOpis.proxy.playerOverlayStatus.put(player, OverlayStatus.MEANTIME);
				modOpis.proxy.playerDimension.put(player, castedPacket.dimension);
				PacketDispatcher.sendPacketToPlayer(Packet_MeanTime.create(TileEntitiesData.getTimes(castedPacket.dimension), castedPacket.dimension), player);
			}		
			
			else if (header == Packets.MEANTIME){
				Packet_MeanTime castedPacket = new Packet_MeanTime(packet);
				ChunksData.chunkMeanTime = castedPacket.chunkStatus;
			}
			
        }
	}

	public byte getHeader(Packet250CustomPayload packet){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try{
			return inputStream.readByte();
		} catch (IOException e){
			return -1;
		}
	}        
        
}
