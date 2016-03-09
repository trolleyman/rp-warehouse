package warehouse.nxt.utils;

public class MyString {

	public MyString() {  }
	
	public static String[] split( String _separator, String _input ) {
		
		String[] output = {  };
		
		try {
	         for( int i = 0; i <= _input.length(); i++ ) {
	             int c = _input.indexOf( _separator ) - 1;
	             output[i] = _input.substring( 0, c );
	             _input = _input.substring( c + 1 );
	          }
	         
	         return output;
		}
		catch( Exception _exception ) { return null; }
	}
	
}
