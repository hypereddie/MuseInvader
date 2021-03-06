package com.eddiep.muse.handlers.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.eddiep.muse.render.Text
import com.eddiep.muse.render.scene.AbstractScene

class TextOverlayScene(val header: String, val subtext: String, val showDots: Boolean) : AbstractScene() {
    private lateinit var headerText: Text
    private lateinit var subText: Text
    var dots = 0
    override fun onInit() {
        headerText = Text(36, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        headerText.x = 512f
        headerText.y = 360f
        headerText.text = header
        headerText.load()

        subText = Text(28, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Light.ttf"));
        subText.x = 512f
        subText.y = 300f
        subText.text = subtext
        subText.load()

        requestOrder(-2)
    }

    public fun setHeaderText(text: String) {
        headerText.text = text
    }

    public fun setSubText(text: String) {
        subText.text = text
    }

    private var lastDot = 0L
    override fun render(camera: OrthographicCamera, batch: SpriteBatch) {
        if (showDots) {
            if (System.currentTimeMillis() - lastDot > 800) {
                dots++;
                if (dots == 4)
                    dots = 0;


                subText.text = subtext
                for (i in 0..dots) {
                    subText.text += "."
                }

                subText.x = 512f

                lastDot = System.currentTimeMillis()
            }
        }

        batch.begin()

        headerText.draw(batch)
        subText.draw(batch)

        batch.end()
    }

    override fun dispose() {

    }

}
