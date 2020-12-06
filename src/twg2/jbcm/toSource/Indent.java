package twg2.jbcm.toSource;

import java.util.ArrayList;

/**
 * @author TeamworkGuy2
 * @since 2020-12-05
 */
public class Indent {
	protected String indentMark;
	protected ArrayList<String> indentCache;
	protected int indent;


	public Indent(String indentMark) {
		this.indentMark = indentMark;
		this.indentCache = new ArrayList<>();
		this.indentCache.add(""); // for indent = 0
		this.indent = 0;
	}


	public void indent() {
		this.indent++;
		int cacheSize = this.indentCache.size();
		if(cacheSize <= this.indent) {
			var indent = cacheSize == 0 ? indentMark : this.indentCache.get(cacheSize - 1) + indentMark;
			this.indentCache.add(indent);
		}
	}


	public void dedent() {
		this.indent--;
	}


	public String getIndent() {
		return this.indentCache.get(this.indent);
	}


	public StringBuilder writeTo(StringBuilder src) {
		src.append(this.indentCache.get(this.indent));

		return src;
	}


	@Override
	public String toString() {
		return getIndent();
	}


	/**
	 * @return the indent if {@code #dedent()} was called
	 */
	public String toDedent() {
		return this.indentCache.get(this.indent - 1);
	}


	/**
	 * @return the indent if {@code #indent()} was called
	 */
	public String toIndent() {
		int cacheSize = this.indentCache.size();
		if(cacheSize <= this.indent + 1) {
			var indent = cacheSize == 0 ? indentMark : this.indentCache.get(cacheSize - 1) + indentMark;
			this.indentCache.add(indent);
		}
		return this.indentCache.get(this.indent + 1);
	}

}
