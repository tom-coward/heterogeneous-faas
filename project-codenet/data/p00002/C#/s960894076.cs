using System;
using System.Linq;
using System.Collections.Generic;
namespace _0002
{
    class Program
    {
        static void Main(string[] args)
        {
            List<int> b = new List<int>();
            string input;
            while(true)
            {
                input = Console.ReadLine();
                if (input == null)
                {
                    break;
                }
                int[] x = input.Split().Select(int.Parse).ToArray();
                b.Add((x[0] + x[1]).ToString().Length);
            }
            foreach (var i in b)
            {
                Console.WriteLine(i);
            }
        }
    }
}