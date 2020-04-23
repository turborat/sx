package fin.data

import test.TestX

class SymRefresher extends TestX
{
	def testRefresh {
		Sym.refreshAll
	}
}
