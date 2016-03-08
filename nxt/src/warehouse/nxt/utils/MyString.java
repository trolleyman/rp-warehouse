package warehouse.nxt.utils;

public class MyString {

	public MyString() {  }
	
	public static String[] split( String _separator, String _input ) {
		String[] output = {  };
		
		if( _separator == null ) { output[ 0 ] = _input; return output; }
		if( _input == null ) { return null; }
		
		int substring = -1;
		int i = 0;
		
		while( ( substring = _input.indexOf( _separator ) ) != -1 ) {
			output[ i ] = _input.substring( 0, substring ).trim();
			_input = _input.substring( substring + _separator.length() );
			
			i++;
		}
		
		output[ i ] = _input.trim();
		
		return output;
		
	}
	
}
