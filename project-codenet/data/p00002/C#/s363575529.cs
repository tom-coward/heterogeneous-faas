using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace mondai0002
{
	class Program
	{
		static void Main(string[] args)
		{
			string	s	= "";
			while( true ) {
				var	c	= Console.Read();
				if( c == -1 ) {
					break;
				}
				s	+= (char)c;
			}
			
			var	ss	= s.Split( new char[ 0 ], StringSplitOptions.RemoveEmptyEntries );
			var	num	= Array.ConvertAll( ss, sss => int.Parse( sss ) );
			for( int i = 0; i < num.Length; i += 2 ) {
				int	sum	= num[ i ] + num[ i + 1 ];
				var	str	= sum.ToString();
				Console.WriteLine( str.Length );
			}
		}
	}
}