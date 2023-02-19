using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AthleteProgramming
{
    class Program
    {
        static void Main()
        {

            while (true)
            {
                string str = Console.ReadLine();
                if (str == null)
                {
                    break;
                }
                int[] ab = str.Split(' ').Select(int.Parse).ToArray();
                int sum = ab[0] + ab[1];
                bool b = true;
                int i = 1;
                int count = 1;
                while (b)
                {
                    if (sum / i < 1)
                    {
                        b = false;
                        count -= 1;
                        Console.WriteLine(count);
                    }else
                    {
                        i *= 10;
                        count++;
                    }
                }
            }
        }

    }
}