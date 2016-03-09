package warehouse.nxt.utils;

import java.util.ArrayList;

public class MyString {

	public MyString() {  }
	
	public static String[] split( String _separator, String _input ) {
		ArrayList<String> output = new ArrayList<>();
		
		if( _separator == null ) { output.add(_input); return output.toArray(new String[output.size()]); }
		if( _input == null ) { return null; }
		
		int substring = -1;
		int i = 0;
		
		while( ( substring = _input.indexOf( _separator ) ) != -1 ) {
			while (i >= output.size()) {
				output.add("");
			}
			output.set(i, _input.substring( 0, substring ).trim());
			_input = _input.substring( substring + _separator.length() );
			
			i++;
		}
		
		if (i >= output.size()) {
			output.add("");
		}
		output.set(i, _input.trim());
		
		return output.toArray(new String[output.size()]);
		
	}
	
}
