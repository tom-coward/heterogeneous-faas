using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DigitNumber
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                var str = Console.ReadLine();

                if(str == null)
                { 
                    break;
                }
                
                var sArray = str.Split();

                var sum = int.Parse(sArray[0]) + int.Parse(sArray[1]);

                Console.WriteLine(check(sum));
            }
        }

        static int check(int n)
        {
            int i;
            for (i = 1; n >= 10; i++, n /= 10)
            {
                
            }
            return i;
        }

    }
}