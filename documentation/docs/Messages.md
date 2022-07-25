All messages are configurable in the messages.yml.
Plugin uses [MiniMessage](https://docs.adventure.kyori.net/minimessage/format.html) format, you should really give a read to it's documentation.

## Why MiniMessage?

Legacy colors has much less power and less readable, comparing to minimessage tags.
They are more verbose, but giving you a lot of power. You can use translatable components,
Make clickable and hoverable messages, and have gradients that doesnt look like a mess.

## Placeholders

You can globally define a plugin prefix and use it everywhere.
There's special placeholders for clan, clan member, and clan home, so you can get
any information you need to display.

For example:

        preparation-title: '<first_display_name> <reset>vs <second_display_name>'

or

        accepter-message: '<prefix> <click:run_command:''/clan war accept <tag>''><red>Click here</red></click> to accept the request or run /clan war accept <clan_tag>'





