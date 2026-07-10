package net.bewis09.capyclient.drawable.renderables.screen

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.common.Color
import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.Util
import net.bewis09.capyclient.common.alpha
import net.bewis09.capyclient.common.color
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.common.then
import net.bewis09.capyclient.data.Constants
import net.bewis09.capyclient.drawable.Animator
import net.bewis09.capyclient.drawable.BackgroundEffectProvider
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.ImageIdentifier.iconIdentifier
import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.button.ImageButton
import net.bewis09.capyclient.drawable.renderables.components.button.MinecraftButton
import net.bewis09.capyclient.drawable.renderables.components.button.ThemeButton
import net.bewis09.capyclient.drawable.renderables.components.element.RainbowImage
import net.bewis09.capyclient.drawable.renderables.components.element.Rectangle
import net.bewis09.capyclient.drawable.renderables.components.setting.Switch
import net.bewis09.capyclient.drawable.renderables.components.structure.Plane
import net.bewis09.capyclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.capyclient.settings.structure.SidebarFeature
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawingInterface.Companion.DEFAULT_FONT
import net.bewis09.capyclient.drawable.screen_drawing.pushAlpha
import net.bewis09.capyclient.drawable.screen_drawing.transform
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.generated.BuildInfo
import net.bewis09.capyclient.server.Security
import net.bewis09.capyclient.features.sidebar.General
import net.bewis09.capyclient.features.sidebar.Home
import net.bewis09.capyclient.settings.types.Setting
import net.bewis09.capyclient.version.setScreen
import net.minecraft.network.chat.CommonComponents
import org.lwjgl.glfw.GLFW

class OptionScreen(startBlur: Float = 0f, startAlpha: Float = 0f) : PopupScreen(), BackgroundEffectProvider {
    val editHudTranslation = Translation("options.edit_hud", "Edit HUD")

    var category = "capyclient:home"

    val alphaMainAnimation = Animator({ animationDuration }, Animator.EASE_IN_OUT, startAlpha)
    val insideMainAnimation = Animator({ animationDuration }, Animator.EASE_IN_OUT, 1f)
    val blurMainAnimation = Animator({ animationDuration }, Animator.EASE_IN_OUT, startBlur)

    val backIdentifier = createIdentifier("capyclient", "textures/gui/sprites/back.png")
    val closeIdentifier = createIdentifier("capyclient", "textures/gui/sprites/remove.png")

    val sidebarPlane = VerticalAlignScrollPlane(
        arrayListOf<Renderable>().also {
            it.add(Home.createButton().let { button ->
                Plane { x, y, _, _ ->
                    listOf(
                        createTopButton(backIdentifier, 1, x, y, ::goBack),
                        button(x + 19, y, 82, SelectiveScreenDrawer.getSideButtonHeight()),
                        createTopButton(closeIdentifier, 3, x + 106 - ((isMinecrafty then 4) ?: 0), y, ::close)
                    )
                }.setHeight(SelectiveScreenDrawer.getSideButtonHeight())
            })
            it.add(Rectangle { General.getThemeColor(alpha = 0.3f) }.setHeight(1))
            it.addAll(APIEntrypointLoader.mapEntrypoint { a -> a.getSidebarCategories().map { b -> b.createButton() } }.flatten())
            it.add(Rectangle { General.getThemeColor(alpha = 0.3f) }.setHeight(1))
            it.add(ThemeButton(editHudTranslation()) {
                alphaMainAnimation.set(0f) {
                    setRenderableScreen(HudEditScreen())
                }
            }.setHeight(SelectiveScreenDrawer.getSideButtonHeight()))
        }, (isMinecrafty then 2) ?: 5
    )

    fun createTopButton(identifier: Identifier, padding: Int, x: Int, y: Int, onClick: () -> Unit): Renderable = ImageButton(identifier) {
        onClick()
    }.setImagePadding(padding).setImageColor { General.getTextThemeColor() }(x, y, SelectiveScreenDrawer.getSideButtonHeight(), SelectiveScreenDrawer.getSideButtonHeight())

    companion object {
        var currentInstance: OptionScreen? = null
        val modrinthButtonText = Translation("menu.pack.modrinth", "Modrinth")
    }

    val pageStack = mutableListOf(
        Page(
            Home.getHeader(),
            Home.getRenderable(),
            null
        )
    )

    val page
        get() = pageStack.last()

    var switch = Switch(state = { page.setting?.get() ?: false }, onChange = { page.setting?.set(it) })
    val image = RainbowImage(iconIdentifier, 0.5f)

    init {
        currentInstance = this
        alphaMainAnimation.set(1f)
        blurMainAnimation.set(1f)
        internalWidth = screenWidth
        internalHeight = screenHeight
        resize()
    }

    override fun renderScreen(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        checkValidVersion()

        screenDrawing.setBewisclientFont()
        screenDrawing.pushAlpha(alphaMainAnimation.get()) {
            renderBackground(screenDrawing)
            renderSidebar(screenDrawing, mouseX, mouseY)
            renderVersionText(screenDrawing)
            renderInner(screenDrawing, mouseX, mouseY)
        }
        screenDrawing.setDefaultFont()
    }

