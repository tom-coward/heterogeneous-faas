using System;
using System.Linq;

class Program
{
    static void Main()
    {
        string input;
        while ((input = Console.ReadLine()) != null)
        {
            var t = input.Split().Select(int.Parse).Aggregate((a, b) => a + b).ToString();
            Console.WriteLine(t.Length);
        }
    }
}