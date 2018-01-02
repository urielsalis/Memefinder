package me.urielsalis.memefinder

import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import java.util.concurrent.ConcurrentMap

object DB {
    val db: DB = DBMaker.fileDB("memefinder.db").make()
    val map: ConcurrentMap<String, String> = db
            .hashMap("map", Serializer.STRING, Serializer.STRING)
            .createOrOpen()
    val jda = JDABuilder(AccountType.BOT).setToken(System.getProperty("Token")).addEventListener(Discord()).buildAsync()

    fun getDB(): ConcurrentMap<String, String> {
        return map
    }

    fun init() {
        println(jda.status)

    }

    fun commit() {
        db.commit()
    }

    fun resolve(text: String, username: String): Message {
        var split = text.split(" ")
        if(split[0] == "/memefinder") {
            split = split.subList(1, split.size)
        }
        when {
            split[0]=="about" -> {
                return Message("", "Created by Uriel Salischiker (v-usalischiker)", false)
            }
            split[0]=="search" -> {
                var returnValue = ""
                for((key, value) in me.urielsalis.memefinder.DB.getDB().entries) {
                    if(key.contains(join(split).toRegex())) {
                        returnValue = "$returnValue, $key - $value"
                    }
                }
                return if(returnValue=="") {
                    Message("", "Not found", true)
                } else {
                    Message("", returnValue.substring(1), true)
                }
            }
            split[0]=="add" -> {
                if (split.size > 2) {
                    val name = join(split, true)
                    val url = split.last()
                    getDB().put(name, url)
                    commit()
                    return Message("", "Added!", true)
                } else {
                    return Message("", "Usage: /memefinder add memename memeurl", true)
                }
            }
            else -> {
                val name = join(split)
                return if(getDB()[name].isNullOrBlank()) {
                    Message("", "Not found", true)
                } else {
                    Message(getDB()[name]!!, "Result: ", false)
                }
            }
        }
    }

    private fun join(split: List<String>, removeLast: Boolean = false): String {
        val str = StringBuilder()
        if(split.size==1) {
            return split[0]
        }
        if(removeLast && split.size==2) {
            return split[0]
        }
        val latest = if(removeLast) {
            split.size-1
        } else {
            split.size
        }
        for(i in (1 until latest)) {
            str.append(split[i] + " ")
        }
        return str.toString().trim()
    }


    data class Message(val link: String, val text: String, val privateMessage: Boolean)
}