    fun renderBackground(screenDrawing: ScreenDrawing) {
        SelectiveScreenDrawer.renderMenuBackground(screenDrawing, width, height)
    }

    fun renderSidebar(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        sidebarPlane.render(screenDrawing, mouseX, mouseY)
        image.render(screenDrawing, mouseX, mouseY)
    }

    fun renderVersionText(screenDrawing: ScreenDrawing) {
        screenDrawing.transform(width - 5f, height - 11f, 0.7f) {
            screenDrawing.drawRightAlignedText("Capy Client ${BuildInfo.VERSION}", 0, 0, if (isMinecrafty) Color.WHITE alpha 0.5f else General.getThemeColor(alpha = 0.5f))
        }
    }

    fun renderInner(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        screenDrawing.pushAlpha(insideMainAnimation.get()) {
            page.header.render(screenDrawing, mouseX, mouseY)
            page.pane.render(screenDrawing, mouseX, mouseY)
            if (page.setting != null) {
                switch.render(screenDrawing, mouseX, mouseY)
            }
        }
    }

    fun checkValidVersion() {
        if (!Security.verificationState.allowed) setRenderableScreen(VersionInvalidScreen)
    }

    object VersionInvalidScreen : Renderable() {
        const val SECURITY_MESSAGE =
            "Your version of Capy Client could not be verified. This probably means that the file your are using was changed after downloading or the version you are using was removed from Modrinth due to a critical bug.\n\nPlease download the newest version from Modrinth to ensure you are using a safe version.\n\nIf you believe this is an error, please let us know on GitHub."

        override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
            screenDrawing.wrapText(SECURITY_MESSAGE + "\n\nError message: ${(Security.verificationState as? Security.ILLEGAL)?.reason ?: "Unknown"}", 300).let {
                screenDrawing.drawCenteredWrappedText(it, width / 2, height / 2 - it.size * 9 / 2 - 30, Color.WHITE, DEFAULT_FONT, true)
            }

            screenDrawing.transform(width - 5f, height - 11f, 0.7f) {
                screenDrawing.drawRightAlignedText("Capy Client ${BuildInfo.VERSION}", 0, 0, General.getThemeColor(alpha = 0.5f))
            }

            renderRenderables(screenDrawing, mouseX, mouseY)
        }

        override fun init() {
            addRenderable(MinecraftButton(CommonComponents.GUI_BACK) {
                setScreen(null)
            }(width / 2 - 102, height / 2 + 50, 100, 20))
            addRenderable(MinecraftButton(modrinthButtonText()) {
                Util.getPlatform().openUri(Constants.MODRINTH_URL)
            }(width / 2 + 2, height / 2 + 50, 100, 20))
        }

        override fun onKeyPress(key: Int, scanCode: Int, modifiers: Int): Boolean {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                setScreen(null)
                return true
            }
            return super.onKeyPress(key, scanCode, modifiers)
        }
    }

    override fun init() {
        super.init()
        addRenderable(sidebarPlane(37, 37, 120, height - 101))
        addRenderable(image(37, height - 59, 120, 22))
        if (page.setting != null) {
            addRenderable(switch.setPosition(width - 37 - switch.width, 37))
        }
        page.header.setPosition(175, 37).setWidth(width - 211).let { addRenderable(it) }
        addRenderable(page.pane.invoke(175, 37 + (page.header.height + 5), width - 211, height - 74 - (page.header.height + 5)))
    }

    fun changeCategory(category: SidebarFeature, instant: Boolean = false) {
        this.category = category.id.toString()

        if (instant) {
            pageStack.removeAll { pageStack[0] != it }
            pageStack.add(Page(category.getHeader(), category.getRenderable()))
            return resize()
        }

        insideMainAnimation.set(0f) {
            pageStack.removeAll { pageStack[0] != it }
            pageStack.add(Page(category.getHeader(), category.getRenderable()))
            resize()
            insideMainAnimation.set(1f)
        }
    }

    fun openPage(afterHeader: Renderable, afterPane: Renderable, setting: Setting<Boolean>? = null, instant: Boolean = false) {
        if (instant) {
            pageStack.add(Page(afterHeader, afterPane, setting))
            return resize()
        }

        insideMainAnimation.set(0f) {
            pageStack.add(Page(afterHeader, afterPane, setting))
            resize()
            insideMainAnimation.set(1f)
        }
    }

    fun goBack(instant: Boolean = false) {
        if (pageStack.size == 1) return close()
        if (pageStack.size == 2) category = "capyclient:home"

        if (instant) {
            pageStack.removeLast()
            return resize()
        }

        insideMainAnimation.set(0f) {
            pageStack.removeLast()
            resize()
            insideMainAnimation.set(1f)
        }
    }

    fun close() {
        blurMainAnimation.set(0f)
        alphaMainAnimation.set(0f) {
            setScreen(null)
        }
    }

    class Page(val header: Renderable, val pane: Renderable, val setting: Setting<Boolean>? = null)

    override fun onKeyPress(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        return super.onKeyPress(key, scanCode, modifiers)
    }

    override fun getBackgroundEffectFactor(): Float {
        return blurMainAnimation.get()
    }
}