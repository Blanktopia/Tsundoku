package me.weiwen.tsundoku.managers

import kotlinx.serialization.Serializable
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import me.weiwen.tsundoku.Tsundoku
import me.weiwen.tsundoku.datastructures.Trie
import me.weiwen.tsundoku.datastructures.add
import me.weiwen.tsundoku.serializers.FormattedString
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.*
import java.io.File
import java.util.logging.Level
import kotlin.io.path.invariantSeparatorsPathString

@Serializable
data class BookTemplate(val pages: List<FormattedString>)

val BookTemplate.book: Book
    get() = Book.book(Component.text("Tsundoku"), Component.text("Tsundoku"), pages.map { it.component })

class BookManager(val plugin: Tsundoku) {
    var books: Map<String, Book> = mapOf()
        private set
    var bookTitles: Trie<Char, String> = Trie(null)

    fun enable() {
        load()

        val command = plugin.getCommand("book")
        command?.setExecutor { sender, _, _, args ->
            val book = books[args.joinToString(" ")]
            if (sender is Player && book != null) {
                sender.openBook(book)
                sender.playSound(sender.location, "item.book.page_turn", 1.0f, 1.0f)
                true
            } else {
                false
            }
        }
        command?.setTabCompleter { _, _, _, args ->
            var node = bookTitles
            val word = args.joinToString(" ")
            for (char in word) {
                node = node[char] ?: return@setTabCompleter node.toList()
            }
            node = node[' '] ?: return@setTabCompleter node.toList()
            node.toList()
        }
    }

    fun load() {
        val directory = File(plugin.dataFolder, "books")

        if (!directory.isDirectory) {
            directory.mkdirs()
        }

        val files = directory.walkTopDown().filter { file -> file.extension in setOf("json", "yml", "yaml") }

        val rootPath = directory.toPath()

        books = files
            .mapNotNull { file ->
                parse(file)?.let {
                    val parent = rootPath.relativize(file.parentFile.toPath())
                        .invariantSeparatorsPathString.replace('/', ' ')
                    if (file.nameWithoutExtension == "index") {
                        parent
                    } else {
                        "$parent ${file.nameWithoutExtension}"
                    }.trim() to it
                }
            }
            .associate { it }

        bookTitles = Trie(null)
        books.forEach { (path, _) ->
            bookTitles.add(path)
        }
    }

    private val json = Json {}

    private val yaml = Yaml(
        EmptySerializersModule,
        YamlConfiguration(
            polymorphismStyle = PolymorphismStyle.Property
        )
    )

    fun parse(file: File): Book? {
        plugin.logger.log(Level.INFO, "Parsing '${file.name}'")

        val format = when (file.extension) {
            "json" -> json
            "yml", "yaml" -> yaml
            else -> return null
        }

        val text = file.readText()
        val template = try {
            format.decodeFromString<BookTemplate>(text)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, e.message)
            return null
        }

        return template.book
    }
}