package mcp.mobius.opis.commands.server;

import java.util.HashMap;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.network.server.Packet_DataScreenAmountEntities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatMessageComponent;

public class CommandAmountEntities extends CommandBase implements IOpisCommand{

	@Override
	public String getCommandName() {
		return "opis_nent";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		HashMap<String, Integer> ents = EntityManager.getCumulativeEntities();
		
		if (icommandsender instanceof EntityPlayer)
			((EntityPlayerMP)icommandsender).playerNetServerHandler.sendPacketToPlayer(Packet_DataScreenAmountEntities.create(ents));
		else if (icommandsender instanceof DedicatedServer){
			for (String s : ents.keySet())
				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("%s : %s", s, ents.get(s))));
		}
		
	}

	@Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }	

	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
		if (sender instanceof DedicatedServer) return true;
		if (((EntityPlayerMP)sender).playerNetServerHandler.netManager instanceof MemoryConnection) return true;		
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(((EntityPlayerMP)sender).username);
    }

	@Override
	public String getDescription() {
		return "Opens a summary of the number of entities on the server, by type.";
	}

}