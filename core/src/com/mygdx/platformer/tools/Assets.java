package com.mygdx.platformer.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class Assets extends AssetManager {

    public Assets() {
        super();
        registerLoaders();
        getLoadScreenAssets();
        finishLoading();
        getMusicAssets();
        getSoundAssets();
        getTextureAssets();
        getMapAssets();
    }

    private void registerLoaders() {
        setLoader(Music.class, new MusicLoader(new InternalFileHandleResolver()));
        setLoader(Sound.class, new SoundLoader(new InternalFileHandleResolver()));
        setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
        setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    private void getLoadScreenAssets() {
        load("load_and_menu_bg.tmx", TiledMap.class);
    }

    private void getMusicAssets() {
        load("audio/music/mario_music.ogg", Music.class);
    }

    private void getSoundAssets() {
        load("audio/sounds/coin.wav", Sound.class);
        load("audio/sounds/bump.wav", Sound.class);
        load("audio/sounds/breakblock.wav", Sound.class);
        load("audio/sounds/powerup_spawn.wav", Sound.class);
        load("audio/sounds/powerup.wav", Sound.class);
        load("audio/sounds/powerdown.wav", Sound.class);
        load("audio/sounds/stomp.wav", Sound.class);
        load("audio/sounds/mariodie.wav", Sound.class);
    }

    private void getTextureAssets() {
        load("Mario_and_Enemies.pack", TextureAtlas.class);
    }

    private void getMapAssets() {
        load("level1.tmx", TiledMap.class);
    }
}
