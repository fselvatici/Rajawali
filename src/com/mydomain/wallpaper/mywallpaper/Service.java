package com.mydomain.wallpaper.mywallpaper;

import rajawali.wallpaper.Wallpaper;
import android.content.Context;

public class Service extends Wallpaper {
	private SerializationRenderer mRenderer;

	public Engine onCreateEngine() {
		mRenderer = new SerializationRenderer(this);
		return new WallpaperEngine(this.getSharedPreferences(SHARED_PREFS_NAME,
				Context.MODE_PRIVATE), getBaseContext(), mRenderer, false);
	}
}
