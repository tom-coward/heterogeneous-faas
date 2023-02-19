using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApplication
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                var str = Console.ReadLine();
                if (string.IsNullOrEmpty(str)) return;
                Console.WriteLine(str.Split(' ').Select(s => int.Parse(s)).Sum().ToString().Length);
            }
        }
    }
}