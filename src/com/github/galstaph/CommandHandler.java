package com.github.galstaph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandHandler extends CaveOfWonders
{
	public CommandHandler()
	{
	
	}
	
	public boolean ListPortals(CommandSender sender)
	{
		for (int x = 0; x < Portals.size(); x++) {
			sender.sendMessage(Portals.get(x)[0] + " -> " + Portals.get(x)[4]);
		}
		return true;
	}
	
	public boolean UpdateLoginMessage(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		String LoginMessage = "";
		for (int x = 0; x < args.length; x++)
			LoginMessage += args[x] + " ";
		LoginMessage.trim();
		CoWConfig.put("LoginMessage", LoginMessage);
		CaveOfWonders.DumpConfig();
		sender.sendMessage("Login Message set to " + LoginMessage);
		return true;
	}

	public boolean Status(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			String PlayerMessage = "";
    		if (args.length > 0)
    			PlayerMessage = args[0];
			if (PlayerMessage.length()== 0)
			{
				sender.sendMessage("You must select a message.");
				return false;
			}
    		if (PlayerMessage.contains("~"))
    		{
    			sender.sendMessage("Cannot use a ~ in your status.");
    			return false;
    		}
    		else
    		{
	    		for (int x = 0; x < PlayerStatus.size(); x++)
    			{
    				if (PlayerStatus.get(x)[0].equalsIgnoreCase(player.getName()))
    				{
    					String PlayerInfo[] = PlayerStatus.get(x);
    					PlayerInfo[3] = PlayerMessage;
    					PlayerStatus.put(x, PlayerInfo);
    					sender.sendMessage("Status Message Updated.");
    					return true;
    				}
    			}
	    		sender.sendMessage("Player " + player.getName() + " not found.");
	    		return false;
    		}
		}
		else
		{
			sender.sendMessage("Player only command.");
			return false;
		}
	
	}

	public boolean AddPortal(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		for (int x = 0; x < Portals.size(); x++)
		{
			if (Portals.get(x)[0].toUpperCase().equals(args[0].toUpperCase()))
			{
				sender.sendMessage("Portal Name already exists.");
				return false;
			}
		}
		Location PortalLocation;
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length > 3)
			{
				PortalLocation = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
			}
			else
			{
				PortalLocation = player.getLocation();
			}
		}
		else
		{
    		if (args.length > 3)
    		{
    			java.util.List<World> worlds = Bukkit.getWorlds();
    			PortalLocation = new Location(worlds.get(0), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
    		}
   		    else
    		{
   		    	sender.sendMessage("Console must send coordinates.");
   		    	return false;
    		}
		}
		String[] PortalInfo = new String[6];
		PortalInfo[0] = args[0];
		Integer Converter = (int)PortalLocation.getX();
		PortalInfo[1] = Converter.toString();
		Converter = (int)PortalLocation.getY();
		PortalInfo[2] = Converter.toString();
		Converter = (int)PortalLocation.getZ();
		PortalInfo[3] = Converter.toString();
		if (args.length > 4)
		{
			PortalInfo[4] = args[4];
		}
		else
		{
			PortalInfo[4] = "@@NONE";
		}
		PortalInfo[5] = "false";
		Path path = Paths.get(FILE_NAME);
		
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING,StandardOpenOption.APPEND)){
	    	
	      writer.append(PortalInfo[0] + "," + PortalInfo[1] + "," + PortalInfo[2] + "," + PortalInfo[3] + "," + PortalInfo[4] + "," + PortalInfo[5]);
	      writer.close();
	    } catch (IOException e) {
			e.printStackTrace();
		}
		Portals.put(Portals.size(), PortalInfo);
		sender.sendMessage("Portal " + PortalInfo[0] + " Added.");
		return true;
	}

	public boolean HidePortal(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
    		boolean AllowCommand = false;
    		if (sender instanceof Player)
    		{
    			Player player = (Player) sender;
    			if (player.isOp())
    				AllowCommand = true;
    			else
    			{
    				sender.sendMessage("This is an OP only command.");
    				return false;
    			}
    		}
    		else
    			AllowCommand = true;
    		if (AllowCommand)
    		{
    			for (int x = 0; x < Portals.size(); x++)
    			{
    				if (Portals.get(x)[0].equalsIgnoreCase(args[0]))
    				{
    					String[] PortalInformation = Portals.get(x);
    					PortalInformation[5] = "True";
    					Portals.put(x, PortalInformation);
    					CaveOfWonders.DumpPortals();
    					sender.sendMessage(Portals.get(x)[0] + " updated to OP Only.");
    					return true;
    				}
    			}
				sender.sendMessage("Portal not found.");
				return false;
    		}
    		else
    			return false;
		}
		else
		{
			sender.sendMessage("You must select a portal.");
			return false;
		}
	}

	public boolean UnHidePortal(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
    		boolean AllowCommand = false;
    		if (sender instanceof Player)
    		{
    			Player player = (Player) sender;
    			if (player.isOp())
    				AllowCommand = true;
    			else
    			{
    				sender.sendMessage("This is an OP only command.");
    				return false;
    			}
    		}
    		else
    			AllowCommand = true;
    		if (AllowCommand)
    		{
    			for (int x = 0; x < Portals.size(); x++)
    			{
    				if (Portals.get(x)[0].equalsIgnoreCase(args[0]))
    				{
    					String[] PortalInformation = Portals.get(x);
    					PortalInformation[5] = "false";
    					Portals.put(x, PortalInformation);
    					CaveOfWonders.DumpPortals();
    					sender.sendMessage(Portals.get(x)[0] + " updated to OP Only.");
    					return true;
    				}
    			}
				sender.sendMessage("Portal not found.");
				return false;
    		}
    		else
    			return false;
		}
		else
		{
			sender.sendMessage("You must select a portal.");
			return false;
		}
	}

	public boolean SetDestination(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		if (args.length > 1)
		{
			for (int x = 0; x < Portals.size(); x++)
			{
				if (Portals.get(x)[0].equalsIgnoreCase(args[0]))
				{
					String[] portalInformation = Portals.get(x);
					portalInformation[4] = args[1];
					Portals.put(x, portalInformation);
					DumpPortals();
					sender.sendMessage("Portal " + portalInformation[0] + " destination set to " + args[1]);
					return true;
				}
			}
		}
		else
		{
			sender.sendMessage("Must select Portals");
			return false;
		}
		return false;
	}

	public boolean SendPlayer(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		Player sendPlayer;
		if (args.length > 3)
		{
			sendPlayer = Bukkit.getPlayer(args[3]);
		} else {
			sendPlayer = (Player) sender;
		}
		if (sendPlayer == null)
		{
			sender.sendMessage("Player is invalid.");
			return false;
		}
		double X, Y, Z;
		try
		{
			X = Double.parseDouble(args[0]);
			Y = Double.parseDouble(args[1]);
			Z = Double.parseDouble(args[2]);
			Location SendLocation = new Location(sendPlayer.getWorld(), X, Y, Z);
			sendPlayer.teleport(SendLocation);
		}
		catch (NumberFormatException e)
		{
			sender.sendMessage("Invalid Coordinates");
			return false;
		}
		sender.sendMessage(sendPlayer.getName() + " set to " + X + " " + Y + " " + Z);
		return true;
	}

	public boolean RemovePortal(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		if (args.length > 0)
		{
    		for (int x = 0; x < Portals.size(); x++)
    		{
				if (Portals.get(x)[0].equalsIgnoreCase(args[0]))
				{
					Portals.remove(x);
					DumpPortals();
					sender.sendMessage("Portal " + args[0] + " removed.");
					return true;
				}
    		}
		} 
		return false;
	}

	public boolean AFK(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			for (int x = 0; x < PlayerStatus.size(); x++)
			{
				if (PlayerStatus.get(x)[0].equalsIgnoreCase(player.getName()))
				{
					String[] PlayerInfo = PlayerStatus.get(x);
					if (!Boolean.parseBoolean(PlayerInfo[2]))
					{
						Bukkit.getServer().broadcastMessage(player.getName() + " has gone AFK.");
						PlayerInfo[2] = "true";
					}
					else
					{
						Bukkit.getServer().broadcastMessage(player.getName() + " has returned.");
						PlayerInfo[2] = "false";
					}
					return true;
				}
			}
			sender.sendMessage("Player " + player.getName() + " not found.");
			return false;
		}
		else
		{
			sender.sendMessage("Player only command.");
			return false;
		}	
	}

	public boolean Who(CommandSender sender, String[] args)
	{
		boolean ShowAll = false;
		if (args.length > 0)
		{
			ShowAll = true;
		}
		sender.sendMessage("Player List:");
		sender.sendMessage("------------");
		for (int x = 0; x < PlayerStatus.size(); x++)
		{
			if (Boolean.parseBoolean(PlayerStatus.get(x)[4]) || ShowAll)
			{
				String[] PlayerInfo = PlayerStatus.get(x);
				String AFK = "";
				if (Boolean.parseBoolean(PlayerInfo[2]))
				{
					AFK = " [AFK]";
				}
				String Offline = "";
				if (ShowAll)
				{
					if (Boolean.parseBoolean(PlayerStatus.get(x)[4]))
						Offline = " - Online";
					else
						Offline = " - Offline";
				}
				sender.sendMessage(ChatColor.WHITE + PlayerInfo[0] + ChatColor.DARK_GREEN + AFK + ChatColor.GRAY + " '" + ChatColor.RED + PlayerInfo[1] + ChatColor.GRAY + "' \"" + ChatColor.DARK_PURPLE + PlayerInfo[3] + ChatColor.GRAY + "\"" + ChatColor.GOLD + Offline);
			}
		}
		return true;
	}

	public boolean PortalInformation(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		if (args.length > 0)
		{
			for (int x = 0; x < Portals.size(); x++)
			{
				if (Portals.get(x)[0].equalsIgnoreCase(args[0]))
				{
					sender.sendMessage("Portal Information");
				    sender.sendMessage("------------------");
				    sender.sendMessage("Name:        " + Portals.get(x)[0]);
				    sender.sendMessage("Coordinates: " + Portals.get(x)[1] + " " + Portals.get(x)[2] + " " + Portals.get(x)[3]);
				    sender.sendMessage("Destination: " + Portals.get(x)[4]);
					return true;
				}
			}
			sender.sendMessage("Portal not found.");
			return false;
		}
		else
		{
			sender.sendMessage("You must select a portal.");
			return false;
		}
	}

	public boolean SendToPortal(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
    		if (!player.isOp())
    		{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		Player _sendPlayer;
		if (args.length > 1)
		{
			_sendPlayer = Bukkit.getPlayer(args[1]);
		}
		else
		{
    		if (!(sender instanceof Player)) 
    		{
    			sender.sendMessage("You must select a player.");
    			return false;
    		}
    		else
    		{
    			_sendPlayer = (Player) sender;
    		}
		}
		if (_sendPlayer != null)
		{
			if (args.length > 0)
			{
				for (int x = 0; x < Portals.size(); x++)
				{
					
					if (Portals.get(x)[0].toUpperCase().equals(args[0].toUpperCase()))
					{
						try
						{
							double X = Double.parseDouble(Portals.get(x)[1]);
							double Y = Double.parseDouble(Portals.get(x)[2]);
							double Z = Double.parseDouble(Portals.get(x)[3]);
							Location SendLocation = new Location(_sendPlayer.getWorld(), X, Y, Z);
							_sendPlayer.teleport(SendLocation);
							sender.sendMessage(_sendPlayer.getName() + " sent to portal " + Portals.get(x)[0]);
							return true;
						}
						catch (NumberFormatException e)
						{
							getLogger().info(e.getMessage());
						}
					}
				}
				sender.sendMessage("Unable to find Portal");
				return false;
			}
		}
		return false;
	}
	public boolean Title(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.isOp())
			{
				sender.sendMessage("This is an OP only command.");
				return false;
			}
		}
		if (args.length < 1)
		{
			sender.sendMessage("You must select a player.");
			return false;
		}
		else if (args.length < 2)
		{
			sender.sendMessage("You must select a title.");
			return false;
		}
		for (int x = 0; x < PlayerStatus.size(); x++)
		{
			if (PlayerStatus.get(x)[0].equalsIgnoreCase(args[0]))
			{
				String[] PlayerInfo = PlayerStatus.get(x);
				String PlayerTitle = "";
				for (int y = 1; y < args.length; y++)
				{
					if (PlayerTitle.length() > 0)
						PlayerTitle += " ";
					PlayerTitle += args[y];
				}
				PlayerInfo[1] = PlayerTitle;
				sender.sendMessage(args[0] + "'s Title changed to " + PlayerTitle);
				return true;
			}
		}
		sender.sendMessage("Player " + args[0] + " not found.");
		return false;
	}
	
}
