Better Farmland is meant to improve your experience with your farmland.
But there are also other functions for people who want something fancy.
If you have any suggestions for the plugin/mod, please use the [Discussion](https://www.spigotmc.org/threads/better-farmland.566196/) function.

Plugin for [spigot](https://www.spigotmc.org/resources/better-farmland.103677/) or as mod for [fabric and forge](https://modrinth.com/mod/betterfarmland/)

For developers: If you want to extend my mod or make it compatible you can use my [repo](https://repo.httprafa.link/).

# Images

## Rightclick Harvest
![rightClickHarvest](https://s4.gifyu.com/images/2022-08-16-05-43-07.gif)

## Standard
![Standard](https://s4.gifyu.com/images/standard6c46a6e9302d7a61.gif)

## Example 1
![Example 1](https://s4.gifyu.com/images/1_example.gif)
```
{
  "configVersion": 1,
  "mod": {},
  "event": {
    "prevent": true,
    "crops": {
      "change": true,
      "changes": [
        {
          "use": true,
          "sound": {
            "sound": "minecraft:block.crop.break",
            "volume": 1.0,
            "pitch": 1.0
          },
          "from": 0,
          "to": "minecraft:air",
          "drop": {
            "item": 0,
            "amount": -1
          },
          "newAge": -1
        },
        {
          "use": false,
          "sound": {
            "sound": "minecraft:block.crop.break",
            "volume": 1.0,
            "pitch": 1.0
          },
          "from": 0,
          "to": 0,
          "drop": null,
          "newAge": 0
        }
      ]
    }
  }
}
```

## Example 2
![Example 2](https://s4.gifyu.com/images/2_example.gif)
```
{
  "configVersion": 1,
  "mod": {},
  "event": {
    "prevent": true,
    "crops": {
      "change": true,
      "changes": [
        {
          "use": false,
          "sound": {
            "sound": "minecraft:block.crop.break",
            "volume": 1.0,
            "pitch": 1.0
          },
          "from": 0,
          "to": "minecraft:air",
          "drop": {
            "item": 0,
            "amount": -1
          },
          "newAge": -1
        },
        {
          "use": true,
          "sound": {
            "sound": "minecraft:block.crop.break",
            "volume": 1.0,
            "pitch": 1.0
          },
          "from": 0,
          "to": 0,
          "drop": null,
          "newAge": 0
        }
      ]
    }
  }
}
```

# Documentation
## Files
```
Config » config/betterfarmland/config.json
```

## Config
```
{
  "configVersion": 2,
  "mod": {},
  "rightClickHarvest": {
    "use": false,
    "sounds": [
      {
        "sound": "minecraft:block.crop.break",
        "volume": 1.0,
        "pitch": 1.0
      },
      {
        "sound": "minecraft:block.pumpkin.carve",
        "volume": 0.75,
        "pitch": 1.0
      }
    ]
  },
  "landedUpon": {
    "preventBreak": true,
    "crops": {
      "change": false,
      "changes": [
        {
          "use": false,
          "sound": {
            "sound": "minecraft:block.crop.break",
            "volume": 1.0,
            "pitch": 1.0
          },
          "from": 0,
          "to": "minecraft:air",
          "drop": {
            "item": 0,
            "amount": -1
          },
          "newAge": -1
        },
        {
          "use": false,
          "sound": {
            "sound": "minecraft:block.crop.break",
            "volume": 1.0,
            "pitch": 1.0
          },
          "from": 0,
          "to": 0,
          "drop": null,
          "newAge": 0
        }
      ]
    }
  }
}
```
```
configVersion: Is set to the current config version.
prevent: Is whether the plugin should cancel the event or not. Effect: nothing happens to the farmland.
change and changes: In change you can specify whether you want to use the changes function. The changes function determines what should happen when an entity / player jumps onto the farmland.

# rightClickHarvest:
use: Whether you want to use the right-click function.
sounds: Which sounds should be played when a plant is harvested.

# landedUpon:
use: Whether this rule should come into force.
sound: What sound to play when this rule is used.
from: From which block
to: To which block
drop: You can specify which item should be dropped.
newAge: You can change the age of the new block. -1 = same as old
```
