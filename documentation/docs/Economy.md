Plugin supports Vault,  which means you can withdraw in-game currency from players on some actions, like

- Clan creation
- Clan home creation
- Clan home upgrade

More will be added in the future.

To get this working, install **any** economy plugin with Vault support and Vault itself on your server.
Then, set `enable-economy-hook` to `true` in config.yml

If you wanna make actions priceless for some players, give them `clans.economy.ignore` role.

All prices are configurable at `prices.yml`.