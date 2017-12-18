package me.urielsalis.memefinder

import org.springframework.web.bind.annotation.RestController
import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMapping

@RestController
class MemeController {
    /**
     * The token you get while creating a new Slash Command. You
     * should paste the token in application.properties file.
     */
    @Value("\${slashCommandToken}")
    private val slackToken: String? = null


    /**
     * Slash Command handler. When a user types for example "/app help"
     * then slack sends a POST request to this endpoint. So, this endpoint
     * should match the url you set while creating the Slack Slash Command.
     *
     * @param token
     * @param teamId
     * @param teamDomain
     * @param channelId
     * @param channelName
     * @param userId
     * @param userName
     * @param command
     * @param text
     * @param responseUrl
     * @return
     */
    @RequestMapping(value = "/memefinder", method = arrayOf(RequestMethod.POST), consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveSlashCommand(@RequestParam("token") token: String,
                              @RequestParam("team_id") teamId: String,
                              @RequestParam("team_domain") teamDomain: String,
                              @RequestParam("channel_id") channelId: String,
                              @RequestParam("channel_name") channelName: String,
                              @RequestParam("user_id") userId: String,
                              @RequestParam("user_name") userName: String,
                              @RequestParam("command") command: String,
                              @RequestParam("text") text: String,
                              @RequestParam("response_url") responseUrl: String): RichMessage {
        // validate token
        if (token != slackToken) {
            return RichMessage("Sorry! You're not lucky enough to use our slack command.")
        }

        val split = text.split(" ")
        when {
            split[0]=="about" -> {
                val richMessage = RichMessage("Created by Uriel Salischiker (v-usalischiker)")
                richMessage.responseType = "in_channel"
                return richMessage.encodedMessage()
            }
            split[0]=="search" -> {
                var returnValue = ""
                for((key, value) in DB.getDB().entries) {
                    if(key.contains(join(split).toRegex())) {
                        returnValue = returnValue + ", " + value
                    }
                }
                return if(returnValue=="") {
                    RichMessage("Not found :C")
                } else {
                    RichMessage(returnValue.substring(1))
                }
            }
            split[0]=="add" -> return if(split.size > 2) {
                DB.getDB().put(split[1], split[2])
                DB.commit()
                RichMessage("Added!")
            } else {
                RichMessage("Usage: /memefinder add memename memeurl")
            }
            else -> {
                val richMessage = RichMessage("$userName requested ${split[0]}")
                richMessage.responseType = "in_channel"
                richMessage.attachments = arrayOfNulls<Attachment>(1)
                richMessage.attachments[0] = Attachment()
                richMessage.attachments[0]!!.imageUrl = DB.getDB()[split[0]]
                return if(split[0].isBlank()) {
                    RichMessage("Not found")
                } else {
                    richMessage.encodedMessage()
                }
            }
        }
    }

    private fun join(split: List<String>): String {
        val str = StringBuilder()
        for(i in (1 until split.size)) {
            str.append(split[i] + " ")
        }
        return str.toString().trim()
    }
}