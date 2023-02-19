using System;
using System.Collections.Generic;
using System.Text;

namespace mondai0002
{
    class Program
    {
        static void Main(string[] args)
        {
            string input;
            int a = 0;
            int b = 0;
            int sum = 0;

            while (true)
            {
                input = Console.ReadLine();

                if (input == null || input.Equals(""))
                {
                    break;
                }

                a = int.Parse(input.Split(' ')[0]);
                b = int.Parse(input.Split(' ')[1]);
                sum = a + b;
                Console.WriteLine(sum.ToString().Length);
            }

        }
    }
}