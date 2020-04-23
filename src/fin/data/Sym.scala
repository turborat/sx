package fin.data

import db.DB
import x.{Disaster, MapX}


class Sym (val id:Int, val name:String, val alt:String)
{
	override def toString = name
	def dd = DailyData(this)
	def refresh = Sym.refresh(this)
	def write = Sym.write(this)
}


object Sym
{
	private var _lastId = 0
	private val _all = new MapX[String,Sym]


	// read them in
	DB.exec("select * from fin_sym", verbose=false) {
		rs => new Sym(rs.getInt("sym_id"), rs.getString("name"), rs.getString("alt"))
	} foreach { sym =>
		_all(sym.name) = sym
		_lastId = scala.math.max(sym.id, _lastId)
	}

	printf("Loaded %,d symbols: %s%n", _all.size, _all.values.mkString(" "))


	// get an existing symbol, or create it
	def apply(name:String):Sym
	  = apply(name, name)


	// get an existing symbol, or create it
	def apply(name:String, alt:String):Sym = {
		if (_all.contains(name)) {
			return _all(name) ;
		}

		// else try to scrape it
		printf("%s not found - attempting to create%n", name)
		val scraper = new Scraper(alt)
		Disaster If (scraper.elements.isEmpty, "invalid symbol: " + alt)

		// newly requested symbol
		_lastId += 1
		val sym = new Sym(_lastId, name, alt)
		Sym.write(sym)
		for (es <- scraper.elements) {
			DailyData.create(sym, es).write
		}

		_all(sym.name) = sym

		sym
	}


	def all = _all.values


	// persist a new symbol
	private def write(sym:Sym) {
		Disaster If _all.contains(sym.name)
		DB.update("insert into fin_sym (sym_id, name, alt) values (%d, %s, %s)"
			format (sym.id, DB.quote(sym.name), DB.quote(sym.alt)))
		_all(sym.name) = sym
	}


	private def refresh(sym:Sym) {
		val curr = DailyData(sym,false) // ha ha - a bit stupid ... possibly infinite recursion.
		for (es <- new Scraper(sym.alt).elements) {
			val dd = DailyData.create(sym, es)
			if (!curr.contains(dd.date)) {
				println("++ " + dd)
				curr(dd.date) = dd
				dd.write
			}
		}
	}


	def refreshAll {
		for (sym <- Sym.all) {
			println("Refreshing " + sym)
			sym.refresh
		}
	}
}


object SymLoader extends App {
//  Sym("CHFGBP", "CHFGBP=X")
  Sym.refreshAll
}

