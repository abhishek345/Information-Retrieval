
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

// Class used to porpoerly format the output result
public class OutputFormatter 
{
	private final List<String[]>	rows	= new LinkedList<String[]>();

	
	@Override
	public String toString()		//used to adjust the column width
	{
		final StringBuilder buf = new StringBuilder();
		final int[] colWidths = this.colWidths();

		for (final String[] row : this.rows)
		{
			for (int colNum = 0; colNum < row.length; colNum++) 
			{
				buf.append(StringUtils.rightPad(StringUtils.defaultString(row[colNum]), colWidths[colNum]));
				buf.append('\t');
			}

			buf.append('\n');
		}

		return buf.toString();
	}

	private int[] colWidths()		// used to set the column width according to the output data
	{
		int cols = -1;
		for (final String[] row : this.rows) 
		{
			cols = Math.max(cols, row.length);
		}

		final int[] widths = new int[cols];
		for (final String[] row : this.rows) 
		{
			for (int colNum = 0; colNum < row.length; colNum++) 
			{
				widths[colNum] = Math.max(widths[colNum], StringUtils.length(row[colNum]));
			}
		}

		return widths;
	}
	
	public void addRow(final String... cols)		//adds new rows
	{
		this.rows.add(cols);
	}
}