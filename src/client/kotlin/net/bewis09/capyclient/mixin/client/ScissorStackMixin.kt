package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.version.ScissorStack
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import java.util.Deque

@Mixin(ScissorStack::class)
interface ScissorStackMixin {
    @Accessor
    fun getStack(): Deque<ScreenRectangle>
}