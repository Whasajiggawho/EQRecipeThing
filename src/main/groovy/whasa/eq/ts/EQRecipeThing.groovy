package whasa.eq.ts

import org.cyberneko.html.parsers.SAXParser

class EQRecipeThing {
	public static void main(String[] args) {
		if(!validate(args)) {
			printUsage()
			System.exit(1)
		}

		for(String id : args) {
			println """\
			================================================================================
			===== LOCATING RECIPES FOR ITEM ID [${id}]
			================================================================================""".stripIndent()
			def items = [:]
			int count = 0
			while(true) {
				String url = "http://www.eqtraders.com/search/reverse_recipe_search.php?item=${id}&min=${count}"
				println url
				def parser = new SAXParser()
				def page = new XmlSlurper(parser).parse(url)
				def recipes = page.depthFirst().findAll {
					!it.children().findAll {
						it.@href =~ /^\/items\/show_item\.php\?item=\d+$/
					}.isEmpty()
				}

				if(recipes.isEmpty()) {
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
			println """\
			================================================================================
			===== ITEMS REQUIRED FOR ITEM ID [${id}] RECIPES:
			================================================================================""".stripIndent()
			def keys = items.keySet().sort()
			keys.each {
				println "${it}: ${items[it]}"
			}
		}




		//www.eqtraders.com/search/reverse_recipe_search.php?item=20916
		//http://www.eqtraders.com/search/reverse_recipe_search.php?item=20916&min=25

	}

	static boolean validate(String[] args) {
		for(String s : args) {
			try {
				s.toInteger()
			} catch(Exception ignored) {
				return false
			}
		}
		return args.size() > 0
	}

	static void printUsage() {
		System.err.println("""\
		You dun goofed.""".stripIndent())
	}
}
