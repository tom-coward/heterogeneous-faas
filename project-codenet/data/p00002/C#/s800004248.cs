using System;
using System.Linq;

class DigitNumber
{
	static void Main()
	{
		var sums =
			from numpair in Enumerable.Range( 0, 1000000 )
				.Select( _ => Console.ReadLine() )
				.TakeWhile( _ => _ != null )
				.Select( _ => _.Split( ' ' ).Select( __ => int.Parse( __ ) ) )
			let a = numpair.First()
			let b = numpair.Last()
			select "" + ( a + b );
		foreach( var sum in sums )
			Console.WriteLine( sum.Length );
	}
}