package whasa.eq.ts

import org.cyberneko.html.parsers.SAXParser

class EQRecipeThing {
	public static void main(String[] args) {
		if (!validate(args)) {
			printUsage()
			System.exit(1)
		}

		List<String> ids = []
		def exclude = []
		def master = []


		if (args.size() == 1) ids = args
		else {
			ids.addAll(args[0..args.size() -2])
			if(args.last().isInteger()) ids.add(args.last())
			else {
				println """\
			================================================================================
			===== EXCLUDING ALL KNOWN RECIPES FROM [${new File(args.last()).getCanonicalPath()}]
			================================================================================""".stripIndent()
				getClass().getResource('/master.txt').eachLine { line ->
					master.add (
							[
									name: 		line.split(/\t/)[0].replace('"', ''),
									trivial: 	line.split(/\t/)[1].replace('"', ''),
									eqtc: 		line.split(/\t/)[2].replace('"', ''),
									zam: 		line.split(/\t/)[3].replace('"', ''),
									eq: 		line.split(/\t/)[4].replace('"', ''),
									expansion: 	line.split(/\t/)[5].replace('"', ''),
							]
					)

				}
				new File(args.last()).readLines().each { String line ->
					exclude.addAll(master.findAll { it.eq == line.split(/\t/)[0] })
				}
			}
		}


		for(String id : ids) {
			def recheck = []
			println """\
			================================================================================
			===== LOCATING RECIPES FOR ITEM ID [${id}]
			================================================================================""".stripIndent()
			def items = [:]
			int count = 0
			while(true) {
				String url = "http://www.eqtraders.com/search/reverse_recipe_search.php?item=${id}&min=${count}"
				println "Checking for recipes ${count} - ${count + 25}"
				int onPage = 0
				def parser = new SAXParser()
				def page = new XmlSlurper(parser).parse(url)
				def recipes = page.depthFirst().findAll {
					def good = !it.children().findAll {
						it.@href =~ /^\/items\/show_item\.php\?item=\d+$/
					}.isEmpty()
					if(good)
					{
						onPage++
						def siblings = it.parent().children().list()
						def index = siblings.indexOf(it)
						def sibling = siblings[index - 1]
						String href = sibling.depthFirst().find { it.@href =~ /^\/items\/show_item\.php\?item=\d+&menustr=\d+$/ }.@href
						String eqtcId = href.substring(href.indexOf('item=') + 5)
						eqtcId = eqtcId.substring(0, eqtcId.indexOf('&'))
						if(exclude.find { it.eqtc == eqtcId }) {
							def masterExcluded = master.findAll { it.eqtc == eqtcId }.sort()
							def currentExcluded = exclude.findAll { it.eqtc == eqtcId }.sort()
							println ("Excluding recipes that generate EQTC item ${eqtcId}")
							if(masterExcluded == currentExcluded) {
								println '\tYou know every recipe that generates that item.'
							} else {
								println '\tYou don\'t know every recipe that generates that item. You should check it yourself manually'
								println "\thttp://www.eqtraders.com/items/show_item.php?item=${eqtcId}"
								recheck.addAll(eqtcId)
							}
							good = false
						}
					}
					return good
				}

				if(onPage == 0) {
					break
				}

				recipes.each {
					String text = it.text()
					String components = text.substring(12, text.indexOf('In: '))
					List<String> componentsList = components.split(', ')
					componentsList.each {
						String name = it
						int amount = 1
						if(it =~ /.+\(\d+\)$/) {
							amount = it.substring(it.lastIndexOf('(') + 1, it.size() - 1).toInteger()
							name = name - "($amount)"
						}
						int currentCount = items.get(name, 0)
						items.put(name, currentCount + amount)
					}
				}
				count+=25
			}

			/*
			 * TODO: Maybe in the future go and check the list of excluded recipes against Allakhazam
			 *  EQTC doesn't store things in terms of RECIPES. They store things in terms of ITEMS that a recipe produces
			 * 	So I can't know for sure that you know a recipe because you know the recipe ID,
			 * 	because there might be two recipes that generate the same EQTC item.
			 * 	Right now I'm just saying "go and check yourself" and ignoring it. I could eventually go and check on
			 * 	Allakhazam, for you, but I'm lazy.
			 */
//			println """\
//			================================================================================
//			===== Rechecking excluded items for possible multiple recipes
//			================================================================================""".stripIndent()
//			recheck.each { eqtcId ->
//			}

			println """\
			================================================================================
			===== ITEMS REQUIRED FOR ITEM ID [${id}] RECIPES:
			===== IMPORTANT: NOT ALL RECIPES ARE GUARANTEED TO COUNT TOWARDS 350 SKILL
			================================================================================""".stripIndent()
			def keys = items.keySet().sort()
			keys.each {
				println "${it}: ${items[it]}"
			}
		}
	}

	static boolean validate(String[] args) {
		if(args.size() == 0) return false
		if(args.size() == 1) return args[0].isInteger()
		for(int i = 0; i < args.size(); i++)
		{
			String s = args[i]
			if(!s.isInteger())
			{
				if(i != args.size() - 1) return false
				if(!new File(s).exists()) return false
			}
		}
		return true
	}

	static void printUsage() {
		System.err.println("""\
You dun goofed.
Provide a list of EQTC Item IDs followed by an optional `/outputfile recipes <tradeskill>` file of recipes to exclude.
If the optional `/outputfile` file is provided the file must exist.
Examples:
	List components of all recipes using Fine Parchment:
		java -jar EQRecipeThing.jar 20916
	List components of all recipes using Fine Parchment that you don't already know:
		java -jar EQRecipeThing.jar 20916 C:/EverQuest/YourName_cazic-Resedarch-Recipes.txt
	List components of all recipes using Fine Parchment and all recipes using Celestial Solvent: 
		java -jar EQRecipeThing.jar 20916 1618""")
	}
}
