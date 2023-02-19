using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApplication4
{
  class Program
  {
    static void Main(string[] args)
    {
      string input;
      int sum = 0, digit = 1;
      while ( (input = Console.ReadLine()) != null)
      {
        foreach (string num in input.Split())
        {
          sum += int.Parse(num);
        }
        while ((sum /= 10) >= 1)
        {
          digit++;
        }
        Console.WriteLine(digit);
        digit = 1;
        sum = 0;
      }
      return;
    }
  }
}