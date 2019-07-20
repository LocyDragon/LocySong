package com.locydragon.song;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LineAsyncRunnable extends Thread {
	public static ConcurrentHashMap<Player,LineAsyncRunnable> runnableList = new ConcurrentHashMap<>();

	private boolean stop = false;
	List<String> lines = new ArrayList<>();
	ConcurrentHashMap<Long,String> timeTickLine = new ConcurrentHashMap<>();
	String lastLine = null;
	Long nowTick = 0L;
	Player listener;
	public LineAsyncRunnable(Player who, String lines) {
		lines = lines.replace("\\n", ChatColor.COLOR_CHAR + "");
		this.lines.addAll(Arrays.asList(lines.split(ChatColor.COLOR_CHAR + "")));
		for (String object : this.lines) {
			String line = object.split("]", 2)[1];
			String timeDrop = object.split("]", 2)[0]
					.replace("[", "").replace("]", "");
			String minute = timeDrop.split(":")[0];
			String others = timeDrop.split(":")[1];
			String second = others.split("\\.")[0];
			String min_second = others.split("\\.")[1];
			Long time = (long)(Integer.valueOf(min_second) + Integer.valueOf(second) *
					1000 + Integer.valueOf(minute) * 1000 * 60);
			timeTickLine.put(time, line);
		}
		this.listener = who;
	}

	@Override
	public void run() {
		while (!stop) {
			if (this.listener == null || !this.listener.isOnline()) {
				break;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nowTick++;
			if (timeTickLine.getOrDefault(nowTick, null) != null) {
				this.lastLine = timeTickLine.get(nowTick);
			}
			if (this.lastLine != null) {
				ActionBarUtils.sendActionBar(this.listener, ChatColor.AQUA + this.lastLine);
			}
		}
	}

	public void stopMe() {
		stop = true;
	}
}
