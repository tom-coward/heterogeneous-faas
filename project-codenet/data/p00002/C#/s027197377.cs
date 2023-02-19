using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AOJ
{
    class Program
    {
        static void Main(string[] args)
        {
            string line = "";
            while ((line = Console.ReadLine()) != null)
            {
                String[] num = line.Split(' ');
                Console.WriteLine((int.Parse(num[0]) + int.Parse(num[1])).ToString().Length);
            }
        }
    }
}