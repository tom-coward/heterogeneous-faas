using System;
using System.Linq;
 
public class Program
{
    private static void Main(string[] args)
    {
        string line;
        while (!string.IsNullOrEmpty(line = Console.ReadLine()))
        {
            Console.WriteLine(line.Split().Select(int.Parse).Sum().ToString().Length);
        }
    }
}