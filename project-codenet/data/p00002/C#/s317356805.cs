using System;
using System.IO;
using System.Linq;

class Program
{
    static void Main()
    {
        string line;
        while((line = Console.ReadLine()) != null)
        {
            string[] tokens = line.Split(' ');
            int a = int.Parse(tokens[0]);
            int b = int.Parse(tokens[1]);
            Console.WriteLine((a + b).ToString().Length);
        }
    }
}