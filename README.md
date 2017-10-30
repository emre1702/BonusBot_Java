# BonusBot

This is a bot for Discord, written in Java.  
It uses LavaPlayer and Discord4J.  


## What can the bot do?  

It can play audio from:
* YouTube
* SoundCloud
* Bandcamp
* Vimeo
* Twitch streams
* Local files
* HTTP URLs  

More informations about the audio:  
[LavaPlayer GitHub](https://github.com/sedmelluq/lavaplayer/tree/5019bd8173a0d8b99ff5fc53149774d77792702c)

Also it can manage the languages of the user.  
Currently supported are:  
* English
* German
* Turkish  

If you understand a little bit of Java, you can add other languages by yourself (and maybe do a pull request).

Oh and there is the command "8ball", just test it out.


## What is discordbot.conf?

There you have to change all the settings to be able to use the bot on your server.  
First of all you have to add your token.  
If you want to disable anything, just delete the settings or use "-1" for the value.  
But DON'T delete "token" and the "EmojiID" settings, these are important. 


## How can I use it?

### Download
First of all you have to download it.  
Head over to the releases-tab:  
[Releases](https://github.com/emre1702/BonusBot/releases)  

Here you can choose between:  
- BonusBot_vX_X.jar  
-- Choose this if you are just lazy or don't want to download the future updates. This file contains every package, even when they won't change in future updates. The main-program is not even 100 KB big. 
-- If you chose this, also download bonusbot.conf.
- BonusBot_vX_X_SingleOnly.jar
-- Choose this if you already got all the packages and only want to update your bot.
-- If you chose this, you should also download bonusbot.conf.
- BonusBot_vX_X_SingleAll.jar
-- Choose this if it's the first time you are downloading the packages and want to download the updates in the future easily.
-- Contains everything you need.

### Settings
Now you can change the settings in bonusbot.conf.  
You can deactivate a optional setting by deleting the line or using "" (empty string).

#### needed 
- token  
-- Token of the created bot. 
-- Can be found here: https://discordapp.com/developers/applications/me
-- Click on your bot (or create it) and use its "Client ID"

#### optional
- prefix
-- How you want to use the commands (e.g. !play - "!" would be the prefix)  
-- default: "!"
-- deactivated: "!"
- name
-- Name of the bot.
-- default: "Bonus-Bot"
-- deactivated: "!"
- playing
-- Playing-text
-- default: "Bonus community"
-- deactivated: "Bonus community"
- languageChannel
-- Channel where the use the language-section commands to get the roles.
-- default: "language"
-- deactivated: No language-section rights.
- audioChannel
-- Channel where to use the audio-commands.
-- default: "audio_commands"
-- deactivated: everywhere
- audioInfoChannel
-- Channel where to post the audio-infos (embed)
-- default: "audio_info"
-- deactivated: no audio-infos
- greetUserChannel
-- Where to greet the user
-- default: "english"
-- deactivated: no greeting
- audiobotUserRole
-- Role to use the audio-commands
-- default: "audio-bot user"
-- deactivated: everyone can use the audio-commands
- englishRole / germanRole / turkishRole
-- Roles to be able to access the language-sections.
-- default: "English" / "German" / "Turkish"
-- deactivated: language-commands won't work
- whatEmoji / hahaEmoji / tadaEmoji
-- Emoji-names for messages (e.g. on "!8ball" the whatEmoji is used)
-- default: "what" / "haha" / "tada"
-- deactivated: no emojis will be used

#### start

Just put it somewhere and start it with:  
java -jar bonusbot.jar   
Or whatever the name is.


## Can I just use your bot?

Later with another release. 
Have to change some things.
