package me.urielsalis.memefinder

import org.springframework.web.bind.annotation.RestController
import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
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
    private val slackToken: String = System.getProperty("ApiKey")


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
                        returnValue = "$returnValue, $key - $value"
                    }
                }
                return if(returnValue=="") {
                    RichMessage("Not found :C")
                } else {
                    RichMessage(returnValue.substring(1))
                }
            }
            split[0]=="add" -> return if(split.size > 2) {
                val name = join(split, true)
                val url = split.last()
                DB.getDB().put(name, url)
                DB.commit()
                RichMessage("Added!")
            } else {
                RichMessage("Usage: /memefinder add memename memeurl")
            }
            else -> {
                val name = join(split)
                val richMessage = RichMessage()
                richMessage.responseType = "in_channel"
                richMessage.attachments = arrayOfNulls<Attachment>(1)
                richMessage.attachments[0] = Attachment()
                richMessage.attachments[0]!!.imageUrl = DB.getDB()[name]
                richMessage.attachments[0].authorName = userName
                richMessage.attachments[0].fallback = DB.getDB()[name]
                return if(DB.getDB()[name].isNullOrBlank()) {
                    RichMessage("Not found")
                } else {
                    richMessage.encodedMessage()
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
}