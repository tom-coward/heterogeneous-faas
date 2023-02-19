using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

class Program
{
    static void Main(string[] args)
    {
        while (true)
        {
            var line = Console.ReadLine();
            if (string.IsNullOrEmpty(line))
            {
                break;
            }
            var m = Regex.Match(line, @"^(?<x>\d+)\s+(?<y>\d+)$");
            if (!m.Success)
            {
                break;
            }
            var value = int.Parse(m.Groups["x"].Value) + int.Parse(m.Groups["y"].Value);
            Console.WriteLine(value.ToString().Length);
        }
    }
}