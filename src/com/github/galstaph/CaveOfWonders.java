package com.github.galstaph;

import org.bukkit.Location;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

public class CaveOfWonders extends JavaPlugin implements Listener {
	
	final static String FILE_NAME = System.getProperty("user.dir") + "\\plugins\\CoWPortals.ini";
	final static String ConfigFile = System.getProperty("user.dir") + "\\plugins\\CoWConfiguration.ini";
	final static String PlayerFile = System.getProperty("user.dir") + "\\plugins\\CoWPlayerInfo.ini";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	protected static HashMap<String, String> PlayerDestination;
	
	protected static HashMap<Integer,String[]> PlayerStatus;
	
	protected static HashMap<String, String> CoWConfig; 
	
	protected static HashMap<Integer, String[]> Portals;
	
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
	    
	    PlayerStatus = new LinkedHashMap<Integer, String[]>();
	    try (Scanner scanner = new Scanner(Paths.get(PlayerFile), ENCODING.name()))
	    {
	    	int PlayerId = -1;
	    	while (scanner.hasNextLine())
	    	{
	    		PlayerId++;
	    		String[] readPlayer = scanner.nextLine().split("~");
	    		String[] PlayerInfo = new String[readPlayer.length  + 1];
	    		
	    		for (int x = 0; x < readPlayer.length; x++)
	    		{
    				PlayerInfo[x] = readPlayer[x];
	    		}
	    		PlayerInfo[readPlayer.length] = "false";
	    		PlayerStatus.put(PlayerId, PlayerInfo);
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
	    boolean FoundPlayer = false;
	    for (int x = 0; x < PlayerStatus.size(); x ++)
	    {
		    if (PlayerStatus.get(x)[0] == player.getName())
		    {
		    	String[] PlayerBasicInfo = PlayerStatus.get(player.getName());
		    	PlayerBasicInfo[4] = "true";
		    	PlayerStatus.put(x, PlayerBasicInfo);
		    	FoundPlayer = true;
		    	break;
		    }
	    }
	    if (!FoundPlayer)
	    {
	    	String[] PlayerBasicInfo = new String[5];
	    	PlayerBasicInfo[0] = player.getName();
	    	PlayerBasicInfo[1] = "Peon";
	    	PlayerBasicInfo[2] = "false";
	    	PlayerBasicInfo[3] = "";
	    	PlayerBasicInfo[4] = "true";
	    	PlayerStatus.put(PlayerStatus.size(), PlayerBasicInfo);
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
	    for (int x = 0; x < PlayerStatus.size(); x++)
	    {
		    if (PlayerStatus.get(x)[0] == player.getName())
		    {
		    	String[] PlayerInfo = PlayerStatus.get(x);
		    	PlayerInfo[4] = "false";
		    	PlayerStatus.put(x, PlayerInfo);
		    }
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
    	DumpPlayerInfo();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	CommandHandler cmdHandler = new CommandHandler();
    	if(cmd.getName().equalsIgnoreCase("ListPortals")){
			return cmdHandler.ListPortals(sender);
    	}
    	else if (cmd.getName().equalsIgnoreCase("UpdateLoginMessage")){
    		return cmdHandler.UpdateLoginMessage(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("Status")){
    		return cmdHandler.Status(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("AddPortal")){
    		return cmdHandler.Status(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("HidePortal")){
    		return cmdHandler.HidePortal(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("UnHidePortal")){
    		return cmdHandler.UnHidePortal(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("SetDestination")) {
    		return cmdHandler.SetDestination(sender, args);
    	}
    	else if(cmd.getName().equalsIgnoreCase("SendPlayer")){
    		return cmdHandler.SendPlayer(sender, args);	
    	}
    	else if (cmd.getName().equalsIgnoreCase("RemovePortal")){
    		return cmdHandler.RemovePortal(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("afk"))
    	{
    		return cmdHandler.AFK(sender);
    	}
    	else if (cmd.getName().equalsIgnoreCase("Who"))
    	{
    		return cmdHandler.Who(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("PortalInformation"))
    	{
    		return cmdHandler.PortalInformation(sender, args);
    	}
    	else if (cmd.getName().equalsIgnoreCase("SendToPortal")){
    		return cmdHandler.SendToPortal(sender, args);
    	}
    	return false; 
    }
    
    protected static void DumpPortals()
    {
    	Path path = Paths.get(FILE_NAME);
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	    	for (int x = 0; x < Portals.size(); x++)
	    	{
	    		if (x != 0)
	    			writer.newLine();
	    		writer.append(Portals.get(x)[0] + "," + Portals.get(x)[1] + "," + Portals.get(x)[2] + "," + Portals.get(x)[3] + "," + Portals.get(x)[4] + "," + Portals.get(x)[5]);
	    	}
	      writer.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    protected static void DumpConfig()
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
    private void DumpPlayerInfo()
    {
    	Path path = Paths.get(PlayerFile);
    	Boolean FirstRun = true;
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING))
	    {
	    	for (int x = 0; x < PlayerStatus.size(); x++)
	    	{
		    	if (!FirstRun)
		    		writer.newLine();
		    	else
		    		FirstRun = false;
		    	String[] Values = PlayerStatus.get(x);
		    	String SendMe = "";
		    	for (int y = 0; y < Values.length; y++)
		    	{
		    		if (SendMe != "")
		    			SendMe += "~";
		    		 SendMe += Values[y];		    	}
		    	if (SendMe != "")
		    		writer.append(SendMe);
		    	else
		    		FirstRun = true;
	    	}
			writer.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
