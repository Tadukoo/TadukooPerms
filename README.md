Tadukoo Perms
=============

Description
-----------
Tadukoo Perms is a permissions plugin for Bukkit.

Features
--------
Plugin gets player's rank on join.

If a player doesn't have a rank set, it will set it to the default for that world.

If there is no default for that world, it will use the default folder.

When a user's permissions are reloaded, it sets "< UUID >.player" for them, so that a server administrator can tell who's who in the users.yml, but doesn't 
have to worry about updating if a player's name changes (UUID's are used).

When a user's permissions are reloaded, it checks if their rank exists. If not, it resets it to the default-rank set in the world's config.yml.

Command Usage
-------------
/tperm setrank < world > < player > < rank >
* Sets a player's rank for the specified world.
* Checks to see if the rank is listed in the world's ranks list in the config.yml and fails if it isn't listed.
* Uses Tadukoo Core to get the UUID of the player to use.

/tperm perm < rank > < world > < permission > [true|false|remove]
* Putting in a true or false sets the permission.
* Putting in "remove" removes it.
* Leaving out a value or "remove" shows the value of the permission (true, false, or null).

/tperm rank < create|remove > < rank > < world >
* Creates/removes a < rank > from a < world >.
* This removes the rank from the world's ranks list in the config.yml and removes everything from the < rank >.yml file.

/tperm reload
* Reloads the permissions.

Permissions
-----------
TadukooPerms.use

Configuration
-------------
Config.yml
----------
player-messages: true
* Decides whether players will get messages when their rank changes.

chat-formatting: true
* Decides whether to format the chat or not.

worlds:

- default
* List all your worlds here.

/< world >/config.yml
-------------------
default-rank: default_member
* The default rank in < world >.

ranks:

- default_members

- default_ops
* The ranks available in < world >.

/< world >/users.yml
------------------
UUID-of-a-player:

   player: Player's Name
   
   rank: player's_rank
   
UUID-of-another-player:

   player: Another_Player's_Name
   
   rank: another_player's_rank
* The settings of a player in < world >

/< world >/< rank >.yml
-------------------
permissions:

- TadukooPerms.use

- ^TadukooPerms.setrank

- ^TadukooPerms.perm

- ^TadukooPerms.rank
* The permissions for < rank > in < world >.
* Use a ^ before the permission to set it to false.

inherits: default_member
* The rank this inherits from.
* Use this so you don't have to retype all the permissions all the time.

prefix: '[Member]'
* The prefix to put before the player's name in chat.

Upcoming Commands/Features
--------------------------
Make an Enum for valid things to type in commands/checking files for worlds and ranks?

/tperm world < create|remove > <world>
* Creates or removes a world from the plugin.

/tperm set < world > < setting > [value]
* Sets or shows a value in a world's config file.

/tperm setrank "all" < player > < rank >
* Sets a player's rank to < rank > in all worlds that contain it.

Make it so that when a player is reloaded, it checks all worlds for their UUID to change the player name.

Issue if command /tperm setrank typed wrong.. nullpoint on MainCommandExecutor line 34

Make it so that a world can have the permissions from another world (or inherit them?)
* This would be all ranks and the users.yml being inherited between worlds.

Make it so that a rank can inherit from another world's rank. (i.e. Member in world_nether inherits from Member in world)
* This is just per rank, like world_nether's Member could have "inherits: world:Member", while the other world_nether ranks aren't inherited from world.

Commands to show inheritance chains, unique permissions, and all permissions in a chain.

Known Issues
------------
None currently.

Download
--------
Currently you are unable to download the plugin jar file. Eventually this plugin will be available on BukkitDev.

Currently to download and install this plugin, you must download the classes and plugin.yml file, then import them into eclipse, along with importing the
Bukkit API (Currently this plugin is built off of [1.7.9-R0.2 Beta Build 1938] (http://dl.bukkit.org/downloads/bukkit/view/02630_1.7.9-R0.2/)).

Version System
--------------
May be documented in the future.
