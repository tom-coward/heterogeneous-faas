using System;
using System.Collections.Generic;
using System.Linq;

namespace AOJ
{
  class Program
  {
    public static void Main(string[] args)
    {
      foreach (var ss in ReadNumbers())
        Console.WriteLine(ss.Sum().ToString().Length);
    }

    static IEnumerable<IEnumerable<int>> ReadNumbers()
    {
      string line;
      while ((line = Console.ReadLine()) != null)
        yield return line.Split(' ').Select(s => int.Parse(s));
    }
  }
}