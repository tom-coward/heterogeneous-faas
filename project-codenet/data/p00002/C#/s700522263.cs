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
			while( true ) {
				var	s	= Console.ReadLine();
				if( s == null ) {
					break;
				}
				var	ss	= s.Split();
				var	sum	= int.Parse( ss[ 0 ] ) + int.Parse( ss[ 1 ] );
				Console.WriteLine( sum.ToString().Length );
			}
		}
	}
}