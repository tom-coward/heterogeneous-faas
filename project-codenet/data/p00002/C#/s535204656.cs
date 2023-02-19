using System;
using System.Collections.Generic;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Text;

namespace AOJ
{
    class Program
    {
        static void Main(string[] args)
        {
            foreach (var s in EnumerateInput()
                .Select(s => s.Split(' ')
                    .Select(int.Parse).Sum()
                    .ToString()
                    .Length))
            {
                Console.WriteLine(s);
            }
        }

        static IEnumerable<string> EnumerateInput()
        {
            var s = "";
            while ((s = Console.ReadLine()) != null)
            {
                yield return s;
            }
        }
    }
}