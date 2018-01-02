package me.urielsalis.memefinder

import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.Message
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

        val message = DB.resolve(text, userName)
        val richMessage = RichMessage(message.text)
        if(!message.privateMessage) {
            richMessage.responseType = "in_channel"
        }
        if(!message.link.isNullOrBlank()) {
            richMessage.attachments = arrayOfNulls<Attachment>(1)
            richMessage.attachments[0] = Attachment()
            richMessage.attachments[0]!!.imageUrl = message.link
            richMessage.attachments[0].authorName = userName
            richMessage.attachments[0].fallback = message.link
        }
        return richMessage
    }

}