package me.urielsalis.memefinder

import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter


class Discord: ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent?) {
        if(event!=null) {
            val message = DB.resolve(event.message.contentDisplay, event.author.name, true) ?: return

            if(!message.link.isBlank()) {
                event.textChannel.sendMessage(message.link).queue()
            } else {
                if(message.privateMessage) {
                    event.textChannel.sendMessage(message.text).queue()
                } else {
                    event.textChannel.sendMessage(message.text).queue()
                }
            }
        }
    }

    override fun onReady(event: ReadyEvent?) {
        System.out.println("Ready!")
    }
}