package twg2.jbcm.toSource;

public class StringBuilderIndent implements CharSequence, Indent {
	public final StringBuilder sb;
	public final Indent indent;

	public StringBuilderIndent(StringBuilder strBuilder, Indent indent) {
		sb = strBuilder;
		this.indent = indent;
	}

	/**
	 * @see StringBuilder#StringBuilder(int)
	 */
	public StringBuilderIndent(int capacity, Indent indent) {
		sb = new StringBuilder(capacity);
		this.indent = indent;
	}

	/**
	 * @see StringBuilder#StringBuilder(String)
	 */
	public StringBuilderIndent(String str, Indent indent) {
		this(str.length() + 16, indent);
		append(str);
	}

	/**
	 * @see StringBuilder#StringBuilder(CharSequence)
	 */
	public StringBuilderIndent(CharSequence seq, Indent indent) {
		this(seq.length() + 16, indent);
		append(seq);
	}

	/**
	 * @see StringBuilder#append(Object)
	 */
	public StringBuilderIndent append(Object obj) {
		return append(String.valueOf(obj));
	}

	/**
	 * @see StringBuilder#append(String)
	 */
	public StringBuilderIndent append(String str) {
		sb.append(str);
		return this;
	}

	/**
	 * @see StringBuilder#append(StringBuffer)
	 */
	public StringBuilderIndent append(StringBuffer sb) {
		sb.append(sb);
		return this;
	}

	/**
	 * @see StringBuilder#append(CharSequence)
	 */
	public StringBuilderIndent append(CharSequence s) {
		sb.append(s);
		return this;
	}

	/**
	 * @see StringBuilder#append(CharSequence, int, int)
	 */
	public StringBuilderIndent append(CharSequence s, int start, int end) {
		sb.append(s, start, end);
		return this;
	}

	/**
	 * @see StringBuilder#append(char[])
	 */
	public StringBuilderIndent append(char[] str) {
		sb.append(str);
		return this;
	}

	/**
	 * @see StringBuilder#append(char[], int, int)
	 */
	public StringBuilderIndent append(char[] str, int offset, int len) {
		sb.append(str, offset, len);
		return this;
	}

	/**
	 * @see StringBuilder#append(boolean)
	 */
	public StringBuilderIndent append(boolean b) {
		sb.append(b);
		return this;
	}

	/**
	 * @see StringBuilder#append(char)
	 */
	public StringBuilderIndent append(char c) {
		sb.append(c);
		return this;
	}

	/**
	 * @see StringBuilder#append(int)
	 */
	public StringBuilderIndent append(int i) {
		sb.append(i);
		return this;
	}

	/**
	 * @see StringBuilder#append(long)
	 */
	public StringBuilderIndent append(long lng) {
		sb.append(lng);
		return this;
	}

	/**
	 * @see StringBuilder#append(float)
	 */
	public StringBuilderIndent append(float f) {
		sb.append(f);
		return this;
	}

	/**
	 * @see StringBuilder#append(double)
	 */
	public StringBuilderIndent append(double d) {
		sb.append(d);
		return this;
	}

	/**
	 * @see StringBuilder#appendCodePoint(int)
	 */
	public StringBuilderIndent appendCodePoint(int codePoint) {
		sb.appendCodePoint(codePoint);
		return this;
	}

	/**
	 * @see StringBuilder#delete(int, int)
	 */
	public StringBuilderIndent delete(int start, int end) {
		sb.delete(start, end);
		return this;
	}

	/**
	 * @see StringBuilder#deleteCharAt(int)
	 */
	public StringBuilderIndent deleteCharAt(int index) {
		sb.deleteCharAt(index);
		return this;
	}

	/**
	 * @see StringBuilder#replace(int, int, String)
	 */
	public StringBuilderIndent replace(int start, int end, String str) {
		sb.replace(start, end, str);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, char[], int, int)
	 */
	public StringBuilderIndent insert(int index, char[] str, int offset, int len) {
		sb.insert(index, str, offset, len);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, Object)
	 */
	public StringBuilderIndent insert(int offset, Object obj) {
		sb.insert(offset, obj);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, String)
	 */
	public StringBuilderIndent insert(int offset, String str) {
		sb.insert(offset, str);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, char[])
	 */
	public StringBuilderIndent insert(int offset, char[] str) {
		sb.insert(offset, str);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, CharSequence)
	 */
	public StringBuilderIndent insert(int dstOffset, CharSequence s) {
		sb.insert(dstOffset, s);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, CharSequence, int, int)
	 */
	public StringBuilderIndent insert(int dstOffset, CharSequence s, int start, int end)
	{
		sb.insert(dstOffset, s, start, end);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, boolean)
	 */
	public StringBuilderIndent insert(int offset, boolean b) {
		sb.insert(offset, b);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, char)
	 */
	public StringBuilderIndent insert(int offset, char c) {
		sb.insert(offset, c);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, int)
	 */
	public StringBuilderIndent insert(int offset, int i) {
		sb.insert(offset, i);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, long)
	 */
	public StringBuilderIndent insert(int offset, long l) {
		sb.insert(offset, l);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, float)
	 */
	public StringBuilderIndent insert(int offset, float f) {
		sb.insert(offset, f);
		return this;
	}

	/**
	 * @see StringBuilder#insert(int, double)
	 */
	public StringBuilderIndent insert(int offset, double d) {
		sb.insert(offset, d);
		return this;
	}

	/**
	 * @see StringBuilder#indexOf(String)
	 */
	public int indexOf(String str) {
		return sb.indexOf(str);
	}

	/**
	 * @see StringBuilder#indexOf(String, int)
	 */
	public int indexOf(String str, int fromIndex) {
		return sb.indexOf(str, fromIndex);
	}

	/**
	 * @see StringBuilder#lastIndexOf(String)
	 */
	public int lastIndexOf(String str) {
		return sb.lastIndexOf(str);
	}

	/**
	 * @see StringBuilder#lastIndexOf(String, int)
	 */
	public int lastIndexOf(String str, int fromIndex) {
		return sb.lastIndexOf(str, fromIndex);
	}

	/**
	 * @see StringBuilder#reverse()
	 */
	public StringBuilderIndent reverse() {
		sb.reverse();
		return this;
	}

	/**
	 * @see StringBuilder#length()
	 */
	@Override
	public int length() {
		return sb.length();
	}

	/**
	 * @see StringBuilder#charAt(int)
	 */
	@Override
	public char charAt(int index) {
		return sb.charAt(index);
	}

	/**
	 * @see StringBuilder#subSequence(int, int)
	 */
	@Override
	public CharSequence subSequence(int start, int end) {
		return sb.subSequence(start, end);
	}

	@Override
	public String toString() {
		return sb.toString();
	}


	// implement Indent

	@Override
	public void indent() {
		indent.indent();
	}

	@Override
	public void dedent() {
		indent.dedent();
	}

	@Override
	public int getCount() {
		return indent.getCount();
	}

	@Override
	public String getIndent() {
		return indent.getIndent();
	}

	@Override
	public StringBuilder writeTo(StringBuilder src) {
		return indent.writeTo(src);
	}

	@Override
	public String toDedent() {
		return indent.toDedent();
	}

	@Override
	public String toIndent() {
		return indent.toIndent();
	}

	public StringBuilderIndent appendIndent() {
		this.append(this.indent);
		return this;
	}
}