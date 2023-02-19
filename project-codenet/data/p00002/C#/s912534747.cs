using System;
using System.Collections.Generic;

class Program
{
    static void Main(string[] args)
    {

        while (true)
        {
            string s = Console.ReadLine();
            if (s == null) break;

            string[] sp = s.Split();

            int sum = int.Parse(sp[0]) + int.Parse(sp[1]);

            Console.WriteLine( sum.ToString().Length );


        }

    }
}


