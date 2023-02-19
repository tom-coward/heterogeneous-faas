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
            string s;
            while (!String.IsNullOrEmpty(s = Console.ReadLine()))
            {
                Console.WriteLine(
                    s.Split(new[] { " " }, StringSplitOptions.RemoveEmptyEntries)
                     .Select(int.Parse)
                     .Aggregate((a, b) => a + b).ToString().Length);
            }
        }
    }
}