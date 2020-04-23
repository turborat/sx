package exp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ExpJ {

	interface ListX<T> extends List<T> {
		default void xform(Function<T,T> f) {
//			System.out.println(f);
			for (int i=0 ; i<size() ; ++i)
				set(i, f.apply(get(i)));
		}
	}

	static class ArrayListX<T> extends ArrayList<T> implements ListX<T> {

		static <T> ArrayListX<T> of(T...t) {
			ArrayListX<T> ret = new ArrayListX<>();
			ret.addAll(Arrays.asList(t));
			return ret;
		}

	}

	ExpJ() {
		ListX<Integer> l = ArrayListX.of(1,2,3);
		System.out.println(l);

		while (true) {
			l.xform(i -> i * i);
//			System.out.println(l);
		}
	}

	public static void main(String[] args) {
		new ExpJ() ;
	}




}
