package me.weiwen.tsundoku

import me.weiwen.tsundoku.managers.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
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
                "give" -> {
                    val book = bookManager.books[args.drop(1).joinToString(" ")]
                    if (sender is Player && book != null) {
                        val item = ItemStack(Material.WRITTEN_BOOK)
                        val meta = (item.itemMeta as BookMeta).apply {
                            title(book.title())
                            author(book.author())
                            addPages(*book.pages().toTypedArray())
                        }
                        item.itemMeta = meta
                        sender.inventory.addItem(item)
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
        command?.setTabCompleter { _, _, _, args ->
            when (args.size) {
                0 -> listOf("reload", "give")
                else -> listOf()
            }
        }

        logger.info("Tsundoku is enabled")
    }

    override fun onDisable() {
        logger.info("Tsundoku is disabled")
    }
}
