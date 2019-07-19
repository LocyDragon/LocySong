package com.locydragon.song;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LocySong extends JavaPlugin {
	private Permission permission = null;
	public static Economy economy = null;
	private Chat chat = null;
	public static FileConfiguration config;
	public static LocySong instance;
	@Override
	public void onEnable() {
		getLogger().info("LocySong 插件启动啦(*^▽^*)~~~");
		getLogger().info("LocySong是一款免费插件.");
		Bukkit.getPluginCommand("ls").setExecutor(new SongCommandMain());
		initVault();
		saveDefaultConfig();
		config = getConfig();
		instance = this;
	}

	private boolean initVault(){
		boolean hasNull = false;
		//获取权限系统实例
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			if ((permission = permissionProvider.getProvider()) == null) { hasNull = true; }
		}
		//初始化聊天系统实例
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			if ((chat = chatProvider.getProvider()) == null) { hasNull = true; }
		}
		//初始化经济系统实例
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			if ((economy = economyProvider.getProvider()) == null) { hasNull = true; }
		}
		return !hasNull;
	}
}
