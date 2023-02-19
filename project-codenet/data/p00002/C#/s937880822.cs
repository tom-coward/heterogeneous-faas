using System;
using System.Linq;

class Program {
    private static int Main() {
        while( true ) {
            var input = Console.ReadLine();
            if( string.IsNullOrEmpty( input ) ) { break; }

            var values = input.Split( ' ' );
            var sum = values.Select( _ => int.Parse( _ ) ).Sum();
            var digit = sum.ToString().Length;

            Console.WriteLine( digit );
        }

        return 0;
    }
}