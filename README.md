# BonusBot

This is a bot for Discord, written in Java.  
It uses:  
* [Discord4J](https://github.com/Discord4J/Discord4J)  
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)  
* [Spotify-Web-API-Java](https://github.com/thelinmichael/spotify-web-api-java)  
* etc. (like Google API)


## What can the bot do?  

It can play audio from:
* YouTube  
* Spotify  
* SoundCloud  
* Bandcamp  
* Vimeo  
* Twitch streams  
* Beam   
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


### Commands
Don't forget to use the prefix (default: "!") before the command!  
See "Settings" for more infos.

#### admin
Usable everywhere, only with admin roles (setted in bonusbot.conf).  

- ban [@User] [?delMessagesForDays = 0] [Reason...]  
  - Bans a user. DelMessagesForDays is optional and only used if you use a number as second argument.  
- mute [@User] [Minutes] [Reason...]  
  - Mutes a user.  
- delmsg [@User] [?AmountMessages = 100]  
  - Deletes the last messages of a user.

#### language-roles  
Only usable in languageChannel, not usable when it's deactivated.  

- english  
- german / deutsch  
- türkce / turkish  

#### audio
Only usable in audioChannel, usable everywhere when it's deactivated.

- play [?url]  
  - Joins your channel and plays an audio (without changing the queue).  
  Use '!play' without arguments to resume an audio.  
- queue [url]  
  - Joins your server and puts audio in the queue.  
  If there is no audio playing, it gets played directly.  
- stop [?time = 0]  
  - Stops the audio and deletes the queue.  
  Use time to let the audio stop after a time (seconds).  
  If you want to use minutes, use m at the time (e.g. !stop 1m - h for hours).  
- pause [?time = 0] 
  - Pauses the audio.    
  Same for time here as in stop.  
- resume [?time = 0]
  - Resumes the audio.  
  Same for time here as in stop.  
- skip  
  - Skips the current audio. 
- volume [0-200]  
  - Changes the volume.  
- join
  - Joines your channel.  
- leave  
  - Leaves your channel.  
- playing  
  - Shows infos about the audio getting played.  
- position [0-100]  
  - Sets the audio-position (argument in percent)  
- delqueue [number]
  - Removes a song from the queue at the number/index.  
- ytsearch [text] [?showamount = 5]  
  - Searchs in YouTube and outputs the result.  
  Amount of result depends on showamount.  
  Needed for !ytplay and !ytqueue.  
- ytplay [number]  
  - Plays the audio at specific place in result of ytsearch.  
- ytqueue [number]  
  - Queues the audio at specific place in result of ytsearch.    
- pl [url]  
  - Clears the queue and plays a whole playlist (max. 100 audio).  
- plqueue [url]  
  - Adds a whole playlist in the queue (max. 100 audios).   
- shuffle  
  - Toggles the shuffle-mode.  

#### rest
Usable everywhere

- 8ball [text]  
  - Ask the bot.


## What is discordbot.conf?

There you have to change all the settings to be able to use the bot on your server.  
First of all you have to add your token.  
If you want to disable anything, just delete the settings or use "-1" for the value.  
But DON'T delete "token" and the "EmojiID" settings, these are important. 


## How can I use it?

### Requirements  
* Java Runtime  
  * You can use "apt-get install default-jre"
* Bot in Discord  
  * Create one here: https://discordapp.com/developers/applications/  

### Download
First of all you have to download it.
Then you have to export it to a package (.jar). 

### Settings
Now you can change the settings in bonusbot.conf.  
You can deactivate a optional setting by deleting the line or using "" (empty string).

#### needed 
- token  
  - Token of the created bot.   
  - Can be found here: https://discordapp.com/developers/applications/me  
  - Click on your bot (or create it) and use its "Client ID"  

#### optional
- prefix  
  - How you want to use the commands (e.g. !play - "!" would be the prefix)   
  - default: "!"  
  - deactivated: "!"  
- name  
  - Name of the bot.  
  - default: "Bonus-Bot"  
  - deactivated: "!"  
- playing  
  - Playing-text  
  - default: "Bonus community"  
  - deactivated: "Bonus community"  
- languageChannel  
  - Channel where the use the language-section commands to get the roles.  
  - default: "language"  
  - deactivated: No language-section rights.  
- audioChannel  
  - Channel where to use the audio-commands.  
  - default: "audio_commands"  
  - deactivated: everywhere  
- audioInfoChannel  
  - Channel where to post the audio-infos (embed)  
  - default: "audio_info"  
  - deactivated: no audio-infos  
- greetUserChannel  
  - Where to greet the user    
  - default: "english"  
  - deactivated: no greeting  
- audiobotUserRole  
  - Role to use the audio-commands  
  - default: "audio-bot user"  
  - deactivated: everyone can use the audio-commands  
- englishRole / germanRole / turkishRole  
  - Roles to be able to access the language-sections.  
  - default: "English" / "German" / "Turkish"  
  - deactivated: language-commands won't work  
- whatEmoji / hahaEmoji / tadaEmoji  
  - Emoji-names for messages (e.g. on "!8ball" the whatEmoji is used)  
  - default: "what" / "haha" / "tada"  
  - deactivated: no emojis will be used   
- etc ... 

### Start

Just put it somewhere and start it with:  
java -jar [FileName].jar   
