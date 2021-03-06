/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {
	/**
	 * @sharpen.ignore
	 */
	public static String join(Iterable<?> iter, String separator) {
		return join(iter.iterator(), separator);
	}

	/**
	 * @sharpen.ignore
	 */
	public static <T> String join(T[] array, String separator) {
		return join(Arrays.asList(array), separator);
	}

	/**
	 * @sharpen.ignore
	 */
    public static <T> String join(Iterator<T> iter, String separator) {
        StringBuilder buf = new StringBuilder();
        while ( iter.hasNext() ) {
            buf.append(iter.next());
            if ( iter.hasNext() ) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

	/**
	 * @sharpen.ignore
	 */
	public static boolean equals(Object x, Object y) {
		if (x == y) {
			return true;
		}

		if (x == null || y == null) {
			return false;
		}

		return x.equals(y);
	}

	public static int numNonnull(Object[] data) {
		int n = 0;
		if ( data == null ) return n;
		for (Object o : data) {
			if ( o!=null ) n++;
		}
		return n;
	}

	public  static <T> void removeAllElements(Collection<T> data, T value) {
		if ( data==null ) return;
		while ( data.contains(value) ) data.remove(value);
	}

	public static String escapeWhitespace(String s, boolean escapeSpaces) {
		StringBuilder buf = new StringBuilder();
		for (char c : s.toCharArray()) {
			if ( c==' ' && escapeSpaces ) buf.append('\u00B7');
			else if ( c=='\t' ) buf.append("\\t");
			else if ( c=='\n' ) buf.append("\\n");
			else if ( c=='\r' ) buf.append("\\r");
			else buf.append(c);
		}
		return buf.toString();
	}

	/**
	 * @sharpen.ignore
	 */
	public static void writeFile(@NotNull String fileName, @NotNull String content) throws IOException {
		writeFile(fileName, content, null);
	}

	/**
	 * @sharpen.ignore
	 */
	public static void writeFile(@NotNull String fileName, @NotNull String content, @Nullable String encoding) throws IOException {
		File f = new File(fileName);
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw;
		if (encoding != null) {
			osw = new OutputStreamWriter(fos, encoding);
		}
		else {
			osw = new OutputStreamWriter(fos);
		}

		try {
			osw.write(content);
		}
		finally {
			osw.close();
		}
	}

	/**
	 * @sharpen.ignore
	 */
	@NotNull
	public static char[] readFile(@NotNull String fileName) throws IOException {
		return readFile(fileName, null);
	}

	/**
	 * @sharpen.ignore
	 */
	@NotNull
	public static char[] readFile(@NotNull String fileName, @Nullable String encoding) throws IOException {
		File f = new File(fileName);
		int size = (int)f.length();
		InputStreamReader isr;
		FileInputStream fis = new FileInputStream(fileName);
		if ( encoding!=null ) {
			isr = new InputStreamReader(fis, encoding);
		}
		else {
			isr = new InputStreamReader(fis);
		}
		char[] data = null;
		try {
			data = new char[size];
			int n = isr.read(data);
			if (n < data.length) {
				data = Arrays.copyOf(data, n);
			}
		}
		finally {
			isr.close();
		}
		return data;
	}

	public static <T> void removeAll(@NotNull List<T> list, @NotNull Predicate<? super T> predicate) {
		int j = 0;
		for (int i = 0; i < list.size(); i++) {
			T item = list.get(i);
			if (!predicate.eval(item)) {
				if (j != i) {
					list.set(j, item);
				}

				j++;
			}
		}

		if (j < list.size()) {
			list.subList(j, list.size()).clear();
		}
	}

	/**
	 * @sharpen.ignore
	 */
	public static <T> void removeAll(@NotNull Iterable<T> iterable, @NotNull Predicate<? super T> predicate) {
		if (iterable instanceof List<?>) {
			removeAll((List<T>)iterable, predicate);
			return;
		}

		for (Iterator<T> iterator = iterable.iterator(); iterator.hasNext(); ) {
			T item = iterator.next();
			if (predicate.eval(item)) {
				iterator.remove();
			}
		}
	}

	/** Convert array of strings to string&rarr;index map. Useful for
	 *  converting rulenames to name&rarr;ruleindex map.
	 */
	public static Map<String, Integer> toMap(String[] keys) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (int i=0; i<keys.length; i++) {
			m.put(keys[i], i);
		}
		return m;
	}

	public static char[] toCharArray(IntegerList data) {
		if ( data==null ) return null;
		char[] cdata = new char[data.size()];
		for (int i=0; i<data.size(); i++) {
			cdata[i] = (char)data.get(i);
		}
		return cdata;
	}

	/**
	 * @since 4.5
	 */
	@NotNull
	public static IntervalSet toSet(@NotNull BitSet bits) {
		IntervalSet s = new IntervalSet();
		int i = bits.nextSetBit(0);
		while ( i >= 0 ) {
			s.add(i);
			i = bits.nextSetBit(i+1);
		}
		return s;
	}
}
