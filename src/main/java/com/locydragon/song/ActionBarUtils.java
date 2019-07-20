package com.locydragon.song;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

public class ActionBarUtils {
	public static Vector<Player> tickList = new Vector<>();
	public static ProtocolManager instance = null;
	public static boolean sendActionBar(Player user, String msg) {
		if (instance == null) {
			instance = ProtocolLibrary.getProtocolManager();
		}
		if (tickList.contains(user)) {
			return false;
		}
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		PacketContainer packet = instance.createPacket(PacketType.Play.Server.CHAT);
		try {
			packet.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + msg + "\"}"));
			packet.getBytes().write(0, (byte)2);
			instance.sendServerPacket(user, packet);
		} catch (Throwable e) {}
		tickList.add(user);
		new Thread(() -> {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tickList.remove(user);
		}).start();
		return true;
	}
}
