using System;
using System.Linq;

class Volumen0
{
    static void Main()
    {
        string line;
        while ((line = Console.ReadLine()) != null)
        {
            Console.WriteLine(line.Split().Select(int.Parse).Sum().ToString().Length);
        }
    }
}
