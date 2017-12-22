package me.urielsalis.memefinder

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.apache.tomcat.util.http.fileupload.IOUtils
import java.net.URL
import java.io.FileOutputStream
import java.io.File


class Discord: ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent?) {
        if(event!=null) {
            val message = DB.resolve(event.message.contentDisplay, event.author.name)

            if(!message.link.isNullOrBlank()) {
                URL(message.link).openStream().use {
                    val msg = MessageBuilder().append(message.text).build()
                    val tempFile = File.createTempFile("memefinder", "")
                    tempFile.deleteOnExit()
                    val out = FileOutputStream(tempFile)
                    IOUtils.copy(it, out)
                    event.textChannel.sendFile(tempFile, msg).queue()
                }
            } else {
                if(message.privateMessage) {
                    event.privateChannel.sendMessage(message.text)
                } else {
                    event.textChannel.sendMessage(message.text)
                }
            }
        }
    }
}