package com.locydragon.song;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.locydragon.abf.api.AudioBufferAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SongCommandMain implements CommandExecutor {
	Executor executor = Executors.newCachedThreadPool();
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args[0].equalsIgnoreCase("reload") && sender.isOp()) {
			LocySong.instance.reloadConfig();
			LocySong.config = LocySong.instance.getConfig();
			sender.sendMessage(ChatColor.GREEN + "配置文件重载完成!");
		}
		if (args[0].equalsIgnoreCase("music") && sender.hasPermission("LocySong.use")) {
			if (args.length == 2) {
				if (LocySong.economy.getBalance((Player) sender) >= LocySong.instance.getConfig().getInt("Money", 1000)) {
					LocySong.economy.withdrawPlayer((Player) sender, LocySong.instance.getConfig().getInt("Money", 1000));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							LocySong.config.getString("DibbleSuccess").replace("{player}", sender.getName()).replace("{sum}",
									String.valueOf(LocySong.instance.getConfig().getInt("Money", 100)))));
					TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&',
							LocySong.config.getString("ShowMusic").replace("{musicname}", args[1])));
					message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ls play " + args[1]));
					for (Player inServer : Bukkit.getOnlinePlayers()) {
						inServer.spigot().sendMessage(message);
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "请使用/ls music [歌曲名字] ——点播一首音乐~!");
			}
		}
		if (args[0].equalsIgnoreCase("play") && sender.hasPermission("LocySong.use")) {
			if (args.length == 2) {
				String songName = args[1];
				AudioBufferAPI.stopPlaying((Player)sender);
				playMusic((Player)sender, songName);
			} else {
				sender.sendMessage(ChatColor.RED + "请使用/ls play [歌曲名字] ——播放一首音乐~!");
			}
		}
		return false;
	}

	public void playMusic(Player who, String musicName) {
		executor.execute(() -> {
			String toDo = "http://music.163.com/api/search/get/web?csrf_token=hlpretag=&hlposttag=&s=" + musicName + "&type=1&offset=0&total=true&limit=1";
			StringBuffer json = new StringBuffer();
			try {
				URL u = new URL(toDo);
				URLConnection yc = u.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
				String inputline = null;
				while ((inputline = in.readLine()) != null) {
					json.append(inputline);
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject jsonMe = JSON.parseObject(json.toString());
			JSONObject result = jsonMe.getJSONObject("result");
			if (result.getInteger("songCount") == 0) {
				who.sendMessage(ChatColor.translateAlternateColorCodes('&',
						LocySong.config.getString("NotFound")));
				return;
			}
			JSONObject jsonOut = jsonMe.getJSONArray("songs").getJSONObject(0);
			int musicID = jsonOut.getInteger("id");
			AudioBufferAPI.playForByParam(who, "[Net]http://music.163.com/song/media/outer/url?id=" + musicID +".mp3");
			who.sendMessage(ChatColor.translateAlternateColorCodes('&',
					LocySong.config.getString("PlaySuccess").replace("{musicname}", musicName)));
		});
	}
}
