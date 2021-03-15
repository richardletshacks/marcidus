# marcidus
marcidus is a WIP configurable reddit to instagram repost bot that also supports image galleries (albums on instagram, up to 10 images) and videos (requires [youtube-dl](https://github.com/ytdl-org/youtube-dl/) installed).

This project is using [instagram4j](https://github.com/instagram4j/instagram4j) as a wrapper for the private instagram api.

## Configuration
```
{
  "username": "example",    -> The username of the bot account
  "password": "dummy123",   -> The password of the bot account
  "subreddits": [
    "okbuddyretard",        -> A list of subreddits you want the bot to repost from
    "dogelore"
  ],
  "min_delay": 3600000,     -> Minimum value for random delay (milliseconds)
  "max_delay": 5400000,     -> Maximum value for random delay (milliseconds)
  "description": "{title}"  -> Instagram post description template. Any field of the RedditPost class can be used as placeholder (See src/main/kotlin/yt/richard/marcidus/utils/RedditUtils.kt)
}
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)
