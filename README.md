# EQRecipeThing

Mostly used for and tested with Spell Research. Might work fine for other tradeskills, but there are probably some bugs.

`java -jar EQRecipeThing.jar <id>...`

Pass one or more item IDs as parameters. For example, `http://www.eqtraders.com/items/show_item.php?item=20916` item ID is `20916`.

You will get a list back of all of the components required for all of the combines used by that item. Doesn't recurse through subcombines or anything. Just gets all of the recipes that use a particular item and adds up all of their components.

For items that are used to make an item that has a lot of different recipes you might get wonky results. For example, Celestial Solvent to make Celestial Essence.
