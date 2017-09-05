import java.util.Comparator;
import java.util.Map;

//class used to comapre the strings
public class SortableMap implements Comparator<String> 
{
	private final Map<String, Double> map;

	@Override
	public int compare(final String s1, final String s2) 		//compare two strings
	{
		return this.map.get(s1) >= this.map.get(s2) ? 1 : -1;
	}
	public SortableMap(final Map<String, Double> queryMap) 	//constructor of the class
	{
		this.map = queryMap;
	}
}