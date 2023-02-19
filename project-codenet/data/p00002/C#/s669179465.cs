using System;
using System.Globalization;

namespace AOJ0002
{
    class Program
    {
        static void Main()
        {
            string line;
            while ((line = Console.ReadLine()) != null)
            {
                var items = line.Split(' ');
                var a = int.Parse(items[0]);
                var b = int.Parse(items[1]);
                var apb = (a + b).ToString(CultureInfo.InvariantCulture);
                Console.WriteLine(apb.Length);
            }
        }
    }
}