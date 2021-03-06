package com.eddiep.muse.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.eddiep.muse.MuseFun
import com.eddiep.muse.game.MuseHandler
import com.eddiep.muse.game.sprites.InvaderCreator
import com.eddiep.muse.game.sprites.Player
import com.eddiep.muse.handlers.scenes.LoadingScene
import com.eddiep.muse.handlers.scenes.SpriteScene
import com.eddiep.muse.handlers.scenes.TextOverlayScene
import com.eddiep.muse.logic.Handler
import com.eddiep.muse.render.Text
import com.eddiep.muse.utils.Global
import com.eddiep.muse.utils.Vector2f
import com.interaxon.libmuse.*

class OnePlayerMuseHandler : Handler/*, MuseDataListener()*/ {
    private lateinit var headsetOnText : TextOverlayScene;
    private lateinit var statsText : Text;
    private lateinit var score : Text;
    private lateinit var player : Player
    private var beta = 0.0
    private var start = System.currentTimeMillis()
    private var lastSpawn = System.currentTimeMillis()
    private var nextSpawn = 0L
    private lateinit var museHandler : MuseHandler
    lateinit var world : SpriteScene;

    override fun start() {
        val loading = LoadingScene()
        MuseFun.getInstance().addScene(loading)

        world = SpriteScene()
        world.isVisible = false
        MuseFun.getInstance().addScene(world)

        statsText = Text(48, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        statsText.text = "Waiting for data.."
        statsText.x = 960f
        statsText.y = 720f - 60f

        score = Text(32, Color.WHITE, Gdx.files.internal("fonts/Oxygen-Bold.ttf"));
        score.text = "Score: 0"
        score.x = 70f
        score.y = 900f

        world.addEntity(score)
        world.addEntity(statsText)

        headsetOnText = TextOverlayScene("Headset 404!", "Please put on the headset", false)
        headsetOnText.isVisible = false
        MuseFun.getInstance().addScene(headsetOnText)

        loading.setLoadedCallback(Runnable {
            loading.setText("Looking for MUSE...")
            MuseManager.refreshPairedMuses();
            val muses = MuseManager.getPairedMuses()
            if (muses.size == 0) {
                loading.setText("No MUSES found :(")
                return@Runnable
            }
            loading.setText("MUSE Found!")
            Thread(Runnable {
                loading.setText("Connecting to MUSE..")
                val muse = muses[0]

                museHandler = MuseHandler(muse)
                museHandler.setOnBlink {
                    if (begin)
                        player.fire()
                }
                museHandler.start()
                ready = true

                player = Player()
                player.y = 64f
                player.x = 360f

                start = System.currentTimeMillis()

                world.addEntity(player)

                world.isVisible = true
                loading.isVisible = false
            }).start()
        })
    }

    private var firstInvader = true
    private var tutorial = 0
    private var begin = false
    private var ready = false
    private var displayedTip = false
    override fun tick() {
        if (!ready)
            return

        headsetOnText.isVisible = !museHandler.isConnected
        world.isVisible = museHandler.isConnected

        beta = museHandler.beta

        if (beta > 0 && !begin) {
            begin = true
            System.out.println("HELLO WORLD")
        }

        if (!world.isVisible || !begin)
            return

        if (tutorial == 0) {
            statsText.text = "Start concentrating on the ship\nFill your mind with thought"
        } else if (tutorial == 1) {
            statsText.text = "..now relax..\nEmpty your mind"
        } else if (tutorial == 2) {
            statsText.text = "Got it? Good!\nPractice for a little bit"
            nextSpawn = 8000L
            lastSpawn = System.currentTimeMillis()
            tutorial++
        }

        if (beta >= 0.3) {
            val pos = beta - 0.3
            player.acceleration = Vector2f(pos.toFloat(), 0f)
            player.maxVelocity = Vector2f(4f, 0f)

            if (beta >= 0.7 && tutorial == 0) {
                tutorial++;
            }
        } else {
            player.acceleration = Vector2f(-beta.toFloat(), 0f)
            player.minVelocity = Vector2f(-4f, 0f)

            if (tutorial == 1) {
                tutorial++;
            }
        }

        if (player.x <= 32)
            player.x = 32f
        if (player.x >= 1024f - 32f) {
            player.x = 1024f - 32f

            if (tutorial == 0)
                tutorial++
        }

        if (tutorial >= 3) {
            if (System.currentTimeMillis() - lastSpawn >= nextSpawn) {
                val count = if (firstInvader) 1 else Global.rand(1, 3)

                for (i in 0..count) {
                    val invader = InvaderCreator.createInvader()
                    invader.y = Global.rand(720 - 32, 900).toFloat()
                    invader.x = Global.rand(32, 1024 - 32).toFloat()
                    world.addEntity(invader)
                }

                if (firstInvader) {
                    statsText.text = "Look out!\nBlink to shoot!"
                    firstInvader = false
                }

                nextSpawn = Global.rand(11000, 20000).toLong()
                lastSpawn = System.currentTimeMillis()
                tutorial++

                if (tutorial == 6) {
                    statsText.text = ""
                }
            }
        }

        score.text = "Score: ${player.score}"
    }
}