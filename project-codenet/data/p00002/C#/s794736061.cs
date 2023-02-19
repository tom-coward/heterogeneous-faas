using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Programminng
{
    class AOJ_Volume0002
    {
        static void Main(string[] args)
        {
            while (true)
            {
                String i = Console.ReadLine();
                if (i != null)
                {
                    string[] values = i.Split(' ');
                    int value = int.Parse(values[0]);
                    int value_2 = int.Parse(values[1]);
                    int sum = value + value_2;

                    Console.WriteLine(sum.ToString().Length);
                }
                else
                {
                    break;
                }
            }
        }
    }
}