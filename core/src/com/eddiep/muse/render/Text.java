package com.eddiep.muse.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.eddiep.muse.game.Attachable;
import com.eddiep.muse.handlers.scenes.SpriteScene;

import java.util.ArrayList;

public class Text implements Drawable, Attachable {
    private static final Blend TEXT_BLEND = new Blend(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    private final FileHandle handle;
    private final int size;
    private final Color color;

    private BitmapFont font;
    private float x, y;
    private String text = "";

    private ArrayList<Attachable> children = new ArrayList<Attachable>();
    private ArrayList<Attachable> parents = new ArrayList<Attachable>();
    private GlyphLayout layout;
    private SpriteScene scene;


    public Text(int size, Color color, FileHandle file) {
        this.size = size;
        this.color = color;
        handle = file;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (font == null)
            return;

        if (layout == null) {
            layout = new GlyphLayout(font, text);
        }

        font.draw(batch, text, x - (layout.width / 2f), y + (layout.height / 2f), layout.width, Align.center, true);
    }

    @Override
    public void load() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                FreeTypeFontGenerator gen = new FreeTypeFontGenerator(handle);
                FreeTypeFontGenerator.FreeTypeFontParameter parm = new FreeTypeFontGenerator.FreeTypeFontParameter();
                parm.size = size;
                parm.color = color;

                font = gen.generateFont(parm);
                gen.dispose();

                layout = new GlyphLayout(font, text);
            }
        });
    }

    @Override
    public void unload() {
        font.dispose();
        text = null;
        font = null;
    }

    @Override
    public Blend blendMode() {
        return TEXT_BLEND;
    }

    @Override
    public boolean hasLighting() {
        return false; //Text never has lighting
    }

    @Override
    public int getZIndex() {
        return 0;
    }

    @Override
    public SpriteScene getParentScene() {
        return scene;
    }

    @Override
    public void setParentScene(SpriteScene scene) {
        this.scene = scene;
    }

    @Override
    public void attach(Attachable attach) {
        children.add(attach);
        attach.addParent(this);
    }

    @Override
    public void deattach(Attachable attach) {
        children.remove(attach);
        attach.removeParent(this);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;

        for (Attachable c : children) {
            c.setX(x);
        }
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;

        for (Attachable c : children) {
            c.setY(y);
        }
    }

    @Override
    public void addParent(Attachable parent) {
        parents.remove(parent);
    }

    @Override
    public void removeParent(Attachable parent) {
        parents.remove(parent);
    }

    @Override
    public void setAlpha(float alpha) {
        color.a = alpha;
        if (font != null)
            font.setColor(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        if (layout != null)
            layout.setText(font, text);
    }

}
