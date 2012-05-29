# TaxCollector
## Tax your users for placing/breaking bloks, crafting items and fishing.
### Version 1.1

## Features:
 * specifing for which actions player will be taxed
 * specifing player which will take collected money (it can be set globally and for each block type individually)
 * possibility to define receiver aliases in separated config section (eg. you can add all stones/minerals to "Mine" alias and then define, in one place, who will get taxes from that; check config example)
 * possibility to create signs with collected taxes statistics (separately for each user/alias)
 * auto version checking (but you can decide if you want to update)
 * supporting many economy plugins via Vault
 
## Dependencies
 * Vault (required)
 
## Permissions:
 * taxcollector.immunity: Gives player tax immunity
 * taxcollector.signs.create: Allows player to create sign with tax counter
 * taxcollector.signs.refresh: Allows player to refresh sign data on right click
 
## Configuration:
    receiver: Player1 # global tax receiver, can be overridden in each action
    debug: true # debug mode, sends messages about tax amount
    signs:
     refresh: 300 # tax statistics on the signs will be updated every 300 seconds
    aliases:
     Mine: Player3
    taxes:
     place:
	  '1': # tax section for placing stone
	   amount: 1 # it'll tax user with 1 currency
	   receiver: Player2 # it'll give all money colected from this section to Player2 instead of Player1
     break: # break actions section
	  '1': # here we'll tax players from breaking stone
	   amount: 0.5 # you can set amount with a precision of 0.01
	   receiver: Mine # tax will go to Player3, it's set in aliases section
	  '3': # here we'll tax players from breaking dirt (receiver is not set, so it'll go to global receiver)
	   amount: 0.5 # you can set amount with a precision of 0.01
	 fish: # fish actions section
     craft: # items craft actions section
      '280': # stick crafting tax
       amount: 1 # user will craft 4 sticks each time, so he'll pay 4 currency
## Version history:

### 1.1 version:
 * updated to Bukkit 1.2.5
 * added tax receivers aliases
 * added sign with collected taxes statistics
 * added new version notifications
 * added PluginMetrics support
 * changed Spout craft event to Bukkit craft event
 * fixed multiple items crafting tax (eg. sticks)
 * disabled shift crafting

### 1.0 version:
 * first public release 