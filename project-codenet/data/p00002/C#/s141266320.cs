using System;
using System.Collections.Generic;
using System.Linq;

namespace Problem0002
{
    class Program
    {
        static void Main(string[] args)
        {
            var sums =
                GetInputLines()
                .Select(line => line.Split().Select(word => int.Parse(word)).Sum());
            var lengths = sums.Select(sum => sum.ToString().Length);
            foreach (int length in lengths)
            {
                Console.WriteLine(length);
            }
        }

        static IEnumerable<string> GetInputLines()
        {
            string line;
            while (!string.IsNullOrEmpty(line = System.Console.ReadLine()))
            {
                yield return line;
            }
        }
    }
}