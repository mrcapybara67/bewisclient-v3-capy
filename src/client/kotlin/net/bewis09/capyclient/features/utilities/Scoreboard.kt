package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

object Scoreboard : ImageFeature(createIdentifier("capyclient", "scoreboard"), "Scoreboard") {
    val scale = float("scale", 1.0f, 0.5f, 2.0f, 0.01f, 2)
    val offsetX = int("offset_x", 0, -512, 512)
    val offsetY = int("offset_y", 0, -256, 256)
    val hideTitle = boolean("hide_title", false)
    val hideScoreNumbers = boolean("hide_score_numbers", false)
    val hideBackground = boolean("hide_background", false)
    val showOnAnyObjective = boolean("show_on_any_objective", true)
    val opacity = float("opacity", 1.0f, 0.1f, 1.0f, 0.01f, 2)
    val textShadow = boolean("text_shadow", true)
    val animationTime = int("animation_time_ms", 150, 0, 1000)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, scale, "scale", "Scale", "Adjust the size of the scoreboard", "scale")
        list.addRenderable(this, offsetX, "offset_x",
            "Horizontal Offset (px)",
            "Shift the scoreboard horizontally in pixels. Positive moves right, negative moves left.",
            "offset_x"
        )
        list.addRenderable(this, offsetY, "offset_y",
            "Vertical Offset (px)",
            "Shift the scoreboard vertically in pixels. Positive moves down, negative moves up.",
            "offset_y"
        )
        list.addRenderable(this, opacity, "opacity",
            "Opacity",
            "Global opacity of the scoreboard. Lower values fade the entire sidebar.",
            "opacity"
        )
        list.addRenderable(this, animationTime, "animation_time",
            "Animation Time (ms)",
            "Animation duration when the scoreboard appears, disappears, or changes objective.",
            "animation_time"
        )
        list.addRenderable(this, hideTitle, "hide_title",
            "Hide Title",
            "Hide the title row at the top of the scoreboard sidebar.",
            "hide_title"
        )
        list.addRenderable(this, hideScoreNumbers, "hide_score_numbers",
            "Hide Score Numbers",
            "Hide the small score numbers on the right side of each row.",
            "hide_score_numbers"
        )
        list.addRenderable(this, hideBackground, "hide_background",
            "Hide Background",
            "Hide the dark background behind the scoreboard entries.",
            "hide_background"
        )
        list.addRenderable(this, textShadow, "text_shadow",
            "Text Shadow",
            "Whether scoreboard text has a drop shadow.",
            "text_shadow"
        )
        list.addRenderable(this, showOnAnyObjective, "show_on_any_objective",
            "Show on Any Objective",
            "If off, the scale/opacity/offset are only applied when a real objective is shown (not the dummy sidebar).",
            "show_on_any_objective"
        )
    }
}
