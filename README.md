# EnvironmentZ
EnvironmentZ is an environment mod which makes the life conditions harder.

### Installation
EnvironmentZ is a mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api), [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) and [AutoTag](https://www.curseforge.com/minecraft/mc-mods/autotag) to be installed separately; all other dependencies are installed with the mod.

### License
EnvironmentZ is licensed under GLPv3.

### Commands
`/environment affection playername hot:cold bool`
- Set the player environment affection for cold and hot biomes

`/environment resistance playername hot:cold integer`
- Set the player resistance for cold and hot biomes

`/environment protection playername hot:cold integer`
- Set the current player protection for cold and hot biomes

`/environment temperature playername integer`
- Set the player temperature

`/info ...`
- Print the current values

### Datapacks

Most of the mods default settings can get changed via datapacks.
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.fandom.com/wiki/Data_Pack)
website and try to create your first one for the vanilla game.\
If you know how to create one, the folder path has to be ```data\environmentz\FOLDER\YOURFILE.json```\
Caution! Make sure you name the files differently than the existing ones.\
If you want to overwrite a setting, do not forget to set the "replace" value to true!

#### 1. Manager
The first folder is called manager where you can replace the default base values of the body temperature and set temperature values for specific dimensions.

```json
{
    "body_temperature": { 
        "replace": false, 
        "max_very_cold": -1200, // player will get freeze damage
        "max_cold": -600, // below this, player will get worse effects
        "min_cold": -60, // below this, player will get debuffs
        "normal": 0, // should be 0
        "min_hot": 60, // higher than this, player will get debuffs
        "max_hot": 600, // higher than this, player will get worse debuffs
        "max_very_hot": 1200 // player will drain exhaustion/thirst
    },
    "body_wetness": {
        "replace": false,
        "max_wetness": 200, 
        "soaked": 180, // if the player is more wett than this, it is considered as soaked
        "water": 200, // while the player is in water, he will get this value applied each second
        "rain": 5, // while in rain the player will get more wett by this
        "dry": -5 // if the player is wett and not in water/rain, the wetness will get reduced by this
    },
    "body_protection": {
        "replace": false,
        "max_heat": 200,
        "max_cold": 200,
        "max_heat_resistance": 200,
        "max_cold_resistance": 200
    },
    "biome_temperature": { // check out the biome wiki of minecraft to figure out what temperatures a biome has
        "replace": false,
        "very_cold": 0.2, // biome temperature like this and below will get considered as very cold
        "cold": 0.4, // ...
        "hot": 1.2,
        "very_hot": 1.6
    },
    "thermometer_temperature": { // values for the thermometer on the gui
        "replace": false,
        "very_cold": -8,
        "cold": -4,
        "hot": 4,
        "very_hot": 8
    },
    "acclimatization": { 
        "replace": false,
        "hot_body_temperature": 30, // while body temperature is higher than this and the player is in a normal temperatured biome, "hot_body" value will get applied
        "hot_body": -5,
        "very_hot_body_temperature": 580, // while body temperature is higher than this and the player is in a hot biome, "very_hot_body" value will get applied
        "very_hot_body": -5,
        "cold_body_temperature": -30, // ...
        "cold_body": 5,
        "very_cold_body_temperature": -580,
        "very_cold_body": 5
    },
    "effect": { // set temperature and protection values for any status effects
        "environmentz:cooling": {
            "replace": false,
            "temperature": -5,
            "cold_protection": 2
        },
        "environmentz:warming": {
            "replace": false,
            "temperature": 5,
            "heat_protection": 2
        }
    }
}
```

An example to customize dimension temperatures:
```json
{
    "minecraft:overworld": { // must be the dimension identifier
        "replace": false,
        "standard": { // standard doesn't have to be used, if not existent or does only have 0 values, day/night values will get used.
            "very_cold": 0,
            "cold": 0,
            "normal": 0,
            "hot": 0,
            "very_hot": 0
        },
        "day": { // will only be applied if standard has only 0 values or not existent and it is day in the dimension
            "very_cold": -4, // for very cold biomes
            "cold": -2, // for cold biomes
            "normal": 1, // ...
            "hot": 2,
            "very_hot": 4
        },
        "night": {
            "very_cold": -8,
            "cold": -4,
            "normal": -1,
            "hot": 1,
            "very_hot": 2
        },
        "armor": { // can have float values
            "very_cold": 1,
            "cold": 0.5,
            "normal": 0,
            "hot": 1,
            "very_hot": 2
        },
        "insulated_armor": { // can have float values
            "very_cold": 2,
            "cold": 1,
            "normal": 0.5,
            "hot": 2,
            "very_hot": 3
        },
        "iced_armor": { // can have float values
            "very_cold": -5,
            "cold": -5,
            "normal": -5,
            "hot": -5,
            "very_hot": -5
        },
        "soaked": { // if player is soaked
            "very_cold": -8,
            "cold": -6,
            "normal": -4,
            "hot": -4,
            "very_hot": -4
        },
        "wett": { // if player is wett but not soaked
            "very_cold": -4,
            "cold": -2,
            "normal": -2,
            "hot": -2,
            "very_hot": -2
        },
        "sweat": { // with dehydration it consumes hydration, without it consumes food
            "hot": -2,
            "very_hot": -3
        },
        "shadow": { // if the player is under cover
            "very_cold": -1,
            "cold": -1,
            "normal": -1,
            "hot": -1,
            "very_hot": -1
        },
        "height": { 
            "very_low": 3,
            "very_low_height": 0,
            "low": 1,
            "low_height": 30,
            "high": -2,
            "high_height": 120,
            "very_high": -3,
            "very_high_height": 190
        }
    }
}
```

For a more simplified way to customize a dimension you have to set the "basic" value to true like in the following example.
Basic allows you to add the "acclimatization" field to replace the default acclimatization.
```json
{
    "minecraft:nether": {
        "replace": false,
        "basic": true,
        "standard": 6,
        "armor": 8,
        "insulated_armor": 12,
        "soaked": -4,
        "wett": -2,
        "sweat": -4,
        "shadow": 0,
        "height": 0,
        "acclimatization": 0
    }
}
```

#### 2. Blocks
The second folder is called environment_blocks where temperatures for blocks can be set.
```json
{
    "minecraft:powder_snow": { // block identifier
        "max_count": 4, // max count for this block type around the player
        "0": -5, // distance 0 to the player will apply this value
        "1": -4, // distance 1 to the player will apply this value
        "2": -3, // ...
        "3": -2 // for further distances, you have to increase the radius check in the configs
    },
    "minecraft:blast_furnace": {
        "property": "lit", // block has to have this boolean property if "property" is set
        "max_count": 4,
        "0": 4,
        "1": 3,
        "2": 2,
        "3": 1
    }
}
```

#### 3. Items
The third folder is called environment_items where temperatures for items can be set. Those items will only apply temperature on the player while being hold in the hand.
```json
{
    "environmentz:heating_stones": {
        "damage": 1, // item has to be damageable if this is set
        "temperature": 3,
        "cold_protection": 1
    },
    "minecraft:lava_bucket": {
        "temperature": 1
    }
}
```
