package net.bewis09.capyclient.common.logic

import org.slf4j.LoggerFactory

/**
 * Interface for logging in the Bewisclient.
 */
interface BewisclientLogger {
    companion object {
        private val logger = LoggerFactory.getLogger("Capy Client")
    }

    fun info(vararg msg: Any?) = logger.info(msg.joinToString(" ") { it.toString() })
    fun warn(vararg msg: Any?) = logger.warn(msg.joinToString(" ") { it.toString() })
    fun error(vararg msg: Any?) = logger.error(msg.joinToString(" ") { it.toString() })
}