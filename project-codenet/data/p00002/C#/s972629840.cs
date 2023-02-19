using System;
using System.Linq;

class Program
{
    static void Main()
    {
        string str = "";
        while ((str = Console.ReadLine()) != null)
        {
            int ans = str.Split(' ').Select(int.Parse).Sum().ToString().Length;
            Console.WriteLine(ans);
        }
    }
}
