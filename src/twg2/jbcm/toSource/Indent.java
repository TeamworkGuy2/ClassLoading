package twg2.jbcm.toSource;

import java.util.ArrayList;

/**
 * @author TeamworkGuy2
 * @since 2020-12-05
 */
public interface Indent {

	public void indent();

	public void dedent();

	public int getCount();

	public String getIndent();

	public StringBuilder writeTo(StringBuilder src);

	/**
	 * @return the indent if {@code dedent()} was called
	 */
	public String toDedent();

	/**
	 * @return the indent if {@code indent()} was called
	 */
	public String toIndent();

	@Override
	public String toString();


	/**
	 * @author TeamworkGuy2
	 * @since 2022-10-01
	 */
	public static class Impl implements Indent {
		protected String indentMark;
		protected ArrayList<String> indentCache;
		protected int indent;


		public Impl(String indentMark) {
			this.indentMark = indentMark;
			this.indentCache = new ArrayList<>();
			this.indentCache.add(""); // for indent = 0
			this.indent = 0;
		}


		@Override
		public void indent() {
			this.indent++;
			int cacheSize = this.indentCache.size();
			if(cacheSize <= this.indent) {
				var indent = cacheSize == 0 ? indentMark : this.indentCache.get(cacheSize - 1) + indentMark;
				this.indentCache.add(indent);
			}
		}


		@Override
		public void dedent() {
			this.indent--;
		}


		@Override
		public int getCount() {
			return this.indent;
		}


		@Override
		public String getIndent() {
			return this.indentCache.get(this.indent);
		}


		@Override
		public StringBuilder writeTo(StringBuilder src) {
			src.append(this.indentCache.get(this.indent));

			return src;
		}


		@Override
		public String toString() {
			return getIndent();
		}


		/**
		 * @return the indent if {@code dedent()} was called
		 */
		@Override
		public String toDedent() {
			return this.indentCache.get(this.indent - 1);
		}


		/**
		 * @return the indent if {@code indent()} was called
		 */
		@Override
		public String toIndent() {
			int cacheSize = this.indentCache.size();
			if(cacheSize <= this.indent + 1) {
				var indent = cacheSize == 0 ? indentMark : this.indentCache.get(cacheSize - 1) + indentMark;
				this.indentCache.add(indent);
			}
			return this.indentCache.get(this.indent + 1);
		}
	}
}
