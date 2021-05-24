package me.weiwen.tsundoku

import me.weiwen.tsundoku.managers.*
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class Tsundoku: JavaPlugin() {
    companion object {
        lateinit var plugin: Tsundoku
            private set
    }

    var config: TsundokuConfig = parseConfig(this)

    val bookManager: BookManager by lazy { BookManager(this) }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        bookManager.enable()

        val command = getCommand("tsundoku")
        command?.setExecutor { sender, _, _, args ->
            when (args[0]) {
                "reload" -> {
                    if (args.size == 1) {
                        config = parseConfig(this)
                        bookManager.load()
                    }
                    sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded configuration!")
                    true
                }
                else -> false
            }
        }
        command?.setTabCompleter { _, _, _, args ->
            when (args.size) {
                0 -> listOf("reload")
                else -> listOf()
            }
        }

        logger.info("Tsundoku is enabled")
    }

    override fun onDisable() {
        logger.info("Tsundoku is disabled")
    }
}
