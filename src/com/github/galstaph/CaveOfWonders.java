package com.github.galstaph;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

public final class CaveOfWonders extends JavaPlugin implements Listener {
	
	final static String FILE_NAME = System.getProperty("user.dir") + "\\plugins\\CoWPortals.ini";
	final static String ConfigFile = System.getProperty("user.dir") + "\\plugins\\CoWConfiguration.ini";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	HashMap<String, String> PlayerDestination;
	
	HashMap<String, String> CoWConfig; 
	
	HashMap<Integer, String[]> Portals;
	
	@Override public void onEnable(){
        // TODO Insert logic to be performed when the plugin is enabled
		getLogger().info("Starting up Cave of Wonders Plugin.");
		
		getServer().getPluginManager().registerEvents(this, this);
		
		Portals = new LinkedHashMap<Integer,String[]>();
		PlayerDestination = new LinkedHashMap<String, String>();
		Path path = Paths.get(FILE_NAME);
	    try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	      while (scanner.hasNextLine()){
	    	  //add a map entry for each portal
	    	  String[] readPortals;
	    	  readPortals = scanner.nextLine().split(",");
	    	  Portals.put(Portals.size(), readPortals);
	    	  getLogger().info("Adding Portal - " + readPortals[0] + " -> " + readPortals[4]);
	    	  //getLogger().info(scanner.nextLine());
	      }      
	    } catch (IOException e) {
			// TODO Create File
		getLogger().info(e.getMessage());	
		}
	    CoWConfig = new LinkedHashMap<String, String>();
	    try (Scanner scanner = new Scanner(Paths.get(ConfigFile), ENCODING.name()))
	    {
	    	while (scanner.hasNextLine())
	    	{
	    		String[] readConfig = scanner.nextLine().split("=");
	    		CoWConfig.put(readConfig[0],readConfig[1]);
	    	}
	    } catch (IOException e) {
			e.printStackTrace();
		}
		getLogger().info("Started Cave of Wonders Plugin.");
    }
	
	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
	    if (!PlayerDestination.containsKey(player.getName()))
	    {
	    	PlayerDestination.put(player.getName(), "");
	    }
	    player.sendMessage(CoWConfig.get("LoginMessage").replace("[PLAYERNAME]", player.getName()));
	}
	
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
	    Player player = e.getPlayer();
	    if (PlayerDestination.containsKey(player.getName()))
	    {
	    	PlayerDestination.remove(player.getName());
	    }
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		boolean JustTeleported = false;
		Player player = evt.getPlayer();
		
		Location playerLocation = player.getLocation();
		
		for (int x = 0; x < Portals.size(); x++)
		{
			if (Integer.parseInt(Portals.get(x)[1]) == (int)playerLocation.getX() && Integer.parseInt(Portals.get(x)[2]) == (int)playerLocation.getY() && Integer.parseInt(Portals.get(x)[3]) == (int)playerLocation.getZ())
			{
				if (!PlayerDestination.get(player.getName()).equalsIgnoreCase(Portals.get(x)[0]))
				{
					if (Portals.get(x)[4].equalsIgnoreCase("@@RANDOM"))
					{
						Random generator = new Random();
						PlayerDestination.put(player.getName(), "@@RANDOM");
						double X = generator.nextInt();
						double Y = generator.nextInt(256);
						double Z = generator.nextInt();
						Location PortalLocation = new Location(player.getWorld(), X, Y, Z);
						player.teleport(PortalLocation);
					}
					else
					{
						for (int y = 0; y < Portals.size(); y++)
						{
							if (Portals.get(x)[4].equalsIgnoreCase(Portals.get(y)[0]))
							{
								PlayerDestination.put(player.getName(), Portals.get(y)[0]);
								double X = Double.parseDouble(Portals.get(y)[1]);
								double Y = Double.parseDouble(Portals.get(y)[2]);
								double Z = Double.parseDouble(Portals.get(y)[3]);
								Location PortalLocation = new Location(player.getWorld(), X - .5, Y, Z - .5);
								player.teleport(PortalLocation);
								JustTeleported = true;
								break;
							}
						}
					}
				}
			}	
		}
		if (!JustTeleported)
		{
			boolean UpdateToNothing = true;
			for (int x = 0; x < Portals.size(); x++)
			{
				if (PlayerDestination.get(player.getName()).equalsIgnoreCase(Portals.get(x)[0]))
					if (Integer.parseInt(Portals.get(x)[1]) == (int)playerLocation.getX() && Integer.parseInt(Portals.get(x)[3]) == (int)playerLocation.getZ())
						UpdateToNothing = false;
			}
			if (UpdateToNothing)
				PlayerDestination.put(player.getName(), "");
		}
	}
	
    @Override public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("ListPortals")){
			for (int x = 0; x < Portals.size(); x++) {
				sender.sendMessage(Portals.get(x)[0] + " -> " + Portals.get(x)[4]);
			}
    		return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("UpdateLoginMessage")){
    		String LoginMessage = "";
    		for (int x = 0; x < args.length; x++)
    			LoginMessage += args[x] + " ";
    		LoginMessage.trim();
    		CoWConfig.put("LoginMessage", LoginMessage);
			DumpConfig();
			sender.sendMessage("Login Message set to " + LoginMessage);
			return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("AddPortal")){
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
    		String[] PortalInfo = new String[5];
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
			Path path = Paths.get(FILE_NAME);
			
		    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING,StandardOpenOption.APPEND)){
		    	
		      writer.append(PortalInfo[0] + "," + PortalInfo[1] + "," + PortalInfo[2] + "," + PortalInfo[3] + "," + PortalInfo[4]);
		      writer.close();
		    } catch (IOException e) {
				e.printStackTrace();
			}
			Portals.put(Portals.size(), PortalInfo);
			sender.sendMessage("Portal " + PortalInfo[0] + " Added.");
			return true;
    	}
    	else if (cmd.getName().equalsIgnoreCase("SetDestination")) {
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
    	}
    	else if(cmd.getName().equalsIgnoreCase("SendPlayer")){
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
    	else if (cmd.getName().equalsIgnoreCase("RemovePortal")){
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
    	else if (cmd.getName().equalsIgnoreCase("PortalInformation"))
    	{
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
    	else if (cmd.getName().equalsIgnoreCase("SendToPortal")){
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
    	}
    	return false; 
    }
    private void DumpPortals()
    {
    	Path path = Paths.get(FILE_NAME);
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	    	for (int x = 0; x < Portals.size(); x++)
	    	{
	    		if (x != 0)
	    			writer.newLine();
	    		writer.append(Portals.get(x)[0] + "," + Portals.get(x)[1] + "," + Portals.get(x)[2] + "," + Portals.get(x)[3] + "," + Portals.get(x)[4]);
	    	}
	      writer.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void DumpConfig()
    {
    	Path path = Paths.get(ConfigFile);
    	Boolean FirstRun = true;
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING))
	    {
	    	if (!FirstRun)
	    		writer.newLine();
	    	else
	    		FirstRun = false;
			writer.append("LoginMessage=" + CoWConfig.get("LoginMessage"));
			writer.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
