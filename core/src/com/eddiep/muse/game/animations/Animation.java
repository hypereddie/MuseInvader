package com.eddiep.muse.game.animations;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eddiep.muse.game.Entity;
import com.eddiep.muse.utils.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Animation {
    private AnimationType type;
    private Direction direction;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framecount;
    private int speed;
    private List<AnimationVariant> variants = new ArrayList<AnimationVariant>();

    private volatile TextureRegion textureRegion;
    private volatile int currentFrame;
    private volatile boolean isPlaying;
    private volatile long currentTick;
    private volatile int lastFrame;
    private volatile AnimationVariant currentVariant;
    private volatile Entity parent;

    public void init() {
        if (getVariant("DEFAULT") != null)
            throw new IllegalAccessError("This animation has already been initialized");

        AnimationVariant defaultVariant = AnimationVariant.fromAnimation(this);
        defaultVariant.setName("DEFAULT");
        variants.add(defaultVariant);
        this.currentVariant = defaultVariant;
    }

    public void attach(Entity parent) {
        if (textureRegion != null)
            throw new IllegalAccessError("This Animation is already attached to a texture!");

        textureRegion = new TextureRegion(parent.getTexture(), x, y, width, height);
        this.parent = parent;
    }

    private Animation() { }

    public boolean tick() {
        currentTick++;
        long tickPerFrame = 60 / speed;
        currentFrame = (int)(currentTick / tickPerFrame);

        if (currentFrame >= framecount) {
            currentFrame = 0;
            currentTick = 0;
            textureRegion.setRegion(x, y, width, height);
            return true;
        } else if (lastFrame != currentFrame) {
            textureRegion.setRegion(x + (width * currentFrame), y, width, height);
            lastFrame = currentFrame;
            return true;
        }

        return false;
    }

    public AnimationType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

    void setWidth(int width) {
        this.width = width;
    }

    void setHeight(int height) {
        this.height = height;
    }

    void setFramecount(int framecount) {
        this.framecount = framecount;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFramecount() {
        return framecount;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Animation play() {
        isPlaying = true;
        parent.setCurrentAnimation(this);
        return this;
    }

    public Animation pause() {
        isPlaying = false;
        return this;
    }

    public Animation stop() {
        isPlaying = false;
        currentFrame = 0;
        currentTick = 0;
        lastFrame = 0;
        textureRegion.setRegion(x, y, width, height);
        return this;
    }

    public List<AnimationVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    public void applyVariant(AnimationVariant animationVariant) {
        if (animationVariant == null)
            animationVariant = getVariant("DEFAULT");

        this.currentVariant = animationVariant;
        animationVariant.applyTo(this);
    }

    public AnimationVariant getVariant(String name) {
        for (AnimationVariant variant : variants) {
            if (variant.getName().equals(name))
                return variant;
        }

        return null;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public Animation reset() {
        stop();
        return this;
    }
}
