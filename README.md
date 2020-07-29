# EQRecipeThing

Mostly used for and tested with Spell Research. Might work fine for other tradeskills, but there are probably some bugs.

`java -jar EQRecipeThing.jar <id>...`

Pass one or more item IDs as parameters. For example, `http://www.eqtraders.com/items/show_item.php?item=20916` item ID is `20916`.

You will get a list back of all of the components required for all of the combines used by that item. Doesn't recurse through subcombines or anything. Just gets all of the recipes that use a particular item and adds up all of their components.

For items that are used to make an item that has a lot of different recipes you might gety wonky results. For example, Celestial Solvent to make Celestial Essence.

### New in version 1.1:
You can now pass an **optional** file at the end of the list of IDs. This file should include a list of recipes you already know, and they will be potentially excluded from the output. To generate this file log in to your character and run `/outputfile recipes <tradeskill>`. Run `/outputfile recipes` for a list of possible `<tradeskill>` options.

This does take some extra time for larger files because it has to cross-reference to a master list of all possible recipes to match the EQTC ID with the actual EQ recipe. Be patient.

```
Examples:
	List components of all recipes using Fine Parchment:
		java -jar EQRecipeThing.jar 20916
	List components of all recipes using Fine Parchment that you don't already know:
		java -jar EQRecipeThing.jar 20916 C:/EverQuest/YourName_cazic-Resedarch-Recipes.txt
	List components of all recipes using Fine Parchment and all recipes using Celestial Solvent: 
		java -jar EQRecipeThing.jar 20916 1618
```

Excluded recipes are **not perfect**. This was originally intended to be used for Spell Research, and it mostly works fine for that. Issues arise when there are multiple recipes that generate the same item. Because of how EQTC stores **items** and not **recipes** it is difficult to tell if you know a particular recipe for a given item just because you know a recipe that produces that item. Read the output carefully to understand if you might need to look something up manually afterwards. I might eventually get around to making it smarter, but for now I'm lazy.

It's also important to note that not all of the recipes this tool finds for you are guaranteed to count towards 350 in a given skill. Again, laziness. I'll get around to making it smarter later, maybe.
