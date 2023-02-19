using System;
using System.Collections.Generic;
using System.Linq;

namespace AOJ0002
{
    class Program
    {
        private static void Main(string[] args)
        {
            for (;;)
            {
                var inputStr = Console.ReadLine();
                if (inputStr == null) break;
                

                var q = inputStr.Split(' ');
                var i = q.Sum(s => Convert.ToInt32(s));

                Console.WriteLine(i.ToString().Length);
            }
        }
    }
}