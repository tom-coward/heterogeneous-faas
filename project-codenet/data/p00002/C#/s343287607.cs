using System;
using System.Collections.Generic;
using System.Linq;

namespace JagArrayTest
{
    class Program
    {
        static void Main(string[] args)
        {
            while(true)
            {
                var readline = Console.ReadLine();

                if(readline == null)
                {
                    break;
                }

                var d = readline.Split(' ').ToList().ConvertAll(double.Parse).ToArray();
                Console.WriteLine((d[0] + d[1]).ToString().Length);
            }

        }
    }
}
