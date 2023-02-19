using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            string line;
            while ((line = Console.ReadLine()) != null)
            {
                string[] input = line.Split(' ');
                int a = int.Parse(input[0]);
                int b = int.Parse(input[1]);
                string num = "" + (a + b);
                Console.WriteLine(num.Length);
            }
        }
    }
}