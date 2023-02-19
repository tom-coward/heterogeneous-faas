using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Prob0002
{
    class Program
    {
        const char splitBy = ' ';

        static void Main(string[] args)
        {
            string line;
            while (!string.IsNullOrEmpty(line = System.Console.ReadLine()))
            {
                string[] ab = line.Split(splitBy);
                int a = Convert.ToInt32(ab[0]);
                int b = Convert.ToInt32(ab[1]);
                Console.WriteLine((int)Math.Log10(a + b) + 1);
            }
        }


    }
}