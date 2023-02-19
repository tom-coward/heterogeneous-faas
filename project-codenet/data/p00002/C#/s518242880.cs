using System;
using System.Collections.Generic;
using System.Linq;

namespace ConsoleApp1
{
    class Program
    {
        static void Main(string[] args)
        {
            string str;
            List<int> digit = new List<int>();

            while (!((str = Console.ReadLine()) == null))
            {
                int sum = 0;
                List<int> num = str.Split(' ').Select(int.Parse).ToList();

                sum = num.Sum();
                digit.Add(sum.ToString().Length);
            }

            foreach (int item in digit)
            {
                Console.WriteLine(item);
            }
        }
    }
}